package thesonid.com.bakingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer2.ExoPlayer;

import java.net.URL;

import thesonid.com.bakingapp.utilities.NetworkUtils;
import thesonid.com.bakingapp.utilities.OpenVideoUtils;


public class StepActivity extends AppCompatActivity {
    private String shortDescription, title,description,videouri,imagethumb;
    private FragmentManager fragmentManager;
    private MediaFragment mediaFragment;
    private static final String POSITION ="position" ;
    private static final String VIDEO ="video" ;
    private static final String THUMB ="thumb" ;
    private static final String DESCRIPTION ="description" ;
    private int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_activity);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                shortDescription = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                title = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                new FetchVideo().execute();
            }
        }
        fragmentManager = getSupportFragmentManager();
        mediaFragment = new MediaFragment();
        if (savedInstanceState!=null){
            Log.v("no",shortDescription);
            Log.v("no",title);
            getSupportFragmentManager().beginTransaction().remove(mediaFragment).commit();
        }
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int pos=mediaFragment.getCurrentPosition();
        outState.putInt(POSITION,pos);
        outState.putString(VIDEO,videouri);
        outState.putString(DESCRIPTION,description);
        outState.putString(THUMB,imagethumb);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt(POSITION);
        String video=savedInstanceState.getString(VIDEO);
        String description=savedInstanceState.getString(DESCRIPTION);
        String thumb=savedInstanceState.getString(THUMB);
        Bundle bundle = new Bundle();
        bundle.putString("video",video);
        bundle.putString("description",description);
        bundle.putString("image",thumb);
        bundle.putString("reset","yes");
        bundle.putInt("position_video",position);
        mediaFragment = new MediaFragment();
        mediaFragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(R.id.container_steps,mediaFragment)
                .commit();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaFragment.getPlayer()!=null){
        releasePlayer(mediaFragment.getPlayer());}
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaFragment.getPlayer()!=null){
        releasePlayer(mediaFragment.getPlayer());}
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaFragment.getPlayer()!=null){
        releasePlayer(mediaFragment.getPlayer());}
    }


    private void releasePlayer(ExoPlayer mExoPlayer) {
        mExoPlayer.stop();
        mExoPlayer.release();
    }



    public class FetchVideo extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {

            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);

                String[] recipesData = OpenVideoUtils
                        .getVideoFromJson(StepActivity.this, jsonResponse,shortDescription,title);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] data) {
            if (data[0] != null && data[0]!="-") {
                videouri=data[0];
            }else{
                videouri="-";
            }
            if (data[1] != null && data[1]!="-") {
                description=data[1];
            }
            if (data[2] != null && data[2]!="-") {
                imagethumb=data[2];
            }
            if (videouri!=null && description!=null ){
                Bundle bundle = new Bundle();
                bundle.putString("video",videouri);
                bundle.putString("description",description);
                bundle.putString("image",imagethumb);
                bundle.putString("reset","no");
                mediaFragment = new MediaFragment();
                mediaFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container_steps,mediaFragment)
                        .commit();
            }
        }
    }
}
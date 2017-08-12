package thesonid.com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.net.URL;

import thesonid.com.bakingapp.utilities.NetworkUtils;
import thesonid.com.bakingapp.utilities.OpenDetailsUtils;
import thesonid.com.bakingapp.utilities.OpenStepUtils;
import thesonid.com.bakingapp.utilities.OpenVideoUtils;


public class DetailsActivity extends AppCompatActivity implements IngridientsAdapter.RecipesAdapterOnClickHandler {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private IngridientsAdapter mIngredientsAdapter;
    private String title,videouri,description,shortDescription;
    private TextView mIngridients;
    private boolean tabletSize;
    private MediaFragment mediaFragment;
    private FragmentManager fragmentManager;
    private ScrollView mScrollView;
    Parcelable mListState1;
    private static final String LIST_STATE_KEY_1 ="menu_1" ;
    private static final String POSITION_1 ="position1" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_activity);
        mScrollView=(ScrollView)findViewById(R.id.scrollView_details);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_details);

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(layoutManager);
        mIngredientsAdapter = new IngridientsAdapter(this);
        mRecyclerView.setAdapter(mIngredientsAdapter);

        mIngridients = (TextView) findViewById(R.id.ingridients_text);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                title = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                new FetchIngredientsTask().execute();
            }
        }
        tabletSize = getResources().getBoolean(R.bool.isTablet);
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
        mListState1 = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY_1, mListState1);
        int mScrollPosition1=0;
        SavedState newState1 = new SavedState(mListState1);
        newState1.mScrollPosition = mScrollPosition1;
        outState.putInt(POSITION_1,mScrollPosition1);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        mListState1 = savedInstanceState.getParcelable(LIST_STATE_KEY_1);
        final int mScrollPosition1=savedInstanceState.getInt(POSITION_1);

        if(position != null)
            new Thread(){
                public void run(){
                    try{
                        Thread.sleep(500);
                    }
                    catch(Exception ex){}
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.scrollTo(position[0], position[1]);
                            mRecyclerView.scrollToPosition(mScrollPosition1);
                        }
                    });
                }
            }.start();
    }


    private void showIngedientsDataView(String data) {
        mIngridients.setText(data);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String data) {
        if (!tabletSize) {
            Context context = this;
            Class destinationClass = StepActivity.class;
            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, data);
            intentToStartDetailActivity.putExtra(Intent.EXTRA_PHONE_NUMBER, title);
            startActivity(intentToStartDetailActivity);
        } else {
            shortDescription=data;
            mediaFragment=new MediaFragment();
            fragmentManager = getSupportFragmentManager();
            new FetchVideo().execute();
        }
    }

    public class FetchIngredientsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);

                String recipesData = OpenDetailsUtils
                        .getIngridientsFromJson(DetailsActivity.this, jsonResponse, title);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String data) {
            if (data != null) {
                showIngedientsDataView(data);
                new FetchStepsTask().execute();
            }
        }
    }

    public class FetchStepsTask extends AsyncTask<Void, Void, String[]> {

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

                String[] recipesData = OpenStepUtils
                        .getSteps(DetailsActivity.this, jsonResponse, title);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                mIngredientsAdapter.setRecipeData(data);
            }
        }
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
                        .getVideoFromJson(DetailsActivity.this, jsonResponse,shortDescription,title);
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
            if (videouri!=null && description!=null ){
                Bundle bundle = new Bundle();
                bundle.putString("video",videouri);
                bundle.putString("description",description);
                mediaFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .add(R.id.container_steps,mediaFragment)
                        .commit();
            }
        }
    }


    static class SavedState extends android.view.View.BaseSavedState {
        public int mScrollPosition;
        SavedState(Parcel in) {
            super(in);
            mScrollPosition = in.readInt();
        }
        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mScrollPosition);
        }
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

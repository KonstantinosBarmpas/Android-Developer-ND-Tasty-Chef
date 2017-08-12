package thesonid.com.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import thesonid.com.bakingapp.utilities.NetworkUtils;
import thesonid.com.bakingapp.utilities.OpenImageUtils;
import thesonid.com.bakingapp.utilities.OpenRecipesUtil;

import static android.R.attr.data;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements RecipesAdapter.RecipesAdapterOnClickHandler {
    private ProgressBar mLoadingIndicator;
    private RecipesAdapter mRecipesAdapter;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private GridLayoutManager layoutManager1;
    private LinearLayoutManager layoutManager2;
    private LinearLayoutManager layoutManager;
    private static final String LIST_STATE_KEY ="menu" ;
    private static final String STATE_KEY ="state_of_movie" ;
    private static final String STATE_KEY_SCROLL ="scroll" ;
    private static final String POSITION ="position" ;
    private String menu=null;
    Parcelable mListState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator=(ProgressBar) findViewById(R.id.loading_indicator);
        mRecyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        mErrorMessageDisplay=(TextView)findViewById(R.id.error_message_display);


        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            layoutManager1=new GridLayoutManager(this,2);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(layoutManager1);
            layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager=layoutManager1;
        } else {
            layoutManager2=new LinearLayoutManager(this);
            layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
            layoutManager=layoutManager2;
            mRecyclerView.setLayoutManager(layoutManager2);
        }

        mRecipesAdapter = new RecipesAdapter(this);
        mRecyclerView.setAdapter(mRecipesAdapter);
        loadRecipeData();

    }

    private void loadRecipeData() {
        showRecipeDataView();
        new FetchRecipesTask().execute();
    }

    private void showRecipeDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String data) {
        Context context = this;
        Class destinationClass = DetailsActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, data);
        startActivity(intentToStartDetailActivity);
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        mListState = layoutManager.onSaveInstanceState();
        int mScrollPosition=0;
        if(layoutManager != null && layoutManager instanceof LinearLayoutManager){
            mScrollPosition = layoutManager.findFirstVisibleItemPosition();
        }
        SavedState newState = new SavedState(mListState);
        newState.mScrollPosition = mScrollPosition;
        state.putParcelable(LIST_STATE_KEY, mListState);
        state.putString(STATE_KEY,menu);
        state.putParcelable(STATE_KEY_SCROLL,newState);
        state.putInt(POSITION,mScrollPosition);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state != null) {
            mListState = state.getParcelable(LIST_STATE_KEY);
            menu=state.getString(STATE_KEY);
            Parcelable stateNew=state.getParcelable(STATE_KEY_SCROLL);
            if(stateNew != null && stateNew instanceof SavedState ){
                final int mScrollPosition = ((SavedState) stateNew).mScrollPosition;
                if(layoutManager != null){
                    int count = layoutManager.getChildCount();
                    if( mScrollPosition != RecyclerView.NO_POSITION && mScrollPosition >= count){

                        new Thread(){
                            public void run(){
                                try{
                                    Thread.sleep(500);
                                }
                                catch(Exception ex){}
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mRecyclerView != null) {
                                            mRecyclerView.scrollToPosition(mScrollPosition);
                                        }
                                    }
                                });
                            }
                        }.start();

                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public class FetchRecipesTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(Void... params) {

            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);

                String[] recipesData = OpenRecipesUtil
                        .getRecipesFromJson(MainActivity.this, jsonResponse);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                new FetchImageTask().execute();
                showRecipeDataView();
                mRecipesAdapter.setRecipeData(data);
            } else {
                showErrorMessage();
            }
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    public class FetchImageTask extends AsyncTask<Void, Void, String[]> {

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

                String[] recipesData = OpenImageUtils
                        .getImageFromJson(MainActivity.this, jsonResponse);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                mRecipesAdapter.setImageData(data);
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

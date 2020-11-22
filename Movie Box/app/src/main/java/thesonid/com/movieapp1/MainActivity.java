package thesonid.com.movieapp1;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import thesonid.com.movieapp1.MovieAdapter.MovieAdapterOnClickHandler;
import thesonid.com.movieapp1.utilities.NetworkUtils;
import thesonid.com.movieapp1.utilities.OpenMovieUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler{

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String LIST_STATE_KEY ="menu" ;
    private static final String STATE_KEY ="state_of_movie" ;
    private static final String STATE_KEY_SCROLL ="scroll" ;
    private static final String POSITION ="position" ;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    GridLayoutManager layoutManager;
    Parcelable mListState;
    private String menu="popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message_display);

        layoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loading_indicator);
        if (savedInstanceState!=null){
            menu=savedInstanceState.getString(STATE_KEY);
            mListState=savedInstanceState.getParcelable(LIST_STATE_KEY);
        }
        loadMovieData(menu);
    }


    private void loadMovieData(String Query) {
        showMovieDataView();
        new FetchMovieTask().execute(Query);
    }

    private void showMovieDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String movieForThis) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, movieForThis);
        startActivity(intentToStartDetailActivity);
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.v(TAG, "on save");
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
        Log.v(TAG, String.valueOf(mScrollPosition));
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        if(state != null) {
            Log.v(TAG, "on restore");
            mListState = state.getParcelable(LIST_STATE_KEY);
            menu=state.getString(STATE_KEY);
            Parcelable stateNew=state.getParcelable(STATE_KEY_SCROLL);
            if(stateNew != null && stateNew instanceof SavedState ){
                Log.v(TAG, "stateNew not null");
                final int mScrollPosition = ((SavedState) stateNew).mScrollPosition;
                if(layoutManager != null){
                    Log.v(TAG, "layout not null");
                    int count = layoutManager.getChildCount();
                    if( mScrollPosition != RecyclerView.NO_POSITION && mScrollPosition >= count){
                        Log.v(TAG, String.valueOf(mScrollPosition));
                        Log.v(TAG, "--");
                        Log.v(TAG, String.valueOf(count));
                        Log.v(TAG, "--");
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
        Log.v(TAG, "resume");
        //if (mListState != null) {
          //  layoutManager.onRestoreInstanceState(mListState);
        //}
    }


    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String movie = params[0];
            URL movieRequestUrl = NetworkUtils.buildUrl(movie);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                String[] movieData = OpenMovieUtils
                       .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);
                return movieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] movieData) {
            if (movieData != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                showErrorMessage();
            }
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            if (id == R.id.action_popular) {
                NetworkUtils.buildUrl("popular");
                menu = "popular";
                layoutManager.scrollToPositionWithOffset(0, 0);
                loadMovieData(menu);
                return true;
            }
            if (id == R.id.action_top_rated) {
                NetworkUtils.buildUrl("top_rated");
                menu = "top_rated";
                layoutManager.scrollToPositionWithOffset(0, 0);
                loadMovieData(menu);
                return true;
            }
            if (id == R.id.action_favourites) {
                Context context = this;
                Class destinationClass = FavouriteActivity.class;
                Intent intentToStartDetailActivity = new Intent(context, destinationClass);
                startActivity(intentToStartDetailActivity);
                return true;
            }

            return super.onOptionsItemSelected(item);
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

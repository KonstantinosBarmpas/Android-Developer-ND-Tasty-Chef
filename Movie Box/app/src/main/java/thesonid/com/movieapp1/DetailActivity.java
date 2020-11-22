package thesonid.com.movieapp1;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import thesonid.com.movieapp1.data.MovieContract;
import thesonid.com.movieapp1.utilities.ImageLoadTask;
import thesonid.com.movieapp1.utilities.NetworkUtils;
import thesonid.com.movieapp1.utilities.OpenDetailsUtils;
import thesonid.com.movieapp1.utilities.OpenReviewUtils;
import thesonid.com.movieapp1.data.MovieProvider;

import static android.view.View.NO_ID;
import static android.view.View.Y;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.TrailerAdapterOnClickHandler {

    private String mGeneral;
    private TextView mTitleDisplay,mPlotDisplay,mDateDisplay,mRatingDisplay,mFavouriteDisplay;
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String STATIC_MOVIE_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final String STATIC_API_KEY="YOUR-API-KEY";
    private static final String STATIC_IMAGE_URL =
            "http://image.tmdb.org/t/p/w185/";
    private static final String STATIC_REVIEW_KEY ="YOUR-API-KEY";
    private static final String STATIC_TRAILER_KEY="YOUR-API-KEY";
    private String imagePath;
    private ImageView imageView,mStar;
    private RecyclerView mRecyclerView_Reviews,mRecyclerView_Trailers;
    private TrailerAdapter mTrailerAdapter;
    private ReviewAdapter  mReviewAdapter;
    private String title;
    private ScrollView mScrollView;
    Parcelable mListState1,mListState2;
    private static final String LIST_STATE_KEY_1 ="menu_1" ;
    private static final String LIST_STATE_KEY_2 ="menu_2" ;
    LinearLayoutManager layoutManager_reviews,layoutManager_trailers;
    private static final String POSITION_1 ="position1" ;
    private static final String POSITION_2 ="position2" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        mTitleDisplay = (TextView) findViewById(R.id.display_title);
        mPlotDisplay = (TextView) findViewById(R.id.display_plot);
        mDateDisplay = (TextView) findViewById(R.id.display_date);
        mRatingDisplay = (TextView) findViewById(R.id.display_rating);
        mFavouriteDisplay=(TextView)findViewById(R.id.favourite_show);
        imageView =(ImageView)findViewById(R.id.image);
        mStar = (ImageView) findViewById(R.id.star);
        int color = Color.parseColor("#FFFFFF");
        mStar.setColorFilter(color);
        mScrollView=(ScrollView)findViewById(R.id.scrollView_details);
        mRecyclerView_Reviews = (RecyclerView) findViewById(R.id.recyclerview_reviews);
        mRecyclerView_Trailers=(RecyclerView) findViewById(R.id.recyclerview_trailers);

        layoutManager_reviews
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager_trailers
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView_Reviews.setLayoutManager(layoutManager_reviews);
        mReviewAdapter = new ReviewAdapter();
        mRecyclerView_Reviews.setAdapter(mReviewAdapter);

        mRecyclerView_Trailers.setLayoutManager(layoutManager_trailers);
        mTrailerAdapter=new TrailerAdapter(this);
        mRecyclerView_Trailers.setAdapter(mTrailerAdapter);

        mStar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (MovieProvider.CheckIsDataAlreadyInDBorNot(mGeneral)){
                    deleteMovie(MovieProvider.getRowId(mGeneral));
                }else{
                    saveMovie();
                    mFavouriteDisplay.setText(R.string.favourite_mark);
                }
            }
        });

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mGeneral = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                loadDetails();
            }
        }

    }

    private void saveMovie() {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        values.put(MovieContract.MovieEntry.COLUMN_ID, mGeneral);
        Uri newUri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);
            if (newUri != null) {
                Toast.makeText(this, R.string.favourited, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
            }
    }

    private void deleteMovie(int i) {
        String request= MovieContract.MovieEntry.CONTENT_URI+"/"+i;
        Uri requestUri =Uri.parse(request);
        int r = getContentResolver().delete(requestUri, null, null);
        if (r !=0) {
            Toast.makeText(this, R.string.unfavourited, Toast.LENGTH_SHORT).show();
            mFavouriteDisplay.setText("");
        } else {
            Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
        }
    }

    public void loadDetails(){
        new FetchMovieTask().execute();
        new FetchReviewTask().execute();
        new FetchTrailerTask().execute();
        if (MovieProvider.CheckIsDataAlreadyInDBorNot(mGeneral)){
            mFavouriteDisplay.setText(R.string.favourite_mark);
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "on save details ");
        outState.putIntArray("ARTICLE_SCROLL_POSITION",
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
        mListState1 = layoutManager_reviews.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY_1, mListState1);
        mListState2 = layoutManager_trailers.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY_2, mListState2);
        int mScrollPosition1=0;
        int mScrollPosition2=0;
        SavedState newState1 = new SavedState(mListState1);
        newState1.mScrollPosition = mScrollPosition1;
        SavedState newState2 = new SavedState(mListState2);
        newState2.mScrollPosition = mScrollPosition2;
        outState.putInt(POSITION_1,mScrollPosition1);
        outState.putInt(POSITION_2,mScrollPosition2);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(TAG, "on restore details");
        final int[] position = savedInstanceState.getIntArray("ARTICLE_SCROLL_POSITION");
        mListState1 = savedInstanceState.getParcelable(LIST_STATE_KEY_1);
        mListState2 = savedInstanceState.getParcelable(LIST_STATE_KEY_2);
        final int mScrollPosition1=savedInstanceState.getInt(POSITION_1);
        final int mScrollPosition2=savedInstanceState.getInt(POSITION_2);

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
                        mRecyclerView_Reviews.scrollToPosition(mScrollPosition1);
                        mRecyclerView_Trailers.scrollToPosition(mScrollPosition2);
                    }
                });
            }
        }.start();
    }

    public static URL buildUrl(String Query) {
        Uri builtUri = Uri.parse(STATIC_MOVIE_URL+Query+STATIC_API_KEY);
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildreviewUrl(String Query) {
        Uri builtUri = Uri.parse(STATIC_MOVIE_URL+Query+ STATIC_REVIEW_KEY);
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildtrailerUrl(String Query) {
        Uri builtUri = Uri.parse(STATIC_MOVIE_URL+Query+ STATIC_TRAILER_KEY);
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    public void onClick(String trailerForThis) {
        String url = "https://www.youtube.com/watch?v="+trailerForThis;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            URL movieRequestUrl = buildUrl(mGeneral);
            String[] results=null;
            try {
                String jsonMovieResponse = getResponseFromHttpUrl(movieRequestUrl);
                results=new String[6];
                results[1] = OpenDetailsUtils.getSimpleMovieStringsFromJson(DetailActivity.this, jsonMovieResponse,"title");
                results[2] = OpenDetailsUtils.getSimpleMovieStringsFromJson(DetailActivity.this, jsonMovieResponse, "plot");
                results[3] = OpenDetailsUtils.getSimpleMovieStringsFromJson(DetailActivity.this, jsonMovieResponse, "date");
                results[4] =OpenDetailsUtils.getSimpleMovieStringsFromJson(DetailActivity.this, jsonMovieResponse, "rating");
                results[0]=mGeneral;
                results[5]=OpenDetailsUtils.getSimpleMovieStringsFromJson(DetailActivity.this,jsonMovieResponse,"image");
                return results;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] results) {
           if (results!=null){
               mTitleDisplay.setText(results[1]);
               title=results[1];
               mPlotDisplay.setText(results[2]);
               mDateDisplay.setText("Date of release: " + results[3]);
               mRatingDisplay.setText("Rating: " +results[4]+"/10");
               imagePath=results[5];
               Log.v(TAG,imagePath);
               String url=STATIC_IMAGE_URL+imagePath;
               new ImageLoadTask(url,imageView).execute();
           }
        }
    }

    public class FetchReviewTask extends AsyncTask<String,Void,String[]>{
        @Override
        protected String[] doInBackground(String... params) {
            URL reviewRequestUrl = buildreviewUrl(mGeneral);
            String[] reviews=null;
            try {
                String jsonReviewResponse = getResponseFromHttpUrl(reviewRequestUrl);
                reviews=OpenReviewUtils.getSimpleReviewStringsFromJson(DetailActivity.this,jsonReviewResponse,"reviews");
                return reviews;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] reviews) {
           if (reviews.length==0){
               reviews=new String[1];
               reviews[0]= String.valueOf(R.string.no_reviews_found);
           }
            mReviewAdapter.setReviewData(reviews);
        }
    }

    public class FetchTrailerTask extends AsyncTask<String,Void,String[]>{
        @Override
        protected String[] doInBackground(String... params) {
            URL trailerRequestUrl = buildtrailerUrl(mGeneral);
            String[] trailer=null;
            try {
                String jsonTrailerResponse = getResponseFromHttpUrl(trailerRequestUrl);
                trailer=OpenReviewUtils.getSimpleReviewStringsFromJson(DetailActivity.this, jsonTrailerResponse,"trailers");
                return trailer;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] trailer) {
            if (trailer.length==0){
                trailer=new String[1];
                trailer[0]= String.valueOf(R.string.no_reviews_found);
            }
            mTrailerAdapter.setTrailerData(trailer);

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

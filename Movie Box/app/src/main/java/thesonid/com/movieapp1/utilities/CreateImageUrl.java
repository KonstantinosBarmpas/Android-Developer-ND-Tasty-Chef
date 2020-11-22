package thesonid.com.movieapp1.utilities;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import thesonid.com.movieapp1.DetailActivity;


import thesonid.com.movieapp1.utilities.ImageLoadTask;
import thesonid.com.movieapp1.utilities.NetworkUtils;
import thesonid.com.movieapp1.utilities.OpenDetailsUtils;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class CreateImageUrl extends AsyncTask<String, Void, String> {

    private static final String TAG = CreateImageUrl.class.getSimpleName();
    private static final String STATIC_IMAGE_URL =
            "http://image.tmdb.org/t/p/w185/";
    private static final String STATIC_MOVIE_URL =
            "https://api.themoviedb.org/3/movie/";
    private static final String STATIC_API_KEY="YOUR-API-KEY";

    private String url;
    private ImageView imageView;

    public CreateImageUrl(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected String doInBackground(String... params) {


        URL movieRequestUrl = buildUrl(url);
        String result;
        try {
            String jsonMovieResponse = getResponseFromHttpUrl(movieRequestUrl);
            result =getImageFromJson(jsonMovieResponse);
            String url=STATIC_IMAGE_URL+result;
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String url) {
        if (url!=null){
            String imagePath=url;
            Log.v(TAG,imagePath);
            new ImageLoadTask(url,imageView).execute();
        }
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

    public String getImageFromJson(String movieJsonStr)
            throws JSONException {
        JSONObject moviesJson = new JSONObject(movieJsonStr);
        String result=moviesJson.getString("poster_path");
        if (result!=null){
            Log.v(TAG, "yes");
        }else{
            Log.v(TAG, "no");
        }
        return result;
    }
}

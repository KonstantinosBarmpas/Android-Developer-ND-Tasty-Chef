package thesonid.com.movieapp1.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesonid.com.movieapp1.MovieAdapter;

/**
 * Created by user on 19/7/17.
 */

public class OpenDetailsUtils {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    public static String getSimpleMovieStringsFromJson(Context context, String movieJsonStr, String field)

            throws JSONException {

        JSONObject moviesJson = new JSONObject(movieJsonStr);
       if (field=="title"){
           return moviesJson.getString("title");
       }else if (field=="plot"){
           return moviesJson.getString("overview");
       }
       else if (field=="date"){
           return moviesJson.getString("release_date");
       }
       else if (field=="rating"){
           return moviesJson.getString("vote_average");
       }else if (field=="image") {
           return moviesJson.getString("poster_path");
       }else{
           return null;
       }
    }
}

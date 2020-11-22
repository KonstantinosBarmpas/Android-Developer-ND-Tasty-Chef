package thesonid.com.movieapp1.utilities;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import thesonid.com.movieapp1.MovieAdapter;


public final class OpenMovieUtils {

    private static final String TAG = MovieAdapter.class.getSimpleName();

    public static String[] getSimpleMovieStringsFromJson(Context context, String movieJsonStr)

             throws JSONException{
        String[] parsedData=null;
        JSONObject moviesJson = new JSONObject(movieJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray("results");
        parsedData = new String[moviesArray.length()];
        for (int i = 0; i < moviesArray.length(); i++) {
            JSONObject movieObject = moviesArray.getJSONObject(i);
            parsedData[i]=movieObject.getString("id");
        }
        return parsedData;

    }
}
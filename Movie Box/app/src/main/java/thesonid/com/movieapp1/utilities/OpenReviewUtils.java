package thesonid.com.movieapp1.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OpenReviewUtils {

    public static String[] getSimpleReviewStringsFromJson(Context context, String movieJsonStr, String key)

            throws JSONException {
        String[] rev=null;
        JSONObject reviewJson = new JSONObject(movieJsonStr);
        JSONArray reviews=reviewJson.getJSONArray("results");
        rev = new String[reviews.length()];
        if (key=="reviews") {
            for (int i = 0; i < reviews.length(); i++) {
                JSONObject reviewObject = reviews.getJSONObject(i);
                rev[i] = reviewObject.getString("content");
            }
        }else if (key=="trailers"){
            for (int i = 0; i < reviews.length(); i++) {
                JSONObject reviewObject = reviews.getJSONObject(i);
                rev[i] = reviewObject.getString("key");
            }
        }
        return rev;
    }
}

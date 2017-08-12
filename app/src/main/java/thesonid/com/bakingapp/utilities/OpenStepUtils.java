package thesonid.com.bakingapp.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 8/8/17.
 */

public final class OpenStepUtils {

    public static String[] getSteps(Context context, String recipesJsonStr,String title)

            throws JSONException {
        String[] parsedData=null;
        JSONArray recipesArray=new JSONArray(recipesJsonStr);
        for (int i=0; i<recipesArray.length(); i++){
            JSONObject recipeJSON=recipesArray.getJSONObject(i);
            if (recipeJSON.getString("name").equals(title)){
                JSONArray steps=recipeJSON.getJSONArray("steps");
                parsedData=new String[steps.length()];
                for (int j=0; j<steps.length(); j++){
                JSONObject stepJSON=steps.getJSONObject(j);
                parsedData[j]=stepJSON.getString("shortDescription");
                }
            }
        }
        return parsedData;
    }
}

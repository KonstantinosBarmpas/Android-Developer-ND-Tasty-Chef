package thesonid.com.bakingapp.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 8/8/17.
 */

public final class OpenRecipesUtil {

    public static String[] getRecipesFromJson(Context context, String recipesJsonStr)

            throws JSONException {
        String[] parsedData=null;
        JSONArray recipesArray=new JSONArray(recipesJsonStr);
        parsedData = new String[recipesArray.length()];
        for (int i=0; i<recipesArray.length(); i++){
            JSONObject recipeJson=recipesArray.getJSONObject(i);
            if(recipeJson.getString("name")!=null){
                parsedData[i]=recipeJson.getString("name");
            }else{
                parsedData[i]="No title";
            }
        }
        return parsedData;
    }
}

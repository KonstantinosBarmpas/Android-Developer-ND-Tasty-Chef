package thesonid.com.bakingapp.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 10/8/17.
 */

public class OpenImageUtils {
    public static String[] getImageFromJson(Context context, String recipesJsonStr)

            throws JSONException {
        String[] parsedData=null;
        JSONArray recipesArray=new JSONArray(recipesJsonStr);
        parsedData = new String[recipesArray.length()];
        for (int i=0; i<recipesArray.length(); i++){
            JSONObject recipeJson=recipesArray.getJSONObject(i);
            if(!recipeJson.getString("image").equals("")){
                parsedData[i]=recipeJson.getString("image");
            }else{
                parsedData[i]="-";
            }
        }
        return parsedData;
    }
}

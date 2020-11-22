package thesonid.com.bakingapp.utilities;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.data;

/**
 * Created by user on 8/8/17.
 */

public final class OpenDetailsUtils {
    public static String getIngridientsFromJson(Context context, String recipesJsonStr, String title)


            throws JSONException {
        String parsedData="";
        JSONArray recipesArray=new JSONArray(recipesJsonStr);
        for (int i=0; i<recipesArray.length(); i++){
            JSONObject rec=recipesArray.getJSONObject(i);
            if (rec.getString("name").equals(title)){
                JSONArray ingridientsArray=rec.getJSONArray("ingredients");
                for (int j=0; j<ingridientsArray.length(); j++){
                    JSONObject ingr=ingridientsArray.getJSONObject(j);
                    parsedData=parsedData+ingr.getString("ingredient")+" "+ingr.getString("quantity")+" "+ingr.getString("measure")+"\n";
                }
                break;
            }
        }
        return parsedData;
    }
}
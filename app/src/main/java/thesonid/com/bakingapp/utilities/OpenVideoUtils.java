package thesonid.com.bakingapp.utilities;

import android.content.Context;
import android.text.LoginFilter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

/**
 * Created by user on 8/8/17.
 */

public final class OpenVideoUtils {
    public static String[] getVideoFromJson(Context context, String recipesJsonStr, String shortDe, String title)


            throws JSONException {
        String[] parsedData=null;
        JSONArray recipesArray=new JSONArray(recipesJsonStr);
        int number;
        if (title.equals("Nutella Pie")){
            number=0;
        }else if (title.equals("Brownies")){
            number=1;
        }else if(title.equals("Yellow Cake")){
            number=2;
        }else{
            number=3;
        }

        JSONObject dec=recipesArray.getJSONObject(number);
        JSONArray steps=dec.getJSONArray("steps");
        parsedData=new String[3];

        for (int i=0; i<steps.length(); i++){
            if (steps.getJSONObject(i).getString("shortDescription").equals(shortDe)){
                if (steps.getJSONObject(i).getString("videoURL").equals("")){
                    parsedData[0]="-";
                }else{
                parsedData[0]=steps.getJSONObject(i).getString("videoURL");}
                if (steps.getJSONObject(i).getString("description").equals("")){
                    parsedData[1]="-";
                }else{
                    parsedData[1]=steps.getJSONObject(i).getString("description");}
                if (steps.getJSONObject(i).getString("thumbnailURL").equals("")){
                    parsedData[2]="-";
                }else{
                    parsedData[2]=steps.getJSONObject(i).getString("thumbnailURL");
                }
                break;
            }
        }
        return parsedData;
    }
}

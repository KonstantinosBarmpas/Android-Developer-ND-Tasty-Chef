package thesonid.com.bakingapp.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.net.URL;

import thesonid.com.bakingapp.MainActivity;
import thesonid.com.bakingapp.MediaFragment;
import thesonid.com.bakingapp.R;
import thesonid.com.bakingapp.StepActivity;

import static android.R.attr.contextClickable;
import static android.R.attr.data;
import static android.R.attr.description;

/**
 * Created by user on 9/8/17.
 */

public class AsyncVideoWhenTablet {
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static String shortDescription;
    private static String title;
    private static String description;
    private static String videouri;
    private static Bundle b;

    public static Bundle getInfo(String shortD, String tit){
        shortDescription=shortD;
        title=tit;
        new FetchVideo().execute();
        b.putString("video",videouri);
        b.putString("description",description);
        Log.v(TAG,videouri+" ! "+description);
        return b;
    }

    public static class FetchVideo extends AsyncTask<Void, Void, String[]> {

        private Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {

            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);

                String[] recipesData = OpenVideoUtils
                        .getVideoFromJson(context, jsonResponse,shortDescription,title);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] data) {
            String videouri_1;
            String description_1;

            if (data[0] != null && data[0]!="-") {
                videouri_1=data[0];
            }else{
                videouri_1="-";
            }
            if (data[1] != null && data[1]!="-") {
                description_1=data[1];
            }else{
                description_1="-";
            }

            if (videouri_1!=null && description_1!=null ){
               videouri=videouri_1;
                description=description_1;
            }
        }
    }
}


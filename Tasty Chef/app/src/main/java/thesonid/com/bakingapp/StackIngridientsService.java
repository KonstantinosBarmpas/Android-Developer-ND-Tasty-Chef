package thesonid.com.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import thesonid.com.bakingapp.utilities.NetworkUtils;
import thesonid.com.bakingapp.utilities.OpenDetailsUtils;
import thesonid.com.bakingapp.utilities.OpenRecipesUtil;



public class StackIngridientsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory2(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory2 implements RemoteViewsService.RemoteViewsFactory {
    private String mWidgetItems;
    private Context mContext;
    private int mAppWidgetId;
    private String title;

    public StackRemoteViewsFactory2(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        title=intent.getStringExtra("name");
    }

    public void onCreate() {
        new FetchIngredientsTask().execute();
    }

    public void onDestroy() {
    }

    public int getCount() {
        return 1;
    }

    public RemoteViews getViewAt(int position) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.new_layout);
        rv.setTextViewText(R.id.widget_item, mWidgetItems);
        return rv;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public long getItemId(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
    }

    public class FetchIngredientsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            Context context=null;
            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);

                String recipesData = OpenDetailsUtils
                        .getIngridientsFromJson(context, jsonResponse, title);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String data) {
            if (data != null) {
                mWidgetItems=data;
            }
        }
    }
}

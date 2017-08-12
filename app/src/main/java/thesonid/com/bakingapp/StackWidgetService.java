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
import thesonid.com.bakingapp.utilities.OpenRecipesUtil;

public class StackWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StackRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private int mCount=4;
    private List<WidgetItem> mWidgetItems1 = new ArrayList<WidgetItem>();
    private Context mContext;
    private int mAppWidgetId;
    private String title;
    private String[] mTitles;

    public StackRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        new FetchRecipesTask().execute();
    }

    public void onDestroy() {
        mWidgetItems1.clear();
    }

    public int getCount() {
        return mCount;
    }

    public RemoteViews getViewAt(int position) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);
        rv.setTextViewText(R.id.widget_item, mWidgetItems1.get(position).text);
        Bundle extras = new Bundle();
        extras.putInt(RecipesWidgetProvider.EXTRA_ITEM, position);
        extras.putString("key_widget",mTitles[position]);
        extras.putString("keys",mTitles[position]);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
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

    public class FetchRecipesTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(Void... params) {
            Context context = null;
            URL recipeRequestUrl = NetworkUtils.buildUrl();

            try {
                String jsonResponse = NetworkUtils
                        .getResponseFromHttpUrl(recipeRequestUrl);

                String[] recipesData = OpenRecipesUtil
                        .getRecipesFromJson(context, jsonResponse);
                return recipesData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] data) {
            if (data != null) {
                mCount = data.length;
                mTitles=new String[mCount];
                for (int i = 0; i < data.length; i++) {
                    title=data[i];
                    mTitles[i]=title;
                    mWidgetItems1.add(new WidgetItem(data[i]));
                }
            } else {
                //do nothing
            }
        }
    }
}
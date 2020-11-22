package thesonid.com.bakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import thesonid.com.bakingapp.utilities.NetworkUtils;

/**
 * Implementation of App Widget functionality.
 */
public class RecipesWidgetProvider extends AppWidgetProvider {

    public static final String TOAST_ACTION = "thesonid.com.bakingapp.TOAST_ACTION";
    public static final String EXTRA_ITEM = "thesonid.com.bakingapp.EXTRA_ITEM";

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent2) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent2.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent2.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent2.getIntExtra(EXTRA_ITEM, 0);
            String test = intent2.getStringExtra("key_widget");
            Toast.makeText(context, "Loading Ingridients for:" + test, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(context, StackIngridientsService.class);
            intent.putExtra(mgr.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra("name",test);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_layout);
            rv.setRemoteAdapter(appWidgetId, R.id.stack_view, intent);
            rv.setEmptyView(R.id.stack_view, R.id.empty);

            Intent toastIntent = new Intent(context, RecipesWidgetProvider.class);
            toastIntent.setAction(RecipesWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(mgr.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);

            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            rv.setOnClickPendingIntent(R.id.skoufos, pendingIntent);
            mgr.updateAppWidget(appWidgetId, rv);

        }
        super.onReceive(context, intent2);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i = 0; i < appWidgetIds.length; ++i) {
            Intent intent = new Intent(context, StackWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.recipes_widget_provider);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.stack_view, intent);
            rv.setEmptyView(R.id.stack_view, R.id.empty);
            Intent toastIntent = new Intent(context, RecipesWidgetProvider.class);
            toastIntent.setAction(RecipesWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent);
            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            rv.setOnClickPendingIntent(R.id.skoufos, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}


package com.vnbstudio.xrt.cbr;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;

import org.json.JSONObject;


public class NewAppWidget extends AppWidgetProvider {

    static String myTag = "Widget Calling!";
    private Context contextProvider;
    private AppWidgetManager appWidgetManagerProvider;
    private int appWidgetIdProvider;

    IResult mResultCallback = null;
    VolleyService mVolleyService;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        contextProvider = context;
        appWidgetManagerProvider = appWidgetManager;
        appWidgetIdProvider = appWidgetId;


//        CharSequence widgetText = NewAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        Log.w(myTag,"updateAppWidget begins!");
        initVolleyCallback();
        connect();

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        Intent launchMain = new Intent(context, MainActivity.class);
        PendingIntent pendingMainIntent = PendingIntent.getActivity(context, 0, launchMain, 0);
        views.setOnClickPendingIntent(R.id.Widget_layout, pendingMainIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    void initVolleyCallback(){
        final String[] list = {"USD", "EUR", "GBP", "CHF"};
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType,JSONObject response) {
                Log.d("TEST", "Volley requester " + requestType);
                Log.d("TEST", "Volley JSON post" + response);
                RemoteViews views = new RemoteViews(contextProvider.getPackageName(), R.layout.new_app_widget);
                for (int i = 0; i < list.length; i++){
                    try {
                        JSONObject check = response.optJSONObject("response").optJSONObject("result").optJSONObject(list[i]);
                        if (check != null) {
                            Log.d("Success", check.toString());
                            if(list[i] == "USD") {
                                views.setTextViewText(R.id.textUSD, "USD: " + (response.optJSONObject("response").optJSONObject("result").optJSONObject("USD").optString("val")));
                            }
                            if(list[i] == "EUR") {
                                views.setTextViewText(R.id.textEUR, "EUR: " + (response.optJSONObject("response").optJSONObject("result").optJSONObject("EUR").optString("val")));

                            }
                            if(list[i] == "GBP") {
                                views.setTextViewText(R.id.textGBP, "GBP: " + (response.optJSONObject("response").optJSONObject("result").optJSONObject("GBP").optString("val")));

                            }
                            if(list[i] == "CHF") {
                                views.setTextViewText(R.id.textCHF, "CHF: " + (response.optJSONObject("response").optJSONObject("result").optJSONObject("CHF").optString("val")));

                            }
                        }
                    } catch (Throwable t) { Log.d("Error", t.toString()); }
                }
                appWidgetManagerProvider.updateAppWidget(appWidgetIdProvider, views);

            }

            @Override
            public void notifyError(String requestType,VolleyError error) {
                Log.d("TEST", "Volley requester " + requestType);
                Log.d("TEST", "Volley JSON post " + "That didn't work!");
            }
        };
    }

    public void connect() {
        mVolleyService = new VolleyService(mResultCallback,contextProvider);
        mVolleyService.getDataVolley("GETCALL","url");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.w(myTag,"onUpdate begins!");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
//        for (int appWidgetId : appWidgetIds) {
//            NewAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
//        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.w(myTag,"onEnable begins!");
        initVolleyCallback();
        connect();
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}


package com.example.pcrc2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.graphics.Color;

public class MediaWidgetProvider extends AppWidgetProvider {

    public static boolean isConnected = false;
    public static final String UPDATE_STATUS = "com.example.pcrc2.UPDATE_STATUS";
    public static final String ACTION_PREV = "com.example.pcrc2.PREV";
    public static final String ACTION_PLAY = "com.example.pcrc2.PLAY";
    public static final String ACTION_NEXT = "com.example.pcrc2.NEXT";
    public static final String ACTION_MUTE = "com.example.pcrc2.MUTE";
    public static final String ACTION_VOL_DOWN = "com.example.pcrc2.VOL_DOWN";
    public static final String ACTION_VOL_UP = "com.example.pcrc2.VOL_UP";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (action == null) return;

        String command = "";
        switch (action) {
            case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
            case "android.net.conn.CONNECTIVITY_CHANGE":
            case UPDATE_STATUS:
                command = "status"; break;
            case ACTION_PREV: command = "prev"; break;
            case ACTION_PLAY: command = "play"; break;
            case ACTION_NEXT: command = "next"; break;
            case ACTION_MUTE: command = "mute"; break;
            case ACTION_VOL_DOWN: command = "voldown"; break;
            case ACTION_VOL_UP: command = "volup"; break;
        }

        if (!command.isEmpty()) MediaClient.sendAction(command, reachedServer -> {
            isConnected = reachedServer;
            updateAllWidgets(context);
        });
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        setPendingIntent(context, views, R.id.btn_prev, ACTION_PREV);
        setPendingIntent(context, views, R.id.btn_play, ACTION_PLAY);
        setPendingIntent(context, views, R.id.btn_next, ACTION_NEXT);
        setPendingIntent(context, views, R.id.btn_mute, ACTION_MUTE);
        setPendingIntent(context, views, R.id.btn_vol_down, ACTION_VOL_DOWN);
        setPendingIntent(context, views, R.id.btn_vol_up, ACTION_VOL_UP);
        setPendingIntent(context, views, R.id.status_container, UPDATE_STATUS);

        String statusText = isConnected ? "Active" : "Offline";
        int targetColor = isConnected
                ? Color.parseColor("#00FF44")
                : Color.parseColor("#5D5D5D");

        views.setImageViewResource(R.id.status_icon, R.drawable.monitor_linear_gray);
        views.setInt(R.id.status_icon, "setColorFilter", targetColor);

        views.setTextViewText(R.id.status_text, statusText);
        views.setTextColor(R.id.status_text, targetColor);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private void setPendingIntent(Context context, RemoteViews views, int viewId, String action) {
        Intent intent = new Intent(context, MediaWidgetProvider.class);
        intent.setAction(action);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(viewId, pendingIntent);
    }

    private void updateAllWidgets(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, MediaWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);

        for (int appWidgetId : appWidgetIds) updateWidget(context, appWidgetManager, appWidgetId);
    }
}
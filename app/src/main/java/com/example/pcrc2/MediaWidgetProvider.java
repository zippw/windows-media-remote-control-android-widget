package com.example.pcrc2;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

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
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();

        if (action == null) return;

        if (action.equals(UPDATE_STATUS)) {
            MediaClient.sendAction("status", new MediaClient.Callback() {
                @Override
                public void onResult(boolean reachedServer) {
                    isConnected = reachedServer;
                    updateAllWidgets(context);
                }
            });
            return;
        }

        String command = "";
        if (action.equals(ACTION_PREV)) command = "prev";
        else if (action.equals(ACTION_PLAY)) command = "play";
        else if (action.equals(ACTION_NEXT)) command = "next";
        else if (action.equals(ACTION_MUTE)) command = "mute";
        else if (action.equals(ACTION_VOL_DOWN)) command = "voldown";
        else if (action.equals(ACTION_VOL_UP)) command = "volup";

        if (!command.isEmpty()) {
            MediaClient.sendAction(command, new MediaClient.Callback() {
                @Override
                public void onResult(boolean reachedServer) {
                    isConnected = reachedServer;
                    updateAllWidgets(context);
                }
            });
        }
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

        int iconRes = isConnected ? R.drawable.monitor_linear_green : R.drawable.monitor_linear_gray;
        String statusText = isConnected ? "Connected" : "Offline";

        views.setTextViewText(R.id.status_text, statusText);
        views.setImageViewResource(R.id.status_icon, iconRes);

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

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }
}
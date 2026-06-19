package com.example.pcrc2;

import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaClient {
    private static final String TAG = "MediaClient";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onResult(boolean reachedServer);
    }

    public static void sendAction(String action, Callback callback) {
        executor.execute(() -> {
            boolean reachedServer = false;

            try {
                URL url = new URL(Config.BASE_URL + "/api/" + action);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + Config.TOKEN);
                conn.setDoOutput(true);
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(1000);

                conn.getOutputStream().write("{}".getBytes());
                conn.getResponseCode();
                reachedServer = true;
                conn.disconnect();

            } catch (IOException e) {
                Log.e(TAG, "Connection failed: " + e.getMessage());
            }

            if (callback != null) {
                callback.onResult(reachedServer);
            }
        });
    }
}
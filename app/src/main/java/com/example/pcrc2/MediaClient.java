package com.example.pcrc2;

import android.util.Log;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MediaClient {
    private static final String TAG = "MediaClient";
    private static final String BASE_URL = "http://192.168.31.81:12597";
    private static final String TOKEN = "chlorine12597-4587802ccd70540989555071f63d94a2b38fac719973d3e92ed8d56f7aee50d0424e0ca9d4dee6519d5c35035e56eb40dbb5528a98395d70fdb94e6099ea311d";

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onResult(boolean reachedServer);
    }

    public static void sendAction(String action, Callback callback) {
        executor.execute(() -> {
            boolean reachedServer = false;

            try {
                URL url = new URL(BASE_URL + "/api/" + action);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
                conn.setDoOutput(true);
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);

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
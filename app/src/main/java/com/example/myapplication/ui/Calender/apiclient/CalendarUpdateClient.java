package com.example.myapplication.ui.Calender.apiclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CalendarUpdateClient {
    private static final String API_URL = "https://100.96.1.3/api_update_calendar.php"; // 替换为你的 PHP 服务器的 URL

    public interface CalendarCallback {
        void onSuccess(String message);
        void onError(String message);
    }

    public void updateCalendar(String eventDataJson, CalendarCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);

                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(eventDataJson.getBytes("UTF-8"));
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();

                        callback.onSuccess(response.toString());
                    } else {
                        callback.onError("请求失败: " + responseCode);
                    }
                } catch (Exception e) {
                    callback.onError("异常: " + e.getMessage());
                }
            }
        }).start();
    }
}
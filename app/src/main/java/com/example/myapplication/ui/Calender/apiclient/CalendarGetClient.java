package com.example.myapplication.ui.Calender.apiclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 這個類提供了與日曆 API 進行 HTTP 請求來抓取日曆資料的方法。
 */
public class CalendarGetClient {
    private static final String GET_CALENDAR_URL = "http://100.96.1.3/api_get_calendar.php";

    public static String getCalendar(String account) throws Exception {
        URL url = new URL(GET_CALENDAR_URL + "?account=" + account);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new RuntimeException("無法獲取日曆：HTTP 錯誤碼：" + responseCode);
        }
    }

    public interface Callback {
        void onSuccess(String result);

        void onFailure(String errorMessage);
    }

    public static void fetchCalendarData(String account, Callback callback) {
        new Thread(() -> {
            try {
                String calendarData = getCalendar(account);
                callback.onSuccess(calendarData);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("無法獲取日曆資料：" + e.getMessage());
            }
        }).start();
    }
}

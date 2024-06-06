package com.example.myapplication.ui.Calender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 這個類提供了與日曆 API 進行 HTTP 請求來更新日曆資料的方法。
 */
public class CalendarUpdateClient {
    private static final String UPDATE_CALENDAR_URL = "http://100.96.1.3/api_update_calendar.php";

    public static String updateCalendar(String eventData) throws Exception {
        URL url = new URL(UPDATE_CALENDAR_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = eventData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

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
            throw new RuntimeException("無法更新日曆：HTTP 錯誤碼：" + responseCode);
        }
    }
}

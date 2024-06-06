package com.example.myapplication.ui.Calender;
// CalendarAddClient.java

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 這個類提供了方法來通過 HTTP POST 請求向日曆 API 添加日曆事件。
 */
public class CalendarAddClient {
    private static final String ADD_EVENT_URL = "http://100.96.1.3/api_add_calendar.php";

    /**
     * 使用 HTTP POST 請求向日曆 API 添加日曆事件。
     * @param eventData 表示事件數據的 JSON 字符串。
     * @param eventId 事件的ID。
     * @return API 的回應。
     * @throws Exception 如果在 HTTP 請求期間發生錯誤。
     */
    public static String addEvent(String eventData, String eventId) throws Exception {
        URL url = new URL(ADD_EVENT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = eventData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }
}

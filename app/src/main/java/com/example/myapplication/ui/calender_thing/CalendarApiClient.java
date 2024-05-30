package com.example.myapplication.ui.calender_thing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CalendarApiClient {

    // 定義獲取行事曆資料的 URL
    private static final String GET_CALENDAR_URL = "http://example.com/api/getcalendar";
    // 定義更新行事曆資料的 URL
    private static final String UPDATE_CALENDAR_URL = "http://example.com/api/updatecalendar";

    // 獲取指定帳號的行事曆資料
    public static String getCalendar(String account) throws Exception {
        // 構建帶有帳號參數的完整 URL
        URL url = new URL(GET_CALENDAR_URL + "?account=" + account);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 設置請求方法為 GET
        connection.setRequestMethod("GET");

        // 獲取 HTTP 響應碼
        int responseCode = connection.getResponseCode();
        // 如果響應碼為 200（HTTP_OK），則讀取返回資料
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // 一行一行讀取返回的數據
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 返回行事曆資料
            return response.toString();
        } else {
            // 如果響應碼不是 200，則拋出異常
            throw new RuntimeException("Failed to get calendar: HTTP error code : " + responseCode);
        }
    }

    // 更新行事曆資料
    public static String updateCalendar(String eventData) throws Exception {
        // 構建 URL
        URL url = new URL(UPDATE_CALENDAR_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 設置請求方法為 POST
        connection.setRequestMethod("POST");
        // 設置請求屬性，指定內容類型為 JSON
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        // 允許輸出數據
        connection.setDoOutput(true);

        // 將行事曆數據寫入輸出流
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = eventData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 獲取 HTTP 響應碼
        int responseCode = connection.getResponseCode();
        // 如果響應碼為 200（HTTP_OK），則讀取返回資料
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // 一行一行讀取返回的數據
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 返回更新結果
            return response.toString();
        } else {
            // 如果響應碼不是 200，則拋出異常
            throw new RuntimeException("Failed to update calendar: HTTP error code : " + responseCode);
        }
    }
}

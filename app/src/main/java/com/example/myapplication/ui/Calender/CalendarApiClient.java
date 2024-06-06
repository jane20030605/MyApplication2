package com.example.myapplication.ui.Calender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 這個類提供了與日曆 API 進行 HTTP 請求交互的方法。
 */
public class CalendarApiClient {

    // 不同日曆 API 端點的 URL
    private static final String GET_CALENDAR_URL = "http://100.96.1.3/api_get_calendar.php";
    private static final String UPDATE_CALENDAR_URL = "http://100.96.1.3/api_update_calendar.php";
    private static final String DELETE_EVENT_URL = "http://100.96.1.3/api_delete_calendar.php";
    private static final String ADD_EVENT_URL = "http://100.96.1.3/api_add_calendar.php";

    /**
     * 從日曆 API 中檢索特定帳戶的日曆資料。
     * @param account 要檢索日曆資料的帳戶。
     * @return 日曆資料的字串表示形式。
     * @throws Exception 在執行 HTTP 請求期間發生錯誤時拋出異常。
     */
    public static String getCalendar(String account) throws Exception {
        // 建立 GET 請求的 URL
        URL url = new URL(GET_CALENDAR_URL + "?account=" + account);
        // 開啟連接
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 獲取 HTTP 響應碼
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 如果成功，讀取響應
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // 逐行讀取響應並追加到 StringBuilder 中
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString(); // 將響應作為字串返回
        } else {
            // 如果請求失敗，拋出異常
            throw new RuntimeException("無法獲取日曆：HTTP 錯誤碼：" + responseCode);
        }
    }

    /**
     * 使用日曆 API 更新日曆中的事件資料。
     * @param eventData 要在日曆中更新的事件資料，格式為 JSON。
     * @return API 的響應，以字串表示。
     * @throws Exception 在執行 HTTP 請求期間發生錯誤時拋出異常。
     */
    public static String updateCalendar(String eventData) throws Exception {
        // 建立 POST 請求的 URL
        URL url = new URL(UPDATE_CALENDAR_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 將事件資料寫入輸出流
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = eventData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 獲取 HTTP 響應碼
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 如果成功，讀取響應
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // 逐行讀取響應並追加到 StringBuilder 中
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString(); // 將響應作為字串返回
        } else {
            // 如果請求失敗，拋出異常
            throw new RuntimeException("無法更新日曆：HTTP 錯誤碼：" + responseCode);
        }
    }

    /**
     * 使用日曆 API 從日曆中刪除事件。
     * @param eventId 要刪除的事件的 ID。
     * @return API 的響應，以字串表示。
     * @throws Exception 在執行 HTTP 請求期間發生錯誤時拋出異常。
     */
    public static String deleteEvent(String eventId) throws Exception {
        // 建立 DELETE 請求的 URL
        URL url = new URL(DELETE_EVENT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 將事件 ID 寫入輸出流
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = eventId.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 獲取 HTTP 響應碼
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 如果成功，讀取響應
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // 逐行讀取響應並追加到 StringBuilder 中
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString(); // 將響應作為字串返回
        } else {
            // 如果請求失敗，拋出異常
            throw new RuntimeException("無法刪除事件：HTTP 錯誤碼：" + responseCode);
        }
    }

    /**
     * 使用日曆 API 向日曆中添加新事件。
     * @param eventData 要添加到日曆中的事件資料，格式為 JSON。
     * @return API 的響應，以字串表示。
     * @throws Exception 在執行 HTTP 請求期間發生錯誤時拋出異常。
     */
    public static String addEvent(String eventData) throws Exception {
        // 建立 POST 請求的 URL
        URL url = new URL(ADD_EVENT_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        // 將事件資料寫入輸出流
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = eventData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // 獲取 HTTP 響應碼
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // 如果成功，讀取響應
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // 逐行讀取響應並追加到 StringBuilder 中
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString(); // 將響應作為字串返回
        } else {
            // 如果請求失敗，拋出異常
            throw new RuntimeException("無法添加事件：HTTP 錯誤碼：" + responseCode);
        }
    }

    public interface Callback {
        void onSuccess(String result);

        void onFailure(String errorMessage);

        void onDataReceived(String calendarData);

    }
    public static void fetchCalendarData(Callback callback) {
        new Thread(new Runnable() {
            private Object account;

            @Override
            public void run() {
                try {
                    String calendarData = getCalendar(account.toString());
                    callback.onSuccess(calendarData);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onFailure("無法獲取日曆資料：" + e.getMessage());
                }
            }
        }).start();
    }


}
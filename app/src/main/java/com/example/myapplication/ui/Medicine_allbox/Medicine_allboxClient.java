package com.example.myapplication.ui.Medicine_allbox;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Medicine_allboxClient {

    private static final String MEDICINE_ALLBOX_URL = "http://100.96.1.3/api_medication.php";

    public static void main(String[] args) {
        try {
            // 創建URL對象
            URL url = new URL(MEDICINE_ALLBOX_URL);

            // 打開連接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 設置請求方法為GET
            connection.setRequestMethod("GET");

            // 設置請求屬性
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            // 獲取響應代碼
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // 成功回應
                // 讀取響應
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                // 關閉BufferedReader
                in.close();

                // 將響應內容轉換為JSON對象
                JSONArray jsonResponse = new JSONArray(content.toString());

                // 遍歷JSON數組並打印每個項目
                for (int i = 0; i < jsonResponse.length(); i++) {
                    JSONObject medication = jsonResponse.getJSONObject(i);
                    System.out.println("Medication ID: " + medication.getInt("id"));
                    System.out.println("Name: " + medication.getString("name"));
                    System.out.println("Dosage: " + medication.getString("dosage"));
                    System.out.println("Frequency: " + medication.getString("frequency"));
                    System.out.println();
                }

            } else { // 錯誤回應
                System.out.println("GET request failed. Response Code: " + responseCode);
            }

            // 斷開連接
            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

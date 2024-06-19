package com.example.myapplication.ui.change_password;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class password_changeApiClient {

    private static final String CHANGE_PASSWORD_URL = "http://100.96.1.3/api_change_password.php"; // 替換為您的 API URL

    public static void main(String[] args) {
        try {
            JSONObject data = new JSONObject();
            data.put("account", "ACCOUNT");
            data.put("current_password", "etCurrentPassword");
            data.put("new_password", "etNewPassword");
            data.put("confirm_new_password", "etConfirmPassword");

            String response = sendPostRequest(CHANGE_PASSWORD_URL, data.toString());
            System.out.println("Response: " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String sendPostRequest(String apiUrl, String jsonData) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return response.toString();
    }
}


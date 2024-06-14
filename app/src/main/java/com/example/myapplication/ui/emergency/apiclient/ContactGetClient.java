package com.example.myapplication.ui.emergency.apiclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContactGetClient {
    private static final String GET_CONTACT_URL = "http://100.96.1.3/api_get_contact.php";

    public static String getContact(String account) throws Exception {
        URL url = new URL(GET_CONTACT_URL + "?account=" + account);
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
            throw new RuntimeException("無法獲取緊急連絡人：HTTP 錯誤碼：" + responseCode);
        }
    }

    public interface Callback {
        void onSuccess(String result);
        void onFailure(String errorMessage);
    }

    public static void fetchContactData(String account, ContactGetClient.Callback callback) {
        new Thread(() -> {
            try {
                String contactData = getContact(account);
                callback.onSuccess(contactData);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure("無法獲取緊急連絡人資料：" + e.getMessage());
            }
        }).start();
    }
}

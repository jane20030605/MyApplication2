package com.example.myapplication.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkRequestManager {
    private static NetworkRequestManager instance;
    private Context context;
    private Handler handler;

    // 私有構造函數，確保單例模式
    private NetworkRequestManager(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }

    // 獲取單例實例
    public static synchronized NetworkRequestManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkRequestManager(context.getApplicationContext());
        }
        return instance;
    }

    // 請求回調接口，用於處理請求成功和失敗的情況
    public interface RequestListener {
        void onSuccess(String response); // 成功回調
        void onError(String error); // 失敗回調
    }

    // 發送GET請求的方法
    public void makeGetRequest(String urlString, RequestListener listener) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString); // 生成URL
                connection = (HttpURLConnection) url.openConnection(); // 打開連接
                connection.setRequestMethod("GET"); // 設置請求方法為GET

                int responseCode = connection.getResponseCode(); // 獲取回應碼
                if (responseCode == HttpURLConnection.HTTP_OK) { // 如果回應碼是200，表示成功
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 讀取輸入流
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine); // 將輸入流中的每行數據添加到StringBuilder中
                    }
                    in.close();
                    handler.post(() -> listener.onSuccess(response.toString())); // 回到主線程調用成功回調
                } else {
                    handler.post(() -> listener.onError("HTTP Error: " + responseCode)); // 回到主線程調用失敗回調，並傳遞錯誤訊息
                }
            } catch (Exception e) {
                handler.post(() -> listener.onError(e.getMessage())); // 捕獲異常並回到主線程調用失敗回調，並傳遞異常訊息
            } finally {
                if (connection != null) {
                    connection.disconnect(); // 斷開連接
                }
            }
        }).start();
    }

    // 發送POST請求的方法
    public void makePostRequest(String urlString, String postData, RequestListener listener) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString); // 生成URL
                connection = (HttpURLConnection) url.openConnection(); // 打開連接
                connection.setRequestMethod("POST"); // 設置請求方法為POST
                connection.setDoOutput(true); // 允許輸出

                OutputStream os = connection.getOutputStream(); // 獲取輸出流
                os.write(postData.getBytes()); // 將數據寫入輸出流
                os.flush(); // 刷新輸出流
                os.close(); // 關閉輸出流

                int responseCode = connection.getResponseCode(); // 獲取回應碼
                if (responseCode == HttpURLConnection.HTTP_OK) { // 如果回應碼是200，表示成功
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 讀取輸入流
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine); // 將輸入流中的每行數據添加到StringBuilder中
                    }
                    in.close();
                    handler.post(() -> listener.onSuccess(response.toString())); // 回到主線程調用成功回調
                } else {
                    handler.post(() -> listener.onError("HTTP Error: " + responseCode)); // 回到主線程調用失敗回調，並傳遞錯誤訊息
                }
            } catch (Exception e) {
                handler.post(() -> listener.onError(e.getMessage())); // 捕獲異常並回到主線程調用失敗回調，並傳遞異常訊息
            } finally {
                if (connection != null) {
                    connection.disconnect(); // 斷開連接
                }
            }
        }).start();
    }
}

package com.example.myapplication.network;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import android.os.StrictMode;
import android.util.Log;

public class DatabaseHelper {

    private static final String URL = "jdbc:mysql://26.110.164.151:3306/appdata";
    private static final String USER = "root";
    private static final String PASS = "AMY911002";

    public DatabaseHelper() {
        // 允許在主執行緒中執行網路操作（僅供測試）
        // 在生產環境中，應該在AsyncTask或其他背景執行緒中執行網路操作
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void connectAndQuery() {
        Connection conn = null;
        Statement stmt = null;
        try {
            // 加載JDBC驅動
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 建立連接
            conn = DriverManager.getConnection(URL, USER, PASS);

            // 創建SQL語句
            stmt = conn.createStatement();
            String sql = "SELECT id, name FROM your_table";
            ResultSet rs = stmt.executeQuery(sql);

            // 處理結果集
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                Log.d("DatabaseHelper", "ID: " + id + ", Name: " + name);
            }

            // 關閉資源
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error: " + e.getMessage(), e);
        }
    }
}

package com.example.myapplication.Request;

public class ResetPasswordRequest {
    // 儲存用戶郵件地址
    private String email;

    // 構造函數，用於初始化 email 字段
    public ResetPasswordRequest(String email) {

        this.email = email;
    }

    // 獲取 email 字段的值
    public String getEmail() {
        return email;
    }

    // 設置 email 字段的值
    public void setEmail(String email) {
        this.email = email;
    }
}

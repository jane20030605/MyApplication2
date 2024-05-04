package com.example.myapplication.models;

public class User {
    private String username; // 使用者名稱
    private String password; // 密碼

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

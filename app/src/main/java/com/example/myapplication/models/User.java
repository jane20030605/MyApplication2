package com.example.myapplication.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    // 无参数构造函数
    public User(String username, String password) {
    }

    // 带参数构造函数
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getter 和 Setter 方法
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRealName(String enteredRealName) {
    }

    public void setPhone(String enteredPhone) {
    }

    public void setHome(String enteredHome) {
    }

    public void setBirthday(String enteredBirthday) {
    }

    public boolean isValid() {
        return false;
    }
}


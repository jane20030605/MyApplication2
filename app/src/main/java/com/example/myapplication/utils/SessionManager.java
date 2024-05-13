package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private final SharedPreferences sharedPreferences; // 偏好設置
    private final SharedPreferences.Editor editor;
    private static final String PREF_NAME = "UserSession"; // 偏好設置名稱
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn"; // 登入狀態鍵名

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // 設置登入狀態
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    // 檢查用戶是否已登入
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // 登入方法
    public void login() {
        setLoggedIn(true);
    }

    // 登出方法
    public void logout() {
        setLoggedIn(false);
    }
}

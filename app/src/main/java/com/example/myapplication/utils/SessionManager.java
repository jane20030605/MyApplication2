package com.example.myapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String ACCOUNT = "SessionPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(ACCOUNT, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void login(String username) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(ACCOUNT, username);
        editor.apply();
    }

    public void logout() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(ACCOUNT);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getCurrentLoggedInAccount() {
        return prefs.getString(ACCOUNT, "");
    }
}
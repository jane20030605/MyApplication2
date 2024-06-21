package com.example.myapplication.models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class PreferenceUtils {

    private static final String PREFERENCE_NAME = "MyAppPreferences";
    private static final String KEY_NIGHT_MODE = "nightMode";

    public static void saveNightMode(Context context, int nightMode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_NIGHT_MODE, nightMode);
        editor.apply();
    }

    public static int getNightMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
}


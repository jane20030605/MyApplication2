package com.example.myapplication.ui.Calender;

import java.io.IOException;

public abstract class CalendarApiCallback<T> {
    public abstract void onSuccess(String result);

    public void onFailure(String message) {
    }

    public void onSuccess(T user) {
    }
    public void onDataReceived(String calendarData){

    }

    public void onFailure(IOException e) {
    }
}

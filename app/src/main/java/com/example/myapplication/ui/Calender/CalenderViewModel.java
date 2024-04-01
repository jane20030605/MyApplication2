package com.example.myapplication.ui.Calender;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;

public class CalenderViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CalenderViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Calender fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
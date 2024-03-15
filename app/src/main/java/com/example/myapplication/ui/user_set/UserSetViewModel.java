package com.example.myapplication.ui.user_set;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserSetViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public UserSetViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("系統設定");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
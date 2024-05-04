package com.example.myapplication.ui.Login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<String> mText; // 登入片段的文本

    public LoginViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Login fragment"); // 設置登入片段的文本
    }

    public LiveData<String> getText() {
        return mText;
    }
}

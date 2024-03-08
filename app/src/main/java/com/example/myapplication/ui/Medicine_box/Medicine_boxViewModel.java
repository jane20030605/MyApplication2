package com.example.myapplication.ui.Medicine_box;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Medicine_boxViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public Medicine_boxViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
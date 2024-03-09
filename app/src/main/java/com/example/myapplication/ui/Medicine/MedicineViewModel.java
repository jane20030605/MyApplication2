package com.example.myapplication.ui.Medicine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MedicineViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MedicineViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("藥物外觀資料查詢");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
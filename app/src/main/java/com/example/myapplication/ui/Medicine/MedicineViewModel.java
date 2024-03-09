package com.example.myapplication.ui.Medicine;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MedicineViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MedicineViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Medicine fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
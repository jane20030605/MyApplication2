package com.example.myapplication.ui.emergency;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EmergencyContactViewModel extends ViewModel {
    private final MutableLiveData<String> emergencyName = new MutableLiveData<>();
    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private final MutableLiveData<String> relationship = new MutableLiveData<>();

    // 返回緊急聯絡人姓名的 LiveData
    public LiveData<String> getEmergencyName() {
        return emergencyName;
    }

    // 返回電話號碼的 LiveData
    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    // 返回關係的 LiveData
    public LiveData<String> getRelationship() {
        return relationship;
    }

    // 設置緊急聯絡人信息
    public void setEmergencyContact(String name, String phone, String relation) {
        emergencyName.setValue(name);
        phoneNumber.setValue(phone);
        relationship.setValue(relation);
    }
}

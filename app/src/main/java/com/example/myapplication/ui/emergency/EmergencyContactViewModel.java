package com.example.myapplication.ui.emergency;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EmergencyContactViewModel extends ViewModel {
    private static final MutableLiveData<String> emergencyName = new MutableLiveData<>();
    private static final MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private static final MutableLiveData<String> relationship = new MutableLiveData<>();

    public static LiveData<String> getEmergencyName() {
        return emergencyName;
    }

    public static LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public static LiveData<String> getRelationship() {
        return relationship;
    }

    public void setEmergencyContact(String name, String phone, String relation) {
        emergencyName.setValue(name);
        phoneNumber.setValue(phone);
        relationship.setValue(relation);
    }
}

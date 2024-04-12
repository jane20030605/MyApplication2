package com.example.myapplication.ui.emergency;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EmergencyContactViewModel extends ViewModel {
    private MutableLiveData<String> emergencyName = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> relationship = new MutableLiveData<>();

    public LiveData<String> getEmergencyName() {
        return emergencyName;
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public LiveData<String> getRelationship() {
        return relationship;
    }

    public void setEmergencyContact(String name, String phone, String relation) {
        emergencyName.setValue(name);
        phoneNumber.setValue(phone);
        relationship.setValue(relation);
    }
}

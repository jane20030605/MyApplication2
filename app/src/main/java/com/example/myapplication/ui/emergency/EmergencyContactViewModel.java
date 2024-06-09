package com.example.myapplication.ui.emergency;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactViewModel extends ViewModel {
    // 用於存儲緊急聯絡人信息的 LiveData
    private MutableLiveData<String> emergencyName; // 緊急聯絡人姓名
    private MutableLiveData<String> phoneNumber; // 聯絡人電話號碼
    private MutableLiveData<String> relationship; // 聯絡人關係

    // 存儲所有緊急聯絡人的列表
    private List<JSONObject> emergencyContactList;

    public EmergencyContactViewModel() {
        emergencyName = new MutableLiveData<>();
        phoneNumber = new MutableLiveData<>();
        relationship = new MutableLiveData<>();

        // 初始化緊急聯絡人列表
        emergencyContactList = new ArrayList<>();
    }

    public LiveData<String> getEmergencyName() {
        return emergencyName;
    }

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public LiveData<String> getRelationship() {
        return relationship;
    }

    // 設置緊急聯絡人信息的方法
    public void setEmergencyContact(String name, String phone, String relation) {
        emergencyName.setValue(name);
        phoneNumber.setValue(phone);
        relationship.setValue(relation);

        // 創建一個 JSONObject 來表示緊急聯絡人
        JSONObject contact = new JSONObject();
        try {
            contact.put("emergency_name", name);
            contact.put("phone_number", phone);
            contact.put("relationship", relation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 將緊急聯絡人添加到列表中
        emergencyContactList.add(contact);
    }

    // 檢索更新後的緊急聯絡人列表的方法
    public List<JSONObject> getUpdatedContactList() {
        return emergencyContactList;
    }
}

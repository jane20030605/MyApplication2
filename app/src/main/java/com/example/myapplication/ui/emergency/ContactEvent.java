package com.example.myapplication.ui.emergency;

public class ContactEvent {
    private String contactName;
    private String contactTel;
    private String relation;

    // 建構函式
    public ContactEvent(String contactName, String contactTel, String relation) {
        this.contactName = contactName;
        this.contactTel = contactTel;
        this.relation = relation;
    }

    // 取得聯絡人姓名
    public String getContactName() {
        return contactName;
    }

    // 設置聯絡人姓名
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    // 取得聯絡人電話號碼
    public String getContactTel() {
        return contactTel;
    }

    // 設置聯絡人電話號碼
    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    // 取得與使用者的關係
    public String getRelation() {
        return relation;
    }

    // 設置與使用者的關係
    public void setRelation(String relation) {
        this.relation = relation;
    }
}

package com.example.myapplication.ui.emergency;

public class ContactEvent {
    private String contactName;
    private String contactTel;
    private String contactMail; // 新增的電子郵件欄位
    private String relation;
    private String account;

    public ContactEvent(String contactName, String contactTel, String contactMail, String relation, String account) {
        this.contactName = contactName;
        this.contactTel = contactTel;
        this.contactMail = contactMail; // 初始化電子郵件欄位
        this.relation = relation;
        this.account = account;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTel() {
        return contactTel;
    }

    public void setContactTel(String contactTel) {
        this.contactTel = contactTel;
    }

    public String getContactMail() {
        return contactMail;
    }

    public void setContactMail(String contactMail) {
        this.contactMail = contactMail;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

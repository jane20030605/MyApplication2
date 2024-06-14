package com.example.myapplication.ui.emergency;

public class ContactEvent {
    private String contactName;
    private String contactTel;
    private String relation;
    private String ACCOUNT;

    public ContactEvent(String contactName, String contactTel, String relation,String ACCOUNT) {
        this.contactName = contactName;
        this.contactTel = contactTel;
        this.relation = relation;
        this.ACCOUNT = ACCOUNT;
    }
    public String getAccount() {
        return ACCOUNT;
    }

    public void setAccount(String ACCOUNT) {
        this.ACCOUNT = ACCOUNT;
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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }
}

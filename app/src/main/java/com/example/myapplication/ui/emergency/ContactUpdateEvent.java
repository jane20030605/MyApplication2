// ContactUpdateEvent.java

package com.example.myapplication.ui.emergency;

public class ContactUpdateEvent {
    private String contactName;
    private String contactTel;
    private String relation;
    private String contactId;  // 修正命名
    private String account;    // 修正命名

    public ContactUpdateEvent() {
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
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

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getAllValue() {
        return "Contact Name: " + contactName + "\n" +
                "Contact Tel: " + contactTel + "\n" +
                "Relation: " + relation + "\n" +
                "Contact ID: " + contactId + "\n" +
                "Account: " + account;
    }
}
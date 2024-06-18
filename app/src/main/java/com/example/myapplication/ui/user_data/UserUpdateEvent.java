package com.example.myapplication.ui.user_data;

public class UserUpdateEvent {
    private String name;
    private String tel;
    private String mail;
    private String address;
    private String birthday;
    private String account;

    public UserUpdateEvent() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAllValue() {
        return "Name: " + name + "\n" +
                "Tel: " + tel + "\n" +
                "Mail: " + mail + "\n" +
                "Address: " + address + "\n" +
                "Birthday: " + birthday + "\n" +
                "Account: " + account;
    }
}

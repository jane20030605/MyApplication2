package com.example.myapplication.ui.user_data;

public class UserUpdateEvent {
    private String name;
    private String tel;
    private String birthday;
    private String mail;
    private String address;
    private String account;

    public UserUpdateEvent(String name, String tel, String birthday, String mail, String address, String account) {
        this.name = name;
        this.tel = tel;
        this.birthday = birthday;
        this.mail = mail;
        this.address = address;
        this.account = account;
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

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "UserUpdateEvent{" +
                "name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", birthday='" + birthday + '\'' +
                ", mail='" + mail + '\'' +
                ", address='" + address + '\'' +
                ", account='" + account + '\'' +
                '}';
    }
}

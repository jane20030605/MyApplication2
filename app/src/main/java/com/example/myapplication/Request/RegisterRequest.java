package com.example.myapplication.Request;

public class RegisterRequest {
    private final String enterUsername;
    private final String enterPassword;
    private final String enterRealname;
    private final String enterPhone;
    private final String enterEmail;
    private final String enterHome;
    private final String enterBirthday;

    public RegisterRequest(
            String enteredUsername, String enteredPassword,
            String enteredRealName, String enteredPhone,
            String enteredEmail, String enteredHome, String enteredBirthday) {

        this.enterUsername = enteredUsername;
        this.enterPassword = enteredPassword;
        this.enterRealname = enteredRealName;
        this.enterPhone = enteredPhone;
        this.enterEmail = enteredEmail;
        this.enterHome = enteredHome;
        this.enterBirthday = enteredBirthday;


    }
}

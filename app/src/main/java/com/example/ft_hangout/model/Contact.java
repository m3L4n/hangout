package com.example.ft_hangout.model;

public class Contact {

    private String mLastName;
    private String mFirstName;
    private String mBirthday;
    private String mEmail;
    private String mNumber;


    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }
    public String getLastName() {
        return mLastName;
    }
    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }
    public String getEmail() {
        return mEmail;
    }
    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public String getBirthday() {
        return mBirthday;
    }

    public void setBirthday(String mBirthday) {
        this.mBirthday = mBirthday;
    }

}

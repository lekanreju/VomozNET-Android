package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 7/21/16.
 */
public class GetMemberRequest {
    private String email;
    private String password;
    private String centerCardId;
    private String phoneNumber;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCenterCardId() {
        return centerCardId;
    }

    public void setCenterCardId(String centerCardId) {
        this.centerCardId = centerCardId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

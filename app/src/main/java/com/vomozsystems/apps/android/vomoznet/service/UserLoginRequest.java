package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 6/18/16.
 */
public class UserLoginRequest {
    private String email;
    private String password;
    private Long centerCardId;

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

    public Long getCenterCardId() {
        return centerCardId;
    }

    public void setCenterCardId(Long centerCardId) {
        this.centerCardId = centerCardId;
    }
}

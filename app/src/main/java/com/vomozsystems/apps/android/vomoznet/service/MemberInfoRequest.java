package com.vomozsystems.apps.android.vomoznet.service;

public class MemberInfoRequest {

    private String phoneNumber;
    private String email;
    private String password;
    private Long centerCardId;

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the centerCardId
     */
    public Long getCenterCardId() {
        return centerCardId;
    }

    /**
     * @param centerCardId the centerCardId to set
     */
    public void setCenterCardId(Long centerCardId) {
        this.centerCardId = centerCardId;
    }

}

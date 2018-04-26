package com.vomozsystems.apps.android.vomoznet.service;

public class DoSignUpRequest {

    private String callerId;
    private Long donationCenterId;
    private String email;
    private String password;

    /**
     * @return the callerId
     */
    public String getCallerId() {
        return callerId;
    }

    /**
     * @param callerId the callerId to set
     */
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    /**
     * @return the donationCenterId
     */
    public Long getDonationCenterId() {
        return donationCenterId;
    }

    /**
     * @param donationCenterId the donationCenterId to set
     */
    public void setDonationCenterId(Long donationCenterId) {
        this.donationCenterId = donationCenterId;
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
}

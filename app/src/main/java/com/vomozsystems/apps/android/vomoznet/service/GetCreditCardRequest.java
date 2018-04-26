package com.vomozsystems.apps.android.vomoznet.service;

public class GetCreditCardRequest {

    private String email;
    private String texterCardId;
    private Long donationCenterCardId;
    private String password;
    private String callerId;

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
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
     * @return the texterCardId
     */
    public String getTexterCardId() {
        return texterCardId;
    }

    /**
     * @param texterCardId the texterCardId to set
     */
    public void setTexterCardId(String texterCardId) {
        this.texterCardId = texterCardId;
    }

    /**
     * @return the donationCenterCardId
     */
    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }

    /**
     * @param donationCenterCardId the donationCenterCardId to set
     */
    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
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

package com.vomozsystems.apps.android.vomoznet.service;

public class ResetPasswordRequest {

    private String newPassword;
    private Long donationCenterId;
    private String phoneNumber;

    /**
     * @return the newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @param newPassword the newPassword to set
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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


}

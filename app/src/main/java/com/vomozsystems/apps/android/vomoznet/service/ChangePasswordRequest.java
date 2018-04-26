package com.vomozsystems.apps.android.vomoznet.service;

public class ChangePasswordRequest {

    private String email;
    private String newPassword;
    private String oldPassword;
    private Long donationCenterId;
    private String phoneNumber;

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
     * @return the oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * @param oldPassword the oldPassword to set
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

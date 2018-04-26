package com.vomozsystems.apps.android.vomoznet.service;

public class ChangeDefaultDonationCenterRequest {

    private String callerId;
    private Long newDonationCenterId;
    private Long currentDonationCenterId;
    private String merchantIdCode;
    private Long currentTexterCardId;
    private Long newTexterCardId;
    private String password;
    private String profileImage;

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public Long getCurrentDonationCenterId() {
        return currentDonationCenterId;
    }

    public void setCurrentDonationCenterId(Long currentDonationCenterId) {
        this.currentDonationCenterId = currentDonationCenterId;
    }

    public Long getCurrentTexterCardId() {
        return currentTexterCardId;
    }

    public void setCurrentTexterCardId(Long currentTexterCardId) {
        this.currentTexterCardId = currentTexterCardId;
    }

    public String getMerchantIdCode() {
        return merchantIdCode;
    }

    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }

    public Long getNewDonationCenterId() {
        return newDonationCenterId;
    }

    public void setNewDonationCenterId(Long newDonationCenterId) {
        this.newDonationCenterId = newDonationCenterId;
    }

    public Long getNewTexterCardId() {
        return newTexterCardId;
    }

    public void setNewTexterCardId(Long newTexterCardId) {
        this.newTexterCardId = newTexterCardId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}

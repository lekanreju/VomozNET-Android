package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 11/12/16.
 */

public class ChangeDonationCenter {
    private Long donationCenterId;
    private String merchantIdCode;
    private Long texterCardId;
    private String profileImage;
    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public Long getDonationCenterId() {
        return donationCenterId;
    }

    public void setDonationCenterId(Long donationCenterId) {
        this.donationCenterId = donationCenterId;
    }

    public String getMerchantIdCode() {
        return merchantIdCode;
    }

    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Long getTexterCardId() {
        return texterCardId;
    }

    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }
}

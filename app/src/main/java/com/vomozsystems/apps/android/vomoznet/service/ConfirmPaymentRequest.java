package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Donation;

import java.util.List;

public class ConfirmPaymentRequest {

    private String paymentId;
    private Long donationCenterCardId;
    private Long texterCardId;
    private List<Donation> donations;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }

    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
    }

    public Long getTexterCardId() {
        return texterCardId;
    }

    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }
}

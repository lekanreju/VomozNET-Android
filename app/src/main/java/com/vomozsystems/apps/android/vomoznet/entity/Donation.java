package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 8/14/16.
 */
public class Donation {

    private Double amount;
    private String donationTypeCode;
    private String donationTypeDescription;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDonationTypeCode() {
        return donationTypeCode;
    }

    public void setDonationTypeCode(String donationTypeCode) {
        this.donationTypeCode = donationTypeCode;
    }

    public String getDonationTypeDescription() {
        return donationTypeDescription;
    }

    public void setDonationTypeDescription(String donationTypeDescription) {
        this.donationTypeDescription = donationTypeDescription;
    }
}

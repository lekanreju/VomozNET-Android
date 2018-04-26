package com.vomozsystems.apps.android.vomoznet.entity;

/**
 * Created by leksrej on 2/24/18.
 */

public class TotalDonation {
    private Double totalAmount;
    private String currency;

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

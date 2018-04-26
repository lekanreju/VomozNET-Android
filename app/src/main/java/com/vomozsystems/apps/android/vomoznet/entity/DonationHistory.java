package com.vomozsystems.apps.android.vomoznet.entity;

import java.util.Date;

import io.realm.annotations.PrimaryKey;

public class DonationHistory {

    @PrimaryKey
    private Long donationId;
    private Integer creditCardId;
    private String creditCardLast4;
    private Long texterCardId;
    private Double amount;
    private String currency;
    private String donationTypeCode;
    private String donationTypeDescription;
    private String merchantIdCode;
    private Long donationCenterCardId;
    private String transactionSource;
    private String approvalCode;
    private String transactionId;
    private Date date;
    private String paymentMode;
    private String donationCenterName;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * @return the donationId
     */
    public Long getDonationId() {
        return donationId;
    }

    /**
     * @param donationId the donationId to set
     */
    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    /**
     * @return the creditCardId
     */
    public Integer getCreditCardId() {
        return creditCardId;
    }

    /**
     * @param creditCardId the creditCardId to set
     */
    public void setCreditCardId(Integer creditCardId) {
        this.creditCardId = creditCardId;
    }

    /**
     * @return the creditCardLast4
     */
    public String getCreditCardLast4() {
        return creditCardLast4;
    }

    /**
     * @param creditCardLast4 the creditCardLast4 to set
     */
    public void setCreditCardLast4(String creditCardLast4) {
        this.creditCardLast4 = creditCardLast4;
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

    public Double getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    /**
     * @return the donationTypeCode
     */
    public String getDonationTypeCode() {
        return donationTypeCode;
    }

    /**
     * @param donationTypeCode the donationTypeCode to set
     */
    public void setDonationTypeCode(String donationTypeCode) {
        this.donationTypeCode = donationTypeCode;
    }

    /**
     * @return the donationTypeDescription
     */
    public String getDonationTypeDescription() {
        return donationTypeDescription;
    }

    /**
     * @param donationTypeDescription the donationTypeDescription to set
     */
    public void setDonationTypeDescription(String donationTypeDescription) {
        this.donationTypeDescription = donationTypeDescription;
    }

    /**
     * @return the merchantIdCode
     */
    public String getMerchantIdCode() {
        return merchantIdCode;
    }

    /**
     * @param merchantIdCode the merchantIdCode to set
     */
    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }

    /**
     * @return the transactionSource
     */
    public String getTransactionSource() {
        return transactionSource;
    }

    /**
     * @param transactionSource the transactionSource to set
     */
    public void setTransactionSource(String transactionSource) {
        this.transactionSource = transactionSource;
    }

    /**
     * @return the approvalCode
     */
    public String getApprovalCode() {
        return approvalCode;
    }

    /**
     * @param approvalCode the approvalCode to set
     */
    public void setApprovalCode(String approvalCode) {
        this.approvalCode = approvalCode;
    }

    /**
     * @return the transactionId
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * @param transactionId the transactionId to set
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the paymentMode
     */
    public String getPaymentMode() {
        return paymentMode;
    }

    /**
     * @param paymentMode the paymentMode to set
     */
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getDonationCenterName() {
        return donationCenterName;
    }

    public void setDonationCenterName(String donationCenterName) {
        this.donationCenterName = donationCenterName;
    }
}

package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 3/5/18.
 */

public class LogPaymentRequest {
    private String universalAuthToken;
    private String processerTransactionId;
    private String processerTotalAmountContributed;
    private String processerName;
    private String processorCurrencyCode;
    private Long defaultDonationCenterId;
    private String defaultMerchantIdCode;
    private Long receivingDonationCenterId;
    private String receivingMerchantIdCode;
    private Long defaultUniqueCCId;
    private String defaultIsReceiving;
    private String giverPhoneNumber;
    private String giverFirstName;
    private String giverLastName;
    private String giverEmail;
    private Double amount1;
    private Double amount2;
    private Double amount3;
    private String contributionType1;
    private String contributionType2;
    private String contributionType3;
    private String serviceName;

    public String getUniversalAuthToken() {
        return universalAuthToken;
    }
    public void setUniversalAuthToken(String universalAuthToken) {
        this.universalAuthToken = universalAuthToken;
    }
    public String getProcesserTransactionId() {
        return processerTransactionId;
    }
    public void setProcesserTransactionId(String processerTransactionId) {
        this.processerTransactionId = processerTransactionId;
    }
    public String getProcesserTotalAmountContributed() {
        return processerTotalAmountContributed;
    }
    public void setProcesserTotalAmountContributed(
            String processerTotalAmountContributed) {
        this.processerTotalAmountContributed = processerTotalAmountContributed;
    }
    public String getProcesserName() {
        return processerName;
    }
    public void setProcesserName(String processerName) {
        this.processerName = processerName;
    }
    public String getProcessorCurrencyCode() {
        return processorCurrencyCode;
    }
    public void setProcessorCurrencyCode(String processorCurrencyCode) {
        this.processorCurrencyCode = processorCurrencyCode;
    }
    public Long getDefaultDonationCenterId() {
        return defaultDonationCenterId;
    }
    public void setDefaultDonationCenterId(Long defaultDonationCenterId) {
        this.defaultDonationCenterId = defaultDonationCenterId;
    }
    public String getDefaultMerchantIdCode() {
        return defaultMerchantIdCode;
    }
    public void setDefaultMerchantIdCode(String defaultMerchantIdCode) {
        this.defaultMerchantIdCode = defaultMerchantIdCode;
    }
    public Long getReceivingDonationCenterId() {
        return receivingDonationCenterId;
    }
    public void setReceivingDonationCenterId(Long receivingDonationCenterId) {
        this.receivingDonationCenterId = receivingDonationCenterId;
    }
    public String getReceivingMerchantIdCode() {
        return receivingMerchantIdCode;
    }
    public void setReceivingMerchantIdCode(String receivingMerchantIdCode) {
        this.receivingMerchantIdCode = receivingMerchantIdCode;
    }
    public Long getDefaultUniqueCCId() {
        return defaultUniqueCCId;
    }
    public void setDefaultUniqueCCId(Long defaultUniqueCCId) {
        this.defaultUniqueCCId = defaultUniqueCCId;
    }
    public String getDefaultIsReceiving() {
        return defaultIsReceiving;
    }
    public void setDefaultIsReceiving(String defaultIsReceiving) {
        this.defaultIsReceiving = defaultIsReceiving;
    }
    public String getGiverPhoneNumber() {
        return giverPhoneNumber;
    }
    public void setGiverPhoneNumber(String giverPhoneNumber) {
        this.giverPhoneNumber = giverPhoneNumber;
    }
    public String getGiverFirstName() {
        return giverFirstName;
    }
    public void setGiverFirstName(String giverFirstName) {
        this.giverFirstName = giverFirstName;
    }
    public String getGiverLastName() {
        return giverLastName;
    }
    public void setGiverLastName(String giverLastName) {
        this.giverLastName = giverLastName;
    }
    public String getGiverEmail() {
        return giverEmail;
    }
    public void setGiverEmail(String giverEmail) {
        this.giverEmail = giverEmail;
    }
    public Double getAmount1() {
        return amount1;
    }
    public void setAmount1(Double amount1) {
        this.amount1 = amount1;
    }
    public Double getAmount2() {
        return amount2;
    }
    public void setAmount2(Double amount2) {
        this.amount2 = amount2;
    }
    public Double getAmount3() {
        return amount3;
    }
    public void setAmount3(Double amount3) {
        this.amount3 = amount3;
    }
    public String getContributionType1() {
        return contributionType1;
    }
    public void setContributionType1(String contributionType1) {
        this.contributionType1 = contributionType1;
    }
    public String getContributionType2() {
        return contributionType2;
    }
    public void setContributionType2(String contributionType2) {
        this.contributionType2 = contributionType2;
    }
    public String getContributionType3() {
        return contributionType3;
    }
    public void setContributionType3(String contributionType3) {
        this.contributionType3 = contributionType3;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}

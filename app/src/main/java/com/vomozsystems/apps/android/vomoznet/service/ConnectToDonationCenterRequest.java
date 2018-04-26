package com.vomozsystems.apps.android.vomoznet.service;

import java.util.Date;

public class ConnectToDonationCenterRequest {

    private String callerId;
    private String toDonationCenterCardId;
    private String merchantIdCode;
    private String texterCardId;
    private String fromDonationCenterCardId;
    private Date date = new Date();

    /**
     * @return the callerId
     */
    public String getCallerId() {
        return callerId;
    }

    /**
     * @param callerId the callerId to set
     */
    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getToDonationCenterCardId() {
        return toDonationCenterCardId;
    }

    public void setToDonationCenterCardId(String toDonationCenterCardId) {
        this.toDonationCenterCardId = toDonationCenterCardId;
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

    public String getFromDonationCenterCardId() {
        return fromDonationCenterCardId;
    }

    public void setFromDonationCenterCardId(String fromDonationCenterCardId) {
        this.fromDonationCenterCardId = fromDonationCenterCardId;
    }
}

package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.CreditCard;
import com.vomozsystems.apps.android.vomoznet.entity.Donation;

import java.util.List;

public class MakeDonationRequest {

    private CreditCard creditCard;
    private Long receivingDonationCenterCardId;
    private Long defaultDonationCenterCardId;
    private Long texterCardId;
    private String callerId;
    private List<Donation> donations;
    private String merchantIdCode;

    /**
     * @return the creditCard
     */
    public CreditCard getCreditCard() {
        return creditCard;
    }

    /**
     * @param creditCard the creditCard to set
     */
    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * @return the texterCardId
     */
    public Long getTexterCardId() {
        return texterCardId;
    }

    /**
     * @param texterCardId the texterCardId to set
     */
    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }

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

    /**
     * @return the receivingDonationCenterCardId
     */
    public Long getReceivingDonationCenterCardId() {
        return receivingDonationCenterCardId;
    }

    /**
     * @param receivingDonationCenterCardId the receivingDonationCenterCardId to set
     */
    public void setReceivingDonationCenterCardId(Long receivingDonationCenterCardId) {
        this.receivingDonationCenterCardId = receivingDonationCenterCardId;
    }

    /**
     * @return the defaultDonationCenterCardId
     */
    public Long getDefaultDonationCenterCardId() {
        return defaultDonationCenterCardId;
    }

    /**
     * @param defaultDonationCenterCardId the defaultDonationCenterCardId to set
     */
    public void setDefaultDonationCenterCardId(Long defaultDonationCenterCardId) {
        this.defaultDonationCenterCardId = defaultDonationCenterCardId;
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

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }
}

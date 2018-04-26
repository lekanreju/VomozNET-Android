package com.vomozsystems.apps.android.vomoznet.service;

public class GetDonationHistoryRequest {

    private Long donationCenterCardId;
    private Long texterCardId;
    private String callerId;
    private String startDate;
    private String endDate;
    private String donationType;

    /**
     * @return the donationCenterCardId
     */
    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }

    /**
     * @param donationCenterCardId the donationCenterCardId to set
     */
    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }
}

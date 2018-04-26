package com.vomozsystems.apps.android.vomoznet.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class DonationType extends RealmObject {

    @PrimaryKey
    private Integer autoId;
    private String code;
    private String description;
    @Required
    private Long donationCenterCardId;
    private String merchantIdCode;
    /**
     * @return the autoId
     */
    public Integer getAutoId() {
        return autoId;
    }

    /**
     * @param autoId the autoId to set
     */
    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getDonationCenterCardId() {
        return donationCenterCardId;
    }

    public void setDonationCenterCardId(Long donationCenterCardId) {
        this.donationCenterCardId = donationCenterCardId;
    }

    public String getMerchantIdCode() {
        return merchantIdCode;
    }

    public void setMerchantIdCode(String merchantIdCode) {
        this.merchantIdCode = merchantIdCode;
    }
}

package com.vomozsystems.apps.android.vomoznet.service;

import com.google.gson.annotations.SerializedName;

/**
 * Created by leksrej on 8/23/16.
 */
public class Information {
    @SerializedName("xprocess_card_message")
    private String cardMessage;

    @SerializedName("xamount_contributed")
    private Double amountContributed;

    @SerializedName("xold_balance")
    private String oldBalance;

    @SerializedName("xnew_balance")
    private String newBalance;

    @SerializedName("xbonus_credit")
    private String bonusCredit;

    @SerializedName("xreturn_message")
    private String returnMessage;

    @SerializedName("xold_donationcenter")
    private Long oldDonationCenterCardId;

    @SerializedName("xold_dcenter_merchantcode")
    private String oldMerchantIdCode;

    @SerializedName("xold_texter_cc_card_id")
    private Long oldTexterCardId;

    @SerializedName("xnew_donationcenter")
    private Long newDonationCenterCardId;

    @SerializedName("xnew_dcenter_merchantcode")
    private String newMerchantIdCode;

    @SerializedName("xnew_texter_cc_card_id")
    private Long newTexterCardId;

    public Double getAmountContributed() {
        return amountContributed;
    }

    public void setAmountContributed(Double amountContributed) {
        this.amountContributed = amountContributed;
    }

    public String getBonusCredit() {
        return bonusCredit;
    }

    public void setBonusCredit(String bonusCredit) {
        this.bonusCredit = bonusCredit;
    }

    public String getCardMessage() {
        return cardMessage;
    }

    public void setCardMessage(String cardMessage) {
        this.cardMessage = cardMessage;
    }

    public String getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(String newBalance) {
        this.newBalance = newBalance;
    }

    public String getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(String oldBalance) {
        this.oldBalance = oldBalance;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Long getOldDonationCenterCardId() {
        return oldDonationCenterCardId;
    }

    public void setOldDonationCenterCardId(Long oldDonationCenterCardId) {
        this.oldDonationCenterCardId = oldDonationCenterCardId;
    }

    public String getOldMerchantIdCode() {
        return oldMerchantIdCode;
    }

    public void setOldMerchantIdCode(String oldMerchantIdCode) {
        this.oldMerchantIdCode = oldMerchantIdCode;
    }

    public Long getOldTexterCardId() {
        return oldTexterCardId;
    }

    public void setOldTexterCardId(Long oldTexterCardId) {
        this.oldTexterCardId = oldTexterCardId;
    }

    public Long getNewDonationCenterCardId() {
        return newDonationCenterCardId;
    }

    public void setNewDonationCenterCardId(Long newDonationCenterCardId) {
        this.newDonationCenterCardId = newDonationCenterCardId;
    }

    public String getNewMerchantIdCode() {
        return newMerchantIdCode;
    }

    public void setNewMerchantIdCode(String newMerchantIdCode) {
        this.newMerchantIdCode = newMerchantIdCode;
    }

    public Long getNewTexterCardId() {
        return newTexterCardId;
    }

    public void setNewTexterCardId(Long newTexterCardId) {
        this.newTexterCardId = newTexterCardId;
    }
}
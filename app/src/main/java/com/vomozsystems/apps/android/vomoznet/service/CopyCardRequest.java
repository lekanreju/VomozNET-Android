package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 12/20/17.
 */

public class CopyCardRequest {
    private String texterCardId;
    private String newTexterCardId;
    private String newMerchantIdCode;
    public String getTexterCardId() {
        return texterCardId;
    }
    public void setTexterCardId(String texterCardId) {
        this.texterCardId = texterCardId;
    }
    public String getNewTexterCardId() {
        return newTexterCardId;
    }
    public void setNewTexterCardId(String newTexterCardId) {
        this.newTexterCardId = newTexterCardId;
    }
    public String getNewMerchantIdCode() {
        return newMerchantIdCode;
    }
    public void setNewMerchantIdCode(String newMerchantIdCode) {
        this.newMerchantIdCode = newMerchantIdCode;
    }
}

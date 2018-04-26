package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 9/17/16.
 */
public class UserExistsInDonationCenterResponse extends BaseServiceResponse {
    private String responseData;
    public String getResponseData() {
        return responseData;
    }
    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}

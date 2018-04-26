package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 9/13/16.
 */
public class PaypalConfirmationResponse extends BaseServiceResponse {
    private String responseData;

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}

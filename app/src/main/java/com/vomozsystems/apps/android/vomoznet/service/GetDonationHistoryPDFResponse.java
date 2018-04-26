package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 8/16/16.
 */
public class GetDonationHistoryPDFResponse extends BaseServiceResponse {

    Byte[] responseData;

    public Byte[] getResponseData() {
        return responseData;
    }

    public void setResponseData(Byte[] responseData) {
        this.responseData = responseData;
    }
}

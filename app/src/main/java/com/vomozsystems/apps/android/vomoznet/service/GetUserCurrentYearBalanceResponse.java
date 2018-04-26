package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 9/18/16.
 */
public class GetUserCurrentYearBalanceResponse extends BaseServiceResponse {
    private Double responseData;

    public Double getResponseData() {
        return responseData;
    }

    public void setResponseData(Double responseData) {
        this.responseData = responseData;
    }
}

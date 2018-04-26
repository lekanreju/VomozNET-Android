package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 6/18/16.
 */
public class UserExistsResponse extends BaseServiceResponse {
    private String responseData;

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
}

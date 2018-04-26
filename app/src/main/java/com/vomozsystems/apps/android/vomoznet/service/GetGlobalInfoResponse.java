package com.vomozsystems.apps.android.vomoznet.service;

/**
 * Created by leksrej on 7/5/17.
 */

public class GetGlobalInfoResponse extends BaseServiceResponse {
    private VomozGlobalInfo responseData;

    public VomozGlobalInfo getResponseData() {
        return responseData;
    }

    public void setResponseData(VomozGlobalInfo responseData) {
        this.responseData = responseData;
    }
}

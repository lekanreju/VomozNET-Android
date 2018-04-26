package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.ReferenceData;

/**
 * Created by leksrej on 7/13/16.
 */
public class GetReferenceDataResponse extends BaseServiceResponse {

    private ReferenceData responseData;

    public ReferenceData getResponseData() {
        return responseData;
    }

    public void setResponseData(ReferenceData responseData) {
        this.responseData = responseData;
    }
}

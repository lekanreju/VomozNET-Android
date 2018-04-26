package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Personal;

import java.util.List;

/**
 * Created by leksrej on 3/11/18.
 */

public class GetPersonalInfoResponse extends BaseServiceResponse {
    private List<Personal> responseData;

    public List<Personal> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<Personal> responseData) {
        this.responseData = responseData;
    }
}
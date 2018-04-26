package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;

import java.util.List;

/**
 * Created by leksrej on 7/20/16.
 */
public class DonationCenterResponse extends BaseServiceResponse {
    private List<DonationCenter> responseData;

    public List<DonationCenter> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<DonationCenter> responseData) {
        this.responseData = responseData;
    }
}

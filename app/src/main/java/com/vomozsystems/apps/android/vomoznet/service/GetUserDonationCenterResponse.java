package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;

import java.util.List;

/**
 * Created by leksrej on 7/18/17.
 */

public class GetUserDonationCenterResponse extends BaseServiceResponse {
    List<DonationCenter> responseData;

    public List<DonationCenter> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<DonationCenter> responseData) {
        this.responseData = responseData;
    }
}

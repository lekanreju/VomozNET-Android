package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.DonationHistory;

import java.util.List;

/**
 * Created by leksrej on 7/31/16.
 */
public class DonationResponse extends BaseServiceResponse {
    List<DonationHistory> responseData;

    public List<DonationHistory> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<DonationHistory> responseData) {
        this.responseData = responseData;
    }
}

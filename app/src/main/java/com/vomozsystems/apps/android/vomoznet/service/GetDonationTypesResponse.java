package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.DonationType;

import java.util.List;

/**
 * Created by leksrej on 7/18/16.
 */
public class GetDonationTypesResponse extends BaseServiceResponse {

    private List<DonationType> responseData;

    public List<DonationType> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<DonationType> responseData) {
        this.responseData = responseData;
    }
}

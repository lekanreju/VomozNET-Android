package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Slider;

import java.util.List;

/**
 * Created by leksrej on 4/2/18.
 */

public class DonationCenterSlidersResponse extends BaseServiceResponse {
    private List<Slider> responseData;

    public List<Slider> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<Slider> responseData) {
        this.responseData = responseData;
    }
}

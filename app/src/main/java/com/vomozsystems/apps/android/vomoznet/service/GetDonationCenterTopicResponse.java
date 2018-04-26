package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.DonationCenterTopic;

import java.util.List;

/**
 * Created by leksrej on 8/29/16.
 */
public class GetDonationCenterTopicResponse extends BaseServiceResponse {
    private List<DonationCenterTopic> responseData;

    public List<DonationCenterTopic> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<DonationCenterTopic> responseData) {
        this.responseData = responseData;
    }
}

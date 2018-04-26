package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Event;

import java.util.List;

/**
 * Created by leksrej on 9/5/16.
 */
public class GetDonationCenterEventsResponse extends BaseServiceResponse {
    private List<Event> responseData;

    public List<Event> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<Event> responseData) {
        this.responseData = responseData;
    }
}

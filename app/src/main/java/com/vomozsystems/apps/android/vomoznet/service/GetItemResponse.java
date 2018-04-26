package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Item;

import java.util.List;

/**
 * Created by leksrej on 2/17/17.
 */

public class GetItemResponse extends BaseServiceResponse {
    List<Item> responseData;

    public List<Item> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<Item> responseData) {
        this.responseData = responseData;
    }
}

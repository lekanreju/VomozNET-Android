package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Child;

import java.util.List;

/**
 * Created by leksrej on 5/8/18.
 */

public class GetMemberChildrenResponse extends BaseServiceResponse {
    private List<Child> responseData;

    public List<Child> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<Child> responseData) {
        this.responseData = responseData;
    }
}

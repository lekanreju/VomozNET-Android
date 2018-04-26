package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.User;

/**
 * Created by leksrej on 6/18/16.
 */
public class UserLoginResponse extends BaseServiceResponse {

    private User responseData;

    public User getResponseData() {
        return responseData;
    }

    public void setResponseData(User responseData) {
        this.responseData = responseData;
    }
}

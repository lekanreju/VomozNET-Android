package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Member;

/**
 * Created by leksrej on 7/21/16.
 */
public class GetMemberResponse {
    private Member responseData;

    public Member getResponseData() {
        return responseData;
    }

    public void setResponseData(Member responseData) {
        this.responseData = responseData;
    }
}

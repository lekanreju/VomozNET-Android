package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.AmountContributed;

/**
 * Created by leksrej on 3/7/18.
 */

public class GetAllBalanceResponse extends BaseServiceResponse {
    private AmountContributed responseData;

    public AmountContributed getResponseData() {
        return responseData;
    }

    public void setResponseData(AmountContributed responseData) {
        this.responseData = responseData;
    }
}

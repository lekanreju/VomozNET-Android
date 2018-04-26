package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.CreditCard;

/**
 * Created by leksrej on 8/7/16.
 */
public class CreateCreditCardResponse extends BaseServiceResponse {
    CreditCard responseData;

    public CreditCard getResponseData() {
        return responseData;
    }

    public void setResponseData(CreditCard responseData) {
        this.responseData = responseData;
    }
}

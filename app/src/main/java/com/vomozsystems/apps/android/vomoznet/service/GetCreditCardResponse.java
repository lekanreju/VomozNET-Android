package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.CreditCard;

import java.util.List;

/**
 * Created by leksrej on 8/7/16.
 */
public class GetCreditCardResponse extends BaseServiceResponse {
    List<CreditCard> responseData;

    public List<CreditCard> getResponseData() {
        return responseData;
    }

    public void setResponseData(List<CreditCard> responseData) {
        this.responseData = responseData;
    }
}

package com.vomozsystems.apps.android.vomoznet.entity;

import java.util.Map;

/**
 * Created by leksrej on 2/25/18.
 */

public class PaymentInfo {
    private ColorWrapper colorWrapper;
    private CreditCard creditCard;
    private Map<String, String> paymentEngine;

    public ColorWrapper getColorWrapper() {
        return colorWrapper;
    }

    public void setColorWrapper(ColorWrapper colorWrapper) {
        this.colorWrapper = colorWrapper;
    }

    public Map<String, String> getPaymentEngine() {
        return paymentEngine;
    }

    public void setPaymentEngine(Map<String, String> paymentEngine) {
        this.paymentEngine = paymentEngine;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}

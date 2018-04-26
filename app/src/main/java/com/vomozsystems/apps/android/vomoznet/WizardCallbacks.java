package com.vomozsystems.apps.android.vomoznet;

import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;

/**
 * Created by leksrej on 2/25/18.
 */

public interface WizardCallbacks {
    void onNext(String currentPageName, PaymentInfo paymentInfo);
    void onPrevious(String currentPageName, PaymentInfo paymentInfo);
    void onCancel(PaymentInfo paymentInfo);
    void onFinish(PaymentInfo paymentInfo);
    void onStartOver(PaymentInfo paymentInfo);
    void setTitle(String title);
}

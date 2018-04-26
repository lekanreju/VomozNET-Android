package com.vomozsystems.apps.android.vomoznet.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leksrej on 7/17/17.
 */

public class VerifiedPhoneNumber extends RealmObject {
    @PrimaryKey
    private String mobileNumber;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}

package com.vomozsystems.apps.android.vomoznet;

import android.app.Application;

import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import co.paystack.android.PaystackSdk;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by leksrej on 9/6/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        PaystackSdk.initialize(getApplicationContext());
        RealmConfiguration realmConfiguration =
                new RealmConfiguration.Builder().schemaVersion(ApplicationUtils.CURRENT_REALM_VERSION).name(getResources().getString(R.string.org_filter)).build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    @Override
    public void onTerminate() {
        Realm.getDefaultInstance().close();
        super.onTerminate();
    }
}

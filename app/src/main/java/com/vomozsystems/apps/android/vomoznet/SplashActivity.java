package com.vomozsystems.apps.android.vomoznet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.splunk.mint.Mint;


/**
 * Created by leksrej on 8/30/16.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.initAndStartSession(this.getApplication(), "36cc1bd3");
        Intent intent = new Intent(this, AgreementActivity.class);
        startActivity(intent);
        finish();
    }
}

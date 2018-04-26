package com.vomozsystems.apps.android.vomoznet;

/**
 * Created by leksrej on 8/27/16.
 */

import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class VomozPayFirebaseInstanceIDService extends FirebaseInstanceIdService {

    public static final String REGISTRATION_TOKEN = "VOMOZPAY_REGISTRATION_TOKEN";
    public static final String REGISTRATION_TOKEN_SAVED = "VOMOZPAY_REGISTRATION_TOKEN_SAVED";
    private RealmConfiguration config;
    private Realm realm;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(REGISTRATION_TOKEN, null).commit();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(getClass().getSimpleName(), "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Log.d(getClass().getSimpleName(), "Saving Cloud Messaging Registration : " + token);
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(REGISTRATION_TOKEN, token).commit();
        PreferenceManager.getDefaultSharedPreferences(VomozPayFirebaseInstanceIDService.this).edit().putBoolean(REGISTRATION_TOKEN_SAVED, false).apply();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (null != realm && !realm.isClosed())
            realm.close();
        super.onDestroy();
    }
}

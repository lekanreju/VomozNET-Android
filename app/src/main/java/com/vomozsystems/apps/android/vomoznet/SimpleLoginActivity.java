package com.vomozsystems.apps.android.vomoznet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.splunk.mint.Mint;
import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;
import com.vomozsystems.apps.android.vomoznet.entity.ReferenceData;
import com.vomozsystems.apps.android.vomoznet.fragment.LoginFragment;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.GetPersonalInfoResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetReferenceDataResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.service.SendAuthCodeResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.System.exit;

public class SimpleLoginActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentInteractionListener {

    public static final String AGREEMENT = "agreement";
    public static final String AUTH_TOKEN_LABEL = "auth_token_label";
    public static ReferenceData referenceData = new ReferenceData();
    private final int REQUEST_READ_PHONE_STATE = 1000;
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(MakeDonationInterface.readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(MakeDonationInterface.connectTimeOut, TimeUnit.SECONDS)
            .build();
    String answer = null;
    private CardView mobilePhoneCardView;
    private CardView passwordCardView;
    private CardView createAccountCardView;
    private CardView resetPasswordCardView;
    private CardView resetPasswordQuestionsCardView;
    private Realm realm;
    private static String thePhoneNumber;
    private String flagUrl;
    private int selectedOption = 0;
    private String mPhoneNumber;
    private ProgressBar loginProgressbar;
    public static Phonenumber.PhoneNumber getMyNumber(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String regionCode = tm.getSimCountryIso().toUpperCase(Locale.US);
            return PhoneNumberUtil.getInstance().parse(thePhoneNumber, regionCode);
        } catch (Exception e) {
            return null;
        }
//        return null;
    }

    private void extractDevicePhoneNumberAndLogin() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(SimpleLoginActivity.this,
                    android.Manifest.permission.READ_PHONE_STATE)) {

                boolean should = ActivityCompat.shouldShowRequestPermissionRationale(SimpleLoginActivity.this,
                        android.Manifest.permission.READ_PHONE_STATE);
                if(should){
                    //user denied without Never ask again, just show rationale explanation
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Permission Denied");
                    builder.setMessage("Without this permission " + getResources().getString(R.string.app_name) + " will not be able to validate your phone number. Are you sure you want to deny this permission?");
                    builder.setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                            exit(0);
                        }
                    });
                    builder.setNegativeButton("RE-TRY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            requestPermission();
                        }
                    });
                    builder.show();

                }else{
                    //user has denied with `Never Ask Again`, go to settings
                    promptSettings();
                }
            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                // No explanation needed; request the permission
            }
        } else {
            final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            thePhoneNumber = tm.getLine1Number();
            extractFlagAndPhone(this);
            start();
        }
    }

    public void extractFlagAndPhone(Context context) {
        PhoneNumberUtil util = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber myNum = getMyNumber(context);
        if (myNum != null) {
            String regionCode = util.getRegionCodeForCountryCode(myNum.getCountryCode());
            flagUrl = "https://apps1.vomozsystems.com/vomoz/png/" + regionCode.toLowerCase() + ".png";
            mPhoneNumber = myNum.getCountryCode() + "" + myNum.getNationalNumber();
            Phonenumber.PhoneNumber phoneNumber = null;
            try {
                phoneNumber = util.parse(mPhoneNumber, Locale.getDefault().getCountry());
                mPhoneNumber = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {

            }
        } else {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String regionCode = tm.getSimCountryIso().toUpperCase(Locale.US);
            flagUrl = "https://apps1.vomozsystems.com/vomoz/png/" + regionCode.toLowerCase() + ".png";
            String countryCode = util.getCountryCodeForRegion(regionCode) + "";
            mPhoneNumber = countryCode + "" + thePhoneNumber;
            Phonenumber.PhoneNumber phoneNumber = null;
            try {
                phoneNumber = util.parse(mPhoneNumber, Locale.getDefault().getCountry());
                mPhoneNumber = util.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            } catch (NumberParseException e) {

            }
        }
        Config config = realm.where(Config.class).findFirst();
        realm.beginTransaction();
        config.setMobilePhone(mPhoneNumber);
        realm.copyToRealmOrUpdate(config);
        realm.commitTransaction();
    }

    public Phonenumber.PhoneNumber getMyNumber(Context context, String phoneNumber, final ImageView flagImageView) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phNumber = null;
        try {
            Locale locale = Locale.getDefault();
            String regionCode = locale.getCountry();
            flagUrl = "https://apps1.vomozsystems.com/vomoz/png/" + regionCode.toLowerCase() + ".png";
            phNumber = phoneNumberUtil.parse(phoneNumber, Locale.getDefault().getCountry());
            mPhoneNumber = phoneNumberUtil.format(phNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            Picasso.with(this)
                    .load(flagUrl)
                    .fit()
                    .into(flagImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            flagImageView.setVisibility(VISIBLE);
                        }

                        @Override
                        public void onError() {
                            flagImageView.setVisibility(GONE);
                        }
                    });

            return phNumber;
        } catch (NumberParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode == REQUEST_READ_PHONE_STATE){
            boolean hasSth = grantResults.length > 0;
            if(hasSth){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //user accepted , make call
                    final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    thePhoneNumber = tm.getLine1Number();
                    extractFlagAndPhone(this);
                    start();
                } else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    boolean should = ActivityCompat.shouldShowRequestPermissionRationale(SimpleLoginActivity.this,
                            android.Manifest.permission.READ_PHONE_STATE);
                    if(should){
                        //user denied without Never ask again, just show rationale explanation
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Permission Denied");
                        builder.setMessage("Without this permission " + getResources().getString(R.string.app_name) + " will not be able to validate your phone number. Are you sure you want to deny this permission?");
                        builder.setPositiveButton("I'M SURE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                exit(0);
                            }
                        });
                        builder.setNegativeButton("RE-TRY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                requestPermission();
                            }
                        });
                        builder.show();

                    }else{
                        //user has denied with `Never Ask Again`, go to settings
                        promptSettings();
                    }
                }
            }
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
    }

    private void promptSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("This permission is needed to validate your phone number.");
        builder.setPositiveButton("go to Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                goToSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                exit(0);
            }
        });
        builder.show();
    }

    private void goToSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(myAppSettings);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
        setContentView(R.layout.activity_simple_login);
        Mint.initAndStartSession(this, "22ce5546");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetReferenceDataResponse> call = apiInterface.getReferenceData();
        call.enqueue(new Callback<GetReferenceDataResponse>() {
            @Override
            public void onResponse(Call<GetReferenceDataResponse> call, Response<GetReferenceDataResponse> response) {
                if (response.isSuccessful()) {
                    referenceData = response.body().getResponseData();

                } else {
                    new SweetAlertDialog(SimpleLoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Service connectivity issue detected. Please try again later.")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    exit(0);
                                }
                            })
                            .setTitleText(getString(R.string.app_name))
                            .show();

                }
            }

            @Override
            public void onFailure(Call<GetReferenceDataResponse> call, Throwable t) {
                //Unable to add window -- token android.os.BinderProxy@2b66a43 is not valid; is your activity running?
                new SweetAlertDialog(SimpleLoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Service connectivity issue detected. Please try again later.")
                        .setTitleText(getString(R.string.app_name))
                        .show();
            }
        });

        realm = Realm.getDefaultInstance();

        Config config = realm.where(Config.class).findFirst();
        final Realm realm = Realm.getDefaultInstance();

        if (null == config) {
            realm.beginTransaction();
            config = new Config();
            config.setConfigId(ApplicationUtils.generateRandomChars(10));
            config.setMobilePhone("");
            realm.copyToRealmOrUpdate(config);
            realm.commitTransaction();
            extractDevicePhoneNumberAndLogin();

        } else if (null != config.getMobilePhone() && null != config.getPassword() && null != config.getLoggedIn() && config.getLoggedIn().equals(Boolean.TRUE)) {
            //logged in. go to home page
            //gotoHome();
        } else if (null != config.getMobilePhone() && null != config.getPassword() && null != config.getLoggedIn() && config.getLoggedIn().equals(Boolean.FALSE)) {
            // logged out. present username and password
            extractDevicePhoneNumberAndLogin();
            //showPasswordForm(config.getPassword());
        } else {
            // start all over
            extractDevicePhoneNumberAndLogin();
        }
    }

    private void start() {
        Config config = realm.where(Config.class).findFirst();
        if (config.getFailedAttemptCount() >= 4) {
            new SweetAlertDialog(SimpleLoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.app_name))
                    .setContentText("Your account has been locked! \n\n You must reset your password.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();

                        }
                    })
                    .setConfirmText("OK")
                    .show();
        } else
            showMobilePhoneForm();

    }

    private void showMobilePhoneForm() {
        LoginFragment fragment = LoginFragment.newInstance(mPhoneNumber, flagUrl);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.content_fragment, fragment);
        transaction.commit();

    }

    @Override
    public void onLoginFragmentInteraction() {

    }
}

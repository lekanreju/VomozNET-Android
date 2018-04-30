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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.splunk.mint.Mint;
import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;
import com.vomozsystems.apps.android.vomoznet.entity.ReferenceData;
import com.vomozsystems.apps.android.vomoznet.entity.ResetPasswordQuestion;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.EmailMessage;
import com.vomozsystems.apps.android.vomoznet.service.GetGlobalInfoResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetPersonalInfoResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetReferenceDataResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.service.MemberInfoRequest;
import com.vomozsystems.apps.android.vomoznet.service.SendAuthCodeResponse;
import com.vomozsystems.apps.android.vomoznet.service.UserLoginResponse;
import com.vomozsystems.apps.android.vomoznet.service.VomozGlobalInfo;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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


public class LoginActivity extends AppCompatActivity {

    private static final String AGREEMENT = "agreement";
    public static ReferenceData referenceData = new ReferenceData();
    private Personal personal;
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
   // private CardView resetPasswordQuestionsCardView;
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

            if (ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
                    android.Manifest.permission.READ_PHONE_STATE)) {

                boolean should = ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
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
            getVomozPayGlobalUserInfo(mPhoneNumber);
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
                    getVomozPayGlobalUserInfo(mPhoneNumber);
                } else if(grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    boolean should = ActivityCompat.shouldShowRequestPermissionRationale(LoginActivity.this,
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

    private void getVomozPayGlobalUserInfo(String mPhoneNumber) {
        if (null != mPhoneNumber && mPhoneNumber.length() >= 10) {
            final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetGlobalInfoResponse> call = apiInterface.getGlobalInfo(ApplicationUtils.cleanPhoneNumber(mPhoneNumber), "", ApplicationUtils.APP_ID);
            call.enqueue(new Callback<GetGlobalInfoResponse>() {
                @Override
                public void onResponse(Call<GetGlobalInfoResponse> call, final Response<GetGlobalInfoResponse> response) {
                    if (response.isSuccessful() && null != response.body() && response.body().getResponseData() != null) {
                        // user exists in vmz_global
                        Realm realm = Realm.getDefaultInstance();
                        Config config = realm.where(Config.class).findFirst();
                        realm.beginTransaction();
                        config.setEmail(response.body().getResponseData().getEmailAddress());
                        config.setSecurityQuestion1Answer(response.body().getResponseData().getResetPasswordAnswer1());
                        config.setSecurityQuestion1Id(response.body().getResponseData().getResetPasswordQuestion1());
                        config.setSecurityQuestion2Answer(response.body().getResponseData().getResetPasswordAnswer2());
                        config.setSecurityQuestion2Id(response.body().getResponseData().getResetPasswordQuestion2());
                        realm.copyToRealmOrUpdate(config);
                        realm.commitTransaction();
                    }
                    start();
                }

                @Override
                public void onFailure(Call<GetGlobalInfoResponse> call, Throwable t) {
                    Log.i(getClass().getSimpleName(), "");
                    start();
                }
            });
        } else
            start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkAgreement() {
        boolean agreed = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getBoolean(LoginActivity.AGREEMENT, false);
        final LinearLayout agreementLayout = (LinearLayout) findViewById(R.id.agree_layout);
        final Button mobilePhoneCardViewNextButton = (Button) findViewById(R.id.mobilephone_cardview_next_button);

        if(agreed) {
            mobilePhoneCardViewNextButton.setEnabled(true);
            agreementLayout.setVisibility(GONE);
            mobilePhoneCardViewNextButton.setEnabled(true);
            mobilePhoneCardViewNextButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        }else {
            agreementLayout.setVisibility(VISIBLE);
        }

        RadioButton yes = (RadioButton) findViewById(R.id.agree_layout_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean(LoginActivity.AGREEMENT, true).apply();
                mobilePhoneCardViewNextButton.setEnabled(true);
                mobilePhoneCardViewNextButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                //agreementLayout.setVisibility(GONE);
            }
        });

        RadioButton no = (RadioButton) findViewById(R.id.agree_layout_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putBoolean(LoginActivity.AGREEMENT, false).apply();
                mobilePhoneCardViewNextButton.setEnabled(false);
                mobilePhoneCardViewNextButton.setTextColor(Color.GRAY);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
        setContentView(R.layout.activity_login);
        Mint.initAndStartSession(this, "22ce5546");
        loginProgressbar = (ProgressBar) findViewById(R.id.login_progress);
        loginProgressbar.setIndeterminate(true);
        loginProgressbar.setVisibility(GONE);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetReferenceDataResponse> call = apiInterface.getReferenceData();
        call.enqueue(new Callback<GetReferenceDataResponse>() {
            @Override
            public void onResponse(Call<GetReferenceDataResponse> call, Response<GetReferenceDataResponse> response) {
                if (response.isSuccessful()) {
                    referenceData = response.body().getResponseData();

                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Service connectivity issue detected. Please try again later.")
                        .setTitleText(getString(R.string.app_name))
                        .show();
            }
        });

        realm = Realm.getDefaultInstance();
        mobilePhoneCardView = (CardView) findViewById(R.id.mobile_phone_card_view);
        passwordCardView = (CardView) findViewById(R.id.password_card_view);
        createAccountCardView = (CardView) findViewById(R.id.create_account_card_view);
        resetPasswordCardView = (CardView) findViewById(R.id.reset_password_card_view);
        //resetPasswordQuestionsCardView = (CardView) findViewById(R.id.reset_questions_and_answers_card_view);

        mobilePhoneCardView.setVisibility(VISIBLE);
        passwordCardView.setVisibility(GONE);
        createAccountCardView.setVisibility(GONE);
        resetPasswordCardView.setVisibility(GONE);
        //resetPasswordQuestionsCardView.setVisibility(GONE);
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
        } else if (null != config.getLastPage() && config.getLastPage().length() > 0 && config.getLastPage().equalsIgnoreCase("Security Questions")) {
            extractDevicePhoneNumberAndLogin();
            //showResetPasswordQuestionsForm();
        } else if (null != config.getVerifiedPhoneNumbers() && config.getVerifiedPhoneNumbers().contains(config.getMobilePhone())) {
            validateUser();
        } else if (null != config.getMobilePhone() && null != config.getPassword() && null != config.getLoggedIn() && config.getLoggedIn().equals(Boolean.TRUE)) {
            //logged in. go to home page
            gotoHome();
        } else if (null != config.getMobilePhone() && null != config.getPassword() && null != config.getLoggedIn() && config.getLoggedIn().equals(Boolean.FALSE)) {
            // logged out. present username and password
            extractDevicePhoneNumberAndLogin();
            showPasswordForm(config.getPassword());
        } else {
            // start all over
            extractDevicePhoneNumberAndLogin();
        }
    }

    private void start() {
        Config config = realm.where(Config.class).findFirst();
        if (config.getFailedAttemptCount() >= 4) {
            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getString(R.string.app_name))
                    .setContentText("Your account has been locked! \n\n You must reset your password.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            showForgotPasswordForm();
                        }
                    })
                    .setConfirmText("OK")
                    .show();
        } else
            showMobilePhoneForm();

    }

    private void showMobilePhoneForm() {
        mobilePhoneCardView.setVisibility(VISIBLE);
        createAccountCardView.setVisibility(GONE);
        resetPasswordCardView.setVisibility(GONE);
        passwordCardView.setVisibility(GONE);
        //resetPasswordQuestionsCardView.setVisibility(GONE);
        checkAgreement();
        final ImageView flagImageView = (ImageView) findViewById(R.id.mobilephone_cardview_flag_imageview);
        if (flagUrl == null) flagImageView.setVisibility(GONE);
        Picasso.with(this)
                .load(flagUrl)
                .fit()
                .into(flagImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        flagImageView.setVisibility(GONE);
                    }
                });
        final EditText mobilePhoneEditText = (EditText) findViewById(R.id.mobilephone_cardview_mobile_phone_edit_txt);
        if (null != mPhoneNumber && mPhoneNumber.contains("null")) {
            mPhoneNumber = "";
            mobilePhoneEditText.setText(mPhoneNumber);
            mobilePhoneEditText.setEnabled(true);
        } else {
            mobilePhoneEditText.setText(mPhoneNumber);
            mobilePhoneEditText.setEnabled(mPhoneNumber.length() < 10);
        }
//        mobilePhoneEditText.setText(mPhoneNumber);
//        mobilePhoneEditText.setEnabled(TextUtils.isEmpty(mPhoneNumber));
        Button quitButton = (Button) findViewById(R.id.mobilephone_cardview_quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Do you want to quit?")
                        .setTitleText(getString(R.string.app_name))
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                                exit(0);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        final TextView accessCodeTextView = (TextView) findViewById(R.id.mobilephone_cardview_accesscode_txt);
        final EditText accessCodeEditText = (EditText) findViewById(R.id.mobilephone_cardview_accesscode_edit_txt);
        final TextView accessCodeEmailTextView = (TextView) findViewById(R.id.mobilephone_cardview_accesscode_email_txt);
        final EditText accessCodeEmailEditText = (EditText) findViewById(R.id.mobilephone_cardview_accesscode_email_edit_txt);
        final LinearLayout accessCodeLinearLayout = (LinearLayout) findViewById(R.id.mobilephone_cardview_accesscode_layout);
        final Config config = realm.where(Config.class).findFirst();
        if (config.getAccessCode() != null) {
            accessCodeTextView.setVisibility(VISIBLE);
            accessCodeLinearLayout.setVisibility(VISIBLE);
            accessCodeEmailTextView.setVisibility(VISIBLE);
            accessCodeEmailEditText.setVisibility(VISIBLE);
            getMyNumber(this, config.getMobilePhone(), flagImageView);
            getAccessCode();
        }
        final Button mobilePhoneCardViewNextButton = (Button) findViewById(R.id.mobilephone_cardview_next_button);
        mobilePhoneCardViewNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = validateMobilePhoneForm();
                if (message != null) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText(message)
                            .setTitleText(getString(R.string.app_name))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else if (accessCodeLinearLayout.getVisibility() == VISIBLE) {
                    String accessCode = config.getAccessCode();
                    if (accessCodeEditText.getText().toString().equals(accessCode)) {
                        //go to backend and validate if user exists in vmzpay table
                        mPhoneNumber = mobilePhoneEditText.getText().toString();
                        realm.beginTransaction();
                        config.setAccessCode(null);
                        config.setSendAccessCodeCount(0);
                        if (config.getVerifiedPhoneNumbers() == null)
                            config.setVerifiedPhoneNumbers("");
                        config.setVerifiedPhoneNumbers(config.getVerifiedPhoneNumbers() + "," + config.getMobilePhone());
                        realm.copyToRealmOrUpdate(config);
                        realm.commitTransaction();
                        validateUser();
                    } else {
                        // display error message == accesscode is invalid
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.app_name))
                                .setContentText("Invalid access code")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .setConfirmText("OK")
                                .show();
                    }
                } else if (!mobilePhoneEditText.isEnabled()) {
                    // phone number was extracted the number from telephony
                    // so go to backend and validate if user exists in vmzpay table
                    mPhoneNumber = mobilePhoneEditText.getText().toString();
                    validateUser();
                } else {
                    // phone number not retrievable from telephony.
                    accessCodeEmailTextView.setVisibility(VISIBLE);
                    accessCodeEmailEditText.setVisibility(VISIBLE);
                    accessCodeTextView.setVisibility(VISIBLE);
                    accessCodeLinearLayout.setVisibility(VISIBLE);
                    getMyNumber(LoginActivity.this, config.getMobilePhone(), flagImageView);
                    getAccessCode();
                }
            }
        });
    }

    private void getAccessCode() {
        final Config config = realm.where(Config.class).findFirst();
        Button accessCodeButton = (Button) findViewById(R.id.mobilephone_cardview_accesscode_get_button);
        final EditText mobilePhoneEditText = (EditText) findViewById(R.id.mobilephone_cardview_mobile_phone_edit_txt);
        final EditText emailEditText = (EditText) findViewById(R.id.mobilephone_cardview_accesscode_email_edit_txt);
        accessCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // generate access code and send to user
                if (config.getSendAccessCodeCount() < 3) {
                    if (ApplicationUtils.isValidPhoneNumber(mobilePhoneEditText.getText().toString())) {
                        if (ApplicationUtils.isValidEmailAddress(emailEditText.getText().toString())) {
                            String accessCode = (config.getAccessCode() == null ? ApplicationUtils.generateRandomNumbers(6) : config.getAccessCode());
                            realm.beginTransaction();
                            config.setAccessCode(accessCode);
                            config.setMobilePhone(mobilePhoneEditText.getText().toString());
                            config.setEmail(emailEditText.getText().toString());
                            realm.copyToRealmOrUpdate(config);
                            realm.commitTransaction();
                            MakeDonationInterface makeDonationInterface = getDonationInterface();
                            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                                    .setTitleText(getString(R.string.app_name))
                                    .setContentText("Sending access code to : " + config.getMobilePhone() + "\n\nPlese wait...");
                            sweetAlertDialog.show();
                            Call<SendAuthCodeResponse> call = makeDonationInterface.sendAuthCode(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()), config.getEmail(), accessCode, "SendAuthSMSAndEmailToThisGiver");

                            call.enqueue(new Callback<SendAuthCodeResponse>() {
                                @Override
                                public void onResponse(Call<SendAuthCodeResponse> call, Response<SendAuthCodeResponse> response) {
                                    if (response.isSuccessful()) {

                                        SendAuthCodeResponse sendAuthCodeResponse = response.body();
                                        if (sendAuthCodeResponse.getStatus().equalsIgnoreCase("1")) {
                                            realm.beginTransaction();
                                            config.setSendAccessCodeCount(config.getSendAccessCodeCount() + 1);
                                            realm.copyToRealmOrUpdate(config);
                                            realm.commitTransaction();
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            sweetAlertDialog.setContentText("Access code has been successfully sent to " + config.getMobilePhone());
                                            sweetAlertDialog.setContentText("An access code has been sent to your phone number : " + config.getMobilePhone() + "\n This code is needed to login.")
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismiss();
                                                        }
                                                    })
                                                    .setConfirmText("OK");
                                        } else {

                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            sweetAlertDialog.setContentText("Access was NOT sent to to your phone number : " + config.getMobilePhone() + "\n\n. " + sendAuthCodeResponse.getFaultCode() + ": " + sendAuthCodeResponse.getFaultString()).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismiss();
                                                }
                                            })
                                                    .setConfirmText("OK");
                                        }
                                    } else {
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setContentText("Access was not sent to to your phone number " + config.getMobilePhone() + "\n successfully. Please try again later")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                        sweetAlertDialog.dismiss();
                                                    }
                                                })
                                                .setConfirmText("OK");
                                    }
                                }

                                @Override
                                public void onFailure(Call<SendAuthCodeResponse> call, Throwable t) {
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setContentText("Access was not sent to to your phone number " + config.getMobilePhone() + "\n successfully. Please try again later")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismiss();
                                                }
                                            })
                                            .setConfirmText("OK");
                                }
                            });
                        } else {
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getString(R.string.app_name))
                                    .setContentText("Invalid email. Please enter a valid email")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    })
                                    .setConfirmText("OK")
                                    .show();
                        }

                    } else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.app_name))
                                .setContentText("Invalid phone number. Please enter a valid phone number")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .setConfirmText("OK")
                                .show();
                    }
                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getString(R.string.app_name))
                            .setContentText("The access code has been sent to your phone number more than 2 times. \n\n Please contact our customer support service if you cannot retrieve the code. ")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            })
                            .setConfirmText("OK")
                            .show();
                }
            }
        });
    }

    private MakeDonationInterface getDonationInterface() {
        final String SERVER_URL = MakeDonationInterface.SERVER_URL; //"https://apps1.vomozsystems.com/vomoz/";
        Retrofit retrofit = null;
        Gson gson = new GsonBuilder().create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            MakeDonationInterface makeDonationInterface = retrofit.create(MakeDonationInterface.class);
            return makeDonationInterface;
        }
        return null;
    }

    private void getUserFromVomozNet(String callerId) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetPersonalInfoResponse> call = apiInterface.geVomozNetMemberDonationCenters(callerId, getResources().getString(R.string.org_filter), "", "");
        call.enqueue(new Callback<GetPersonalInfoResponse>() {
            @Override
            public void onResponse(Call<GetPersonalInfoResponse> call, Response<GetPersonalInfoResponse> response) {
                if(response.isSuccessful() && null != response.body()) {
                    List<Personal> personals = response.body().getResponseData();
                    if(null != personals && personals.size() > 0) {
                        showExistingDonationCenterDialog(personals);
                    }else {
                        showManualCreateUserForm();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetPersonalInfoResponse> call, Throwable t) {
                Log.i(getClass().getSimpleName(), "");
            }
        });
    }

    private void showExistingDonationCenterDialog(final List<Personal> personals) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog title
        final String[] items = new String[personals.size()];
        for(int i=0;i<personals.size();i++) {
            items[i] = personals.get(i).getDonationCenterName();
        }
        builder.setTitle("Choose Profiles")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(items, selectedOption, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedOption = which;
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        personal = personals.get(selectedOption);
                        createMyProfile(personal.getFirstName(), personal.getLastName(), personal.getPrimaryEmail(), personal.getAuthPass());

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
       AlertDialog dialog =  builder.create();
       dialog.show();
    }

    private void showCreateUserForm() {
        getUserFromVomozNet(ApplicationUtils.cleanPhoneNumber(mPhoneNumber));
    }

    private void showManualCreateUserForm() {
        mobilePhoneCardView.setVisibility(GONE);
        createAccountCardView.setVisibility(VISIBLE);
        resetPasswordCardView.setVisibility(GONE);
        passwordCardView.setVisibility(GONE);
        //resetPasswordQuestionsCardView.setVisibility(GONE);

        final EditText firstNameEditText = (EditText) findViewById(R.id.createaccount_cardview_first_name_edit_txt);
        final EditText lastNameEditText = (EditText) findViewById(R.id.createaccount_cardview_last_name_edit_txt);
        final EditText passwordEditText = (EditText) findViewById(R.id.createaccount_cardview_password_edit_txt);
        final EditText emailEditText = (EditText) findViewById(R.id.createaccount_cardview_email_edit_txt);
        final EditText mobileEditText = (EditText) findViewById(R.id.createaccount_cardview_mobile_phone_edit_txt);
        final ImageView flagImageView = (ImageView) findViewById(R.id.createaccount_cardview_flag_imageview);
        if (flagUrl == null) flagImageView.setVisibility(GONE);
        Picasso.with(this)
                .load(flagUrl)
                .fit()
                .into(flagImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        flagImageView.setVisibility(GONE);
                    }
                });
        mobileEditText.setText(mPhoneNumber);
        mobileEditText.setEnabled(false);
        Config config = realm.where(Config.class).findFirst();
        if (config != null && config.getEmail() != null) {
            emailEditText.setText(config.getEmail());
            emailEditText.setEnabled(false);
        }
        Button quitButton = (Button) findViewById(R.id.createaccount_cardview_quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Do you want to quit?")
                        .setTitleText(getString(R.string.app_name))
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                                exit(0);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        Button createAccountButton = (Button) findViewById(R.id.createaccount_cardview_signup_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = validateCreateAccountForm();
                if (message != null) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText(message)
                            .setTitleText(getString(R.string.app_name))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    createMyProfile(firstNameEditText.getText().toString(), lastNameEditText.getText().toString(), emailEditText.getText().toString(), passwordEditText.getText().toString());
                }
            }
        });
    }

    private void createMyProfile(String firstName, String lastName, final String email, final String password) {
        final VomozGlobalInfo vomozGlobalInfo = new VomozGlobalInfo();
        vomozGlobalInfo.setCallerId(ApplicationUtils.cleanPhoneNumber(mPhoneNumber));
        vomozGlobalInfo.setFirstName(firstName);
        vomozGlobalInfo.setLastName(lastName);
        vomozGlobalInfo.setEmailAddress(email);
        vomozGlobalInfo.setPassword(password);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<BaseServiceResponse> callCreate = apiInterface.createGlobalInfo(vomozGlobalInfo, "", ApplicationUtils.APP_ID);
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                .setContentText("Creating your profile...Please wait")
                .setTitleText(getString(R.string.app_name));
        sweetAlertDialog.show();
        callCreate.enqueue(new Callback<BaseServiceResponse>() {
            @Override
            public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                if (response.isSuccessful()) {
                    if (!response.body().getMessage().getType().equalsIgnoreCase("SUCCESS")) {
                        sweetAlertDialog.setContentText("Profile cannot be created\n\n" + response.body().getMessage().getDescription());
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                    } else {
                        realm.beginTransaction();
                        final Config config = realm.where(Config.class).findFirst();
                        config.setLoggedIn(false);
                        config.setMobilePhone(mPhoneNumber);
                        config.setAccessCode(null);
                        config.setSendResetEmailCount(0);
                        config.setCurrentDonationCenterCardId(personal.getCenterCardId());
                        config.setSendAccessCodeCount(0);
                        config.setResetPasswordCode(null);
                        config.setFailedAttemptCount(0);
                        if(vomozGlobalInfo.getFirstName() != null) {
                            config.setFirstName(vomozGlobalInfo.getFirstName());
                            config.setLastName(vomozGlobalInfo.getLastName());
                            config.setEmail(email);
                            config.setPassword(password);
                        }
                        config.setLastPage("Security Questions");
                        realm.copyToRealmOrUpdate(config);
                        realm.commitTransaction();
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setContentText("Successfully signed up!\n\n");
                        sweetAlertDialog.setConfirmText("Continue");
                        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                gotoHome();
                            }
                        });
                    }
                } else {
                    String json = null;
                    try {
                        json = response.errorBody().string();
                        BaseServiceResponse baseServiceResponse = ApiClient.getError(json);
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setContentText("Your profile could not be created. " + baseServiceResponse.getMessage().getDescription() + "\n\n" +
                                "");
                        sweetAlertDialog.setConfirmText("Continue");
                        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                Log.i(getClass().getSimpleName(), "");

                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setContentText("You profile could not be created");
                sweetAlertDialog.setConfirmText("OK");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                });
            }
        });
    }
    private void showResetPasswordQuestionsForm1() {
        mobilePhoneCardView.setVisibility(GONE);
        createAccountCardView.setVisibility(GONE);
        resetPasswordCardView.setVisibility(GONE);
        passwordCardView.setVisibility(GONE);
        //resetPasswordQuestionsCardView.setVisibility(VISIBLE);

        final ImageView flagImageView = (ImageView) findViewById(R.id.resetpassword_cardview_flag_imageview);
        if (flagUrl == null) flagImageView.setVisibility(GONE);
        Picasso.with(this)
                .load(flagUrl)
                .fit()
                .into(flagImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        flagImageView.setVisibility(GONE);
                    }
                });

        List<String> list = new ArrayList<String>();
        list.add("Select first security question");
        final Spinner question1Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_1);
        if (null != referenceData.getGroup1Questions())
            for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup1Questions()) {
                list.add(resetPasswordQuestion.getQuestion());
            }
        ArrayAdapter<String> data1Adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_item, list);
        data1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        question1Options.setAdapter(data1Adapter);

        list = new ArrayList<String>();
        list.add("Select second security question");
        final Spinner question2Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_2);
        if (null != referenceData.getGroup2Questions())
            for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup2Questions()) {
                list.add(resetPasswordQuestion.getQuestion());
            }
        ArrayAdapter<String> data2Adapter = new ArrayAdapter<String>(LoginActivity.this, android.R.layout.simple_spinner_item, list);
        data2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        question2Options.setAdapter(data2Adapter);

        final EditText question1Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer1_edit_txt);
        final EditText question2Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer2_edit_txt);

        Button nextButton = (Button) findViewById(R.id.resetpasswordquestion_cardview_next_button);
        Button quitButton = (Button) findViewById(R.id.resetpasswordquestion_cardview_quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Do you want to quit?")
                        .setTitleText(getString(R.string.app_name))
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                exit(0);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = validateResetPasswordQuestionsForm();
                if (message != null) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText(message)
                            .setTitleText(getString(R.string.app_name))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    realm = Realm.getDefaultInstance();
                    final Config config = realm.where(Config.class).findFirst();
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    final VomozGlobalInfo vomozGlobalInfo = new VomozGlobalInfo();
                    vomozGlobalInfo.setCallerId(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
                    vomozGlobalInfo.setResetPasswordQuestion1(referenceData.getGroup1Questions().get(question1Options.getSelectedItemPosition() - 1).getQuestionId());
                    vomozGlobalInfo.setResetPasswordQuestion2(referenceData.getGroup2Questions().get(question2Options.getSelectedItemPosition() - 1).getQuestionId());
                    vomozGlobalInfo.setResetPasswordAnswer1(question1Answer.getText().toString().replace(" ", "").toLowerCase());
                    vomozGlobalInfo.setResetPasswordAnswer2(question2Answer.getText().toString().replace(" ", "").toLowerCase());
                    Call<BaseServiceResponse> call = apiInterface.updateForgotPasswordQuestions(vomozGlobalInfo, "", ApplicationUtils.APP_ID);
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    sweetAlertDialog.setContentText("Updating your challenge questions...Please wait")
                            .setTitleText(getString(R.string.app_name))
                            .show();
                    call.enqueue(new Callback<BaseServiceResponse>() {
                        @Override
                        public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                            if (response.isSuccessful()) {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setContentText("Your challenge questions have been updated successfully.")
                                        .setTitleText(getString(R.string.app_name))
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                realm.beginTransaction();
                                                config.setSecurityQuestion1Answer(vomozGlobalInfo.getResetPasswordAnswer1());
                                                config.setSecurityQuestion2Answer(vomozGlobalInfo.getResetPasswordAnswer2());
                                                config.setSecurityQuestion1Id(vomozGlobalInfo.getResetPasswordQuestion1());
                                                config.setSecurityQuestion2Id(vomozGlobalInfo.getResetPasswordQuestion2());
                                                config.setSecurityQuestion1Text(referenceData.getGroup1Questions().get(question1Options.getSelectedItemPosition() - 1).getQuestion());
                                                config.setSecurityQuestion2Text(referenceData.getGroup2Questions().get(question2Options.getSelectedItemPosition() - 1).getQuestion());
                                                config.setLoggedIn(true);
                                                config.setLastPage(null);
                                                realm.copyToRealmOrUpdate(config);
                                                realm.commitTransaction();
                                                gotoHome();
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        })
                                        .setConfirmText("OK")
                                        .show();
                            } else {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setContentText("Your challenge questions was not updated. Please try again")
                                        .setTitleText(getString(R.string.app_name))
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        })
                                        .setConfirmText("OK")
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setContentText("Your challenge questions was not updated. Please try again")
                                    .setTitleText(getString(R.string.app_name))
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .setConfirmText("OK")
                                    .show();
                        }
                    });
                }
            }
        });
    }

    private void showForgotPasswordForm() {
        mobilePhoneCardView.setVisibility(GONE);
        createAccountCardView.setVisibility(GONE);
        resetPasswordCardView.setVisibility(VISIBLE);
        passwordCardView.setVisibility(GONE);
        final Realm realm = Realm.getDefaultInstance();
        final Config config = realm.where(Config.class).findFirst();

        Button buttonGetPasscode = findViewById(R.id.resetpasswordquestion_get_passcode_button);
        buttonGetPasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                EmailMessage emailMessage = new EmailMessage();
                final int min = 100000;
                final int max = 999999;
                final int random = new Random().nextInt((max - min) + 1) + min;
                String accessCode = random + "";
                realm.beginTransaction();
                config.setResetPasswordCode(accessCode);
                realm.copyToRealmOrUpdate(config);
                realm.commitTransaction();
                emailMessage.setReceiver(config.getEmail());
                emailMessage.setSubject(getResources().getString(R.string.app_name) + " - Reset Your Password");
                emailMessage.setBody("Hello " + config.getFirstName() + "\n\nHere is the passcode needed to reset your password.\n\n<b>" + accessCode + "</b>\n\nBest Regards.\n"+getResources().getString(R.string.app_name)+ " app");
                Call<BaseServiceResponse> call = apiInterface.sendEmail(config.getEmail(),emailMessage, "", "");
                call.enqueue(new Callback<BaseServiceResponse>() {
                    @Override
                    public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                        if(response.isSuccessful()) {
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setContentText("The passcode has been sent to your email.")
                                    .setTitleText(getString(R.string.app_name));
                            sweetAlertDialog.show();
                        }else {
                            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setContentText("The passcode could NOT be sent to your email.")
                                    .setTitleText(getString(R.string.app_name));
                            sweetAlertDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setContentText("The passcode could NOT be sent to your email.")
                                .setTitleText(getString(R.string.app_name));
                        sweetAlertDialog.show();
                    }
                });
            }
        });
        final EditText passCodeEditText = (EditText) findViewById(R.id.resetpasswordquestion_cardview_passcode_edit_txt);

        final EditText newPasswordEditText = (EditText) findViewById(R.id.resetpasswordquestion_cardview_password_edit_txt);
        Button resetPasswordButton = findViewById(R.id.resetpasswordquestion_cardview_next_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != config.getAccessCode() && config.getAccessCode().equalsIgnoreCase(passCodeEditText.getText().toString())) {
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Invalid Passcode. Please check your email and try again")
                            .setTitleText(getString(R.string.app_name));
                    sweetAlertDialog.show();
                } else {
                    VomozGlobalInfo vomozGlobalInfo = new VomozGlobalInfo();
                    vomozGlobalInfo.setCallerId(ApplicationUtils.cleanPhoneNumber(mPhoneNumber));
                    vomozGlobalInfo.setPassword(newPasswordEditText.getText().toString());
                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<BaseServiceResponse> call = apiInterface.resetPasswordGlobalInfo(vomozGlobalInfo, "", ApplicationUtils.APP_ID);
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                            .setContentText("Reseting your password...Please Wait")
                            .setTitleText(getString(R.string.app_name));
                    sweetAlertDialog.show();
                    call.enqueue(new Callback<BaseServiceResponse>() {
                        @Override
                        public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                            if (response.isSuccessful()) {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setContentText("Password reset successfully");
                                sweetAlertDialog.setConfirmText("Login Now");
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        Config config = realm.where(Config.class).findFirst();
                                        realm.beginTransaction();
                                        config.setFailedAttemptCount(0);
                                        config.setResetPasswordCode(null);
                                        config.setAccessCode(null);
                                        config.setSendResetEmailCount(0);
                                        config.setSendAccessCodeCount(0);
                                        config.setLoggedIn(true);
                                        realm.copyToRealmOrUpdate(config);
                                        realm.commitTransaction();
                                        showPasswordForm(newPasswordEditText.getText().toString());
                                    }
                                });
                            } else {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setContentText("Password was not reset successfully");
                                sweetAlertDialog.setConfirmText("OK");
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setContentText("Password was not reset successfully");
                            sweetAlertDialog.setConfirmText("OK");
                            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            });
                        }
                    });
                }
            }
        });
        Button quitButton = (Button) findViewById(R.id.resetpassword_cardview_quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Do you want to quit?")
                        .setTitleText(getString(R.string.app_name))
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                exit(0);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });


    }

    private void showForgotPasswordForm1() {
        mobilePhoneCardView.setVisibility(GONE);
        createAccountCardView.setVisibility(GONE);
        resetPasswordCardView.setVisibility(VISIBLE);
        passwordCardView.setVisibility(GONE);
        //resetPasswordQuestionsCardView.setVisibility(GONE);

        final ImageView flagImageView = (ImageView) findViewById(R.id.resetpassword_cardview_flag_imageview);
        if (flagUrl == null) flagImageView.setVisibility(GONE);
        Picasso.with(this)
                .load(flagUrl)
                .fit()
                .into(flagImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        flagImageView.setVisibility(GONE);
                    }
                });
        final Config config = realm.where(Config.class).findFirst();
        final EditText mobilePhoneEditText = (EditText) findViewById(R.id.resetpassword_cardview_mobile_phone_edit_txt);
        final EditText passwordEditText = (EditText) findViewById(R.id.resetpassword_cardview_password_edit_txt);
        final TextView securityQuestionTextView = (TextView) findViewById(R.id.resetpassword_cardview_security_question_txt);
        Random rand = new Random();
//        int selectedId = 0;
//        String question = null;
//        int x = rand.nextInt(2);
//        if (x == 0) {
//            selectedId = config.getSecurityQuestion1Id();
//            answer = config.getSecurityQuestion1Answer();
//            for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup1Questions()) {
//                if (resetPasswordQuestion.getQuestionId().equals(selectedId)) {
//                    question = resetPasswordQuestion.getQuestion();
//                    break;
//                }
//            }
//        } else {
//            selectedId = config.getSecurityQuestion2Id();
//            answer = config.getSecurityQuestion2Answer();
//            for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup2Questions()) {
//                if (resetPasswordQuestion.getQuestionId().equals(selectedId)) {
//                    question = resetPasswordQuestion.getQuestion();
//                    break;
//                }
//            }
//        }
//        securityQuestionTextView.setText(question);
        final Button resetButton = (Button) findViewById(R.id.resetpassword_cardview_resetpassword_button);
        Button quitButton = (Button) findViewById(R.id.resetpassword_cardview_quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Do you want to quit?")
                        .setTitleText(getString(R.string.app_name))
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                exit(0);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        mobilePhoneEditText.setText(config.getMobilePhone());
        mobilePhoneEditText.setEnabled(false);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = validateResetPasswordForm();
                if (message != null) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText(message)
                            .setTitleText(getString(R.string.app_name))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {

                    final EditText securityQuestionAnswerEditText = (EditText) findViewById(R.id.resetpassword_cardview_security_answer_edit_txt);
                    String correctAnswer = answer;
                    if (null != correctAnswer && securityQuestionAnswerEditText.getText().toString().replace(" ", "").toLowerCase().equalsIgnoreCase(correctAnswer.replace(" ", "").toLowerCase())) {
                        // update global table and go to home page
                        VomozGlobalInfo vomozGlobalInfo = new VomozGlobalInfo();
                        vomozGlobalInfo.setCallerId(ApplicationUtils.cleanPhoneNumber(mPhoneNumber));
                        vomozGlobalInfo.setPassword(passwordEditText.getText().toString());
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<BaseServiceResponse> call = apiInterface.resetPasswordGlobalInfo(vomozGlobalInfo, "", ApplicationUtils.APP_ID);
                        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                                .setContentText("Reseting your password...Please Wait")
                                .setTitleText(getString(R.string.app_name));
                        sweetAlertDialog.show();
                        call.enqueue(new Callback<BaseServiceResponse>() {
                            @Override
                            public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                                if (response.isSuccessful()) {
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    sweetAlertDialog.setContentText("Password reset successfully");
                                    sweetAlertDialog.setConfirmText("Login Now");
                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            realm.beginTransaction();
                                            config.setFailedAttemptCount(0);
                                            config.setResetPasswordCode(null);
                                            config.setAccessCode(null);
                                            config.setSendResetEmailCount(0);
                                            config.setSendAccessCodeCount(0);
                                            config.setLoggedIn(true);
                                            realm.copyToRealmOrUpdate(config);
                                            realm.commitTransaction();
                                            showPasswordForm(passwordEditText.getText().toString());
                                        }
                                    });
                                } else {
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setContentText("Password was not reset successfully");
                                    sweetAlertDialog.setConfirmText("OK");
                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setContentText("Password was not reset successfully");
                                sweetAlertDialog.setConfirmText("OK");
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                            }
                        });
                    } else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.app_name))
                                .setContentText("Incorrect security answer.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                })
                                .setConfirmText("OK")
                                .show();
                    }
                }
            }
        });
    }

    private void showPasswordForm(final String password) {
        mobilePhoneCardView.setVisibility(GONE);
        createAccountCardView.setVisibility(GONE);
        resetPasswordCardView.setVisibility(GONE);
        passwordCardView.setVisibility(VISIBLE);
        //resetPasswordQuestionsCardView.setVisibility(GONE);
        realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        if (TextUtils.isEmpty(mPhoneNumber)) mPhoneNumber = config.getMobilePhone();
        final ImageView flagImageView = (ImageView) findViewById(R.id.password_cardview_flag_imageview);
        if (flagUrl == null) flagImageView.setVisibility(GONE);
        Picasso.with(this)
                .load(flagUrl)
                .fit()
                .into(flagImageView, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        flagImageView.setVisibility(GONE);
                    }
                });
        final EditText phoneNumberEditText = (EditText) findViewById(R.id.password_cardview_mobile_phone_edit_txt);
        phoneNumberEditText.setEnabled(TextUtils.isEmpty(mPhoneNumber));
        phoneNumberEditText.setText(mPhoneNumber);
        Button quitButton = (Button) findViewById(R.id.password_cardview_quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Do you want to quit?")
                        .setTitleText(getString(R.string.app_name))
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                finish();
                                exit(0);
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
        Button forgotPasswordButton = (Button) findViewById(R.id.password_cardview_forgot_password_button);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordForm();
            }
        });
        final Button button = (Button) findViewById(R.id.password_cardview_next_button);
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //button.setEnabled(false);
                String message = validatePasswordForm();
                if (message != null) {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setContentText(message)
                            .setTitleText(getString(R.string.app_name))
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    final Config config = realm.where(Config.class).findFirst();
                    if (config.getFailedAttemptCount() > 3) {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getString(R.string.app_name))
                                .setContentText("Your account has been locked! \n\n Your account must be reset.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                        showForgotPasswordForm();
                                    }
                                })
                                .setConfirmText("OK")
                                .show();
                    } else {
                        attemptLogin();
                    }
                }
            }
        });
    }

    private String validateResetPasswordQuestionsForm() {
        String message = null;
        Spinner question1Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_1);
        Spinner question2Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_2);

        EditText question1Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer1_edit_txt);
        EditText question2Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer2_edit_txt);
        if (question1Options.getSelectedItemPosition() == 0) {
            message = "Please select the first question";
        }
        if (question2Options.getSelectedItemPosition() == 0) {
            message = "Please select the second question";
        }
        if (question1Answer.getText().toString().length() == 0)
            message = "You must provide an answer to the first question";
        if (question2Answer.getText().toString().length() == 0)
            message = "You must provide an answer to the second question";
        return message;
    }

    private String validateMobilePhoneForm() {
        final EditText mobilePhoneEditText = (EditText) findViewById(R.id.mobilephone_cardview_mobile_phone_edit_txt);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(mobilePhoneEditText.getText().toString(), Locale.getDefault().getCountry());
            phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
            return null;
        } catch (NumberParseException e) {
            return "Invalid phone number";
        }
    }

    private String validatePasswordForm() {
        final EditText phoneNumberEditText = (EditText) findViewById(R.id.password_cardview_mobile_phone_edit_txt);
        final EditText passwordEditText = (EditText) findViewById(R.id.password_cardview_password_edit_txt);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(phoneNumberEditText.getText().toString(), Locale.getDefault().getCountry());
            phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            return "Invalid phone number";
        }
        String message = ApplicationUtils.validatePassword(passwordEditText.getText().toString());
        return message;
    }

    private String validateCreateAccountForm() {
        final EditText firstNameEditText = (EditText) findViewById(R.id.createaccount_cardview_first_name_edit_txt);
        final EditText lastNameEditText = (EditText) findViewById(R.id.createaccount_cardview_last_name_edit_txt);
        final EditText passwordEditText = (EditText) findViewById(R.id.createaccount_cardview_password_edit_txt);
        final EditText confirmPasswordEditText = (EditText) findViewById(R.id.createaccount_cardview_confirm_password_edit_txt);
        final EditText emailEditText = (EditText) findViewById(R.id.createaccount_cardview_email_edit_txt);
        final EditText mobileEditText = (EditText) findViewById(R.id.createaccount_cardview_mobile_phone_edit_txt);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(mobileEditText.getText().toString(), Locale.getDefault().getCountry());
            phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            return "Invalid phone number";
        }
        if (ApplicationUtils.cleanPhoneNumber(mobileEditText.getText().toString()).length() < 11) {
            return "Invalid phone number. Your phone number must include international/ country code";
        }
        if (!ApplicationUtils.isValidEmailAddress(emailEditText.getText().toString()))
            return "Invalid email address";
        if (!ApplicationUtils.isValidName(firstNameEditText.getText().toString()))
            return "Invalid first name";
        if (!ApplicationUtils.isValidName(lastNameEditText.getText().toString()))
            return "Invalid last name";
        if (!passwordEditText.getText().toString().equalsIgnoreCase(confirmPasswordEditText.getText().toString()))
            return "Please confirm your password";
        String message = ApplicationUtils.validatePassword(passwordEditText.getText().toString());
        return message;
    }

    private String validateResetPasswordForm() {
        final EditText mobilePhoneEditText = (EditText) findViewById(R.id.resetpassword_cardview_mobile_phone_edit_txt);
        final EditText passwordEditText = (EditText) findViewById(R.id.resetpassword_cardview_password_edit_txt);
        EditText confirmPasswordEditText = (EditText) findViewById(R.id.resetpassword_cardview_confirm_password_edit_text);
        EditText answerEditText = (EditText) findViewById(R.id.resetpassword_cardview_security_answer_edit_txt);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(mobilePhoneEditText.getText().toString(), Locale.getDefault().getCountry());
            phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            return "Invalid phone number";
        }
        if (answerEditText.getText().toString().length() == 0)
            return "Invalid answer. Please supply a valid answer";
        if (!confirmPasswordEditText.getText().toString().equalsIgnoreCase(passwordEditText.getText().toString()))
            return "Please confirm your password";
        String message = ApplicationUtils.validatePassword(passwordEditText.getText().toString());
        if (message == null) {
            if (!confirmPasswordEditText.getText().toString().equals(passwordEditText.getText().toString()))
                return "Your password and confirmation are not the same.";
        } else
            return message;
        return null;
    }

    private void validateUser() {
        final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        if (mPhoneNumber == null) {
            Config config = realm.where(Config.class).findFirst();
            mPhoneNumber = config.getMobilePhone();
        }
        Call<GetGlobalInfoResponse> call = apiInterface.getGlobalInfo(ApplicationUtils.cleanPhoneNumber(mPhoneNumber), "", ApplicationUtils.APP_ID);
        call.enqueue(new Callback<GetGlobalInfoResponse>() {
            @Override
            public void onResponse(Call<GetGlobalInfoResponse> call, final Response<GetGlobalInfoResponse> response) {
                if (response.isSuccessful() && null != response.body() && response.body().getResponseData() != null) {
                    // user exists in vmz_global
                    showPasswordForm(response.body().getResponseData().getPassword());

                } else if (response.isSuccessful() && null != response.body() && response.body().getResponseData() == null) {
                    // user does not exist in vmz_global. go to create
                    showCreateUserForm();

                } else {
                    Log.i(getClass().getSimpleName(), "");
                }
            }

            @Override
            public void onFailure(Call<GetGlobalInfoResponse> call, Throwable t) {
                Log.i(getClass().getSimpleName(), "");
            }
        });
    }

    private void attemptLogin() {
        final Config config = realm.where(Config.class).findFirst();
        final EditText passwordEditText = (EditText) findViewById(R.id.password_cardview_password_edit_txt);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        VomozGlobalInfo vomozGlobalInfo = new VomozGlobalInfo();
        vomozGlobalInfo.setCallerId(ApplicationUtils.cleanPhoneNumber(mPhoneNumber));
        vomozGlobalInfo.setPassword(passwordEditText.getText().toString());
        Call<GetGlobalInfoResponse> call = apiInterface.vmzGlobalLogin(vomozGlobalInfo, "", ApplicationUtils.APP_ID);
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(getString(R.string.app_name))
                .setContentText("Logging in...Please wait.");
        sweetAlertDialog.show();
        call.enqueue(new Callback<GetGlobalInfoResponse>() {
            @Override
            public void onResponse(Call<GetGlobalInfoResponse> call, Response<GetGlobalInfoResponse> response) {
                if (response.isSuccessful() && response.body().getMessage().getType().equalsIgnoreCase("SUCCESS")) {
                    realm.beginTransaction();
                    config.setLoggedIn(true);
                    config.setAccessCode(null);
                    config.setSendResetEmailCount(0);
                    config.setSendAccessCodeCount(0);
                    config.setResetPasswordCode(null);
                    config.setFailedAttemptCount(0);
                    config.setLastPage(null);
                    if(null != response.body().getResponseData()) {
                        config.setFirstName(response.body().getResponseData().getFirstName());
                        config.setLastName(response.body().getResponseData().getLastName());
                        config.setEmail(response.body().getResponseData().getEmailAddress());
                    }
                    config.setMobilePhone(mPhoneNumber);
                    config.setPassword(passwordEditText.getText().toString());
                    realm.copyToRealmOrUpdate(config);
                    realm.commitTransaction();
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            gotoHome();
                        }
                    });
                    sweetAlertDialog.setConfirmText("OK");
                    sweetAlertDialog.setContentText("Logged on successfully.");
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                } else if (response.isSuccessful() && response.body().getMessage().getType().equalsIgnoreCase("FAILURE")) {
                    realm.beginTransaction();
                    config.setFailedAttemptCount(config.getFailedAttemptCount() + 1);
                    config.setSecurityQuestion1Answer(response.body().getResponseData().getResetPasswordAnswer1());
                    config.setSecurityQuestion1Id(response.body().getResponseData().getResetPasswordQuestion1());
                    config.setSecurityQuestion2Answer(response.body().getResponseData().getResetPasswordAnswer2());
                    config.setSecurityQuestion2Id(response.body().getResponseData().getResetPasswordQuestion2());
                    realm.copyToRealmOrUpdate(config);
                    realm.commitTransaction();
                    String message = response.body().getMessage().getCode() + ": Invalid phone number or password combination.";
                    if (config.getFailedAttemptCount() > 2) {
                        message = message + "\n\nSeems you are having trouble signing in...Further failure attempts may lock your account.\n You have " + (5 - config.getFailedAttemptCount()) + " more attempt(s) before your account is locked";
                    }
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            gotoHome();
                        }
                    });
                    sweetAlertDialog.setConfirmText("OK");
                    sweetAlertDialog.setContentText(message);
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetGlobalInfoResponse> call, Throwable t) {
                Log.i(getClass().getSimpleName(), "");
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("")
                        .setTitleText(getString(R.string.app_name))
                        .setContentText("Your password could not be reset at this time. Please try again later.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setConfirmText("OK")
                        .show();
            }
        });
    }

    private void gotoHome() {
        final Config config = realm.where(Config.class).findFirst();
        realm.beginTransaction();
        config.setLoggedIn(true);
        config.setLastPage(null);
        realm.copyToRealmOrUpdate(config);
        realm.commitTransaction();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void gotoHome2() {
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        if (donationCenter != null) {
            finish();
        } else {
            final Config config = realm.where(Config.class).findFirst();
            if (config != null) {
                final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("Starting up..");
                sweetAlertDialog.show();
                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<DonationCenterResponse> call = apiService.getMemberDonationCenters(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()), getResources().getString(R.string.org_filter),"", ApplicationUtils.APP_ID);
                call.enqueue(new Callback<DonationCenterResponse>() {
                    @Override
                    public void onResponse(Call<DonationCenterResponse> call, Response<DonationCenterResponse> response) {
                        if (response.isSuccessful()) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(response.body().getResponseData());
                            realm.commitTransaction();
                            DonationCenter center = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
                            if (center != null) {
                                MemberInfoRequest memberInfoRequest = new MemberInfoRequest();
                                memberInfoRequest.setPhoneNumber(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
                                memberInfoRequest.setPassword(config.getPassword());
                                memberInfoRequest.setCenterCardId(center.getCardId());
                                Call<UserLoginResponse> call2 = apiService.login(memberInfoRequest, getResources().getString(R.string.org_filter), "", ApplicationUtils.APP_ID);
                                call2.enqueue(new Callback<UserLoginResponse>() {
                                    @Override
                                    public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                                        if (response.isSuccessful()) {
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            sweetAlertDialog.setContentText("Success");
                                            realm.beginTransaction();
                                            realm.copyToRealmOrUpdate(response.body().getResponseData());
                                            config.setLoggedIn(true);
                                            config.setLastPage(null);
                                            realm.copyToRealmOrUpdate(config);
                                            realm.commitTransaction();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // cannot load user profile
                                            sweetAlertDialog.setContentText("7350 - Failure: Login was not successful");
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                                        // network failure - cannot load user profile
                                        sweetAlertDialog.setContentText("7351 - Failure: Login was not successful");
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    }
                                });
                            }
                            else {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setContentText("7354 - Failure: No default donation center");
                                finish();
                            }
                        } else {
                            sweetAlertDialog.setContentText("7355 - Failure: Cannot retrieve your associated organization(s)");
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    }

                    @Override
                    public void onFailure(Call<DonationCenterResponse> call, Throwable t) {
                        sweetAlertDialog.setContentText("7355 - Network Failure: Cannot retrieve your associated organization(s)");
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                });
            }
        }
    }

}

//                            else if (null != response.body().getResponseData() && response.body().getResponseData().size() > 0) {
//                                // prompt to select a donation center
//                                final List<DonationCenter> donationCenters = response.body().getResponseData();
//                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                builder.setTitle("Select Default " + getResources().getString(R.string.org_type));
//                                // add a radio button list
//                                final String[] animals = new String[donationCenters.size()];
//                                int i = 0;
//                                for (DonationCenter donationCenter : donationCenters) {
//                                    animals[i] = donationCenter.getName();
//                                    i++;
//                                }
//                                int checkedItem = 0; // cow
//                                builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // user checked an item
//                                        selectedOption = which;
//                                    }
//                                });
//
//                                // add OK and Cancel buttons
//                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        ChangeDefaultDonationCenterRequest changeDefaultDonationCenterRequest = new ChangeDefaultDonationCenterRequest();
//                                        changeDefaultDonationCenterRequest.setCallerId(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
//                                        changeDefaultDonationCenterRequest.setNewDonationCenterId(donationCenters.get(selectedOption).getCardId());
//
//
//
//                                    }
//                                });
//                                builder.setNegativeButton("Cancel", null);
//
//                                // create and show the alert dialog
//                                AlertDialog dialog = builder.create();
//                                dialog.show();
//                            }
package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.LoginActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.SimpleLoginActivity;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.service.SendAuthCodeResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

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

public class LoginFragment extends Fragment {
    private String mPhoneNumber;
    private String flagUrl;
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(MakeDonationInterface.readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(MakeDonationInterface.connectTimeOut, TimeUnit.SECONDS)
            .build();
    private View view;

    private OnLoginFragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String mPhoneNumber, String flagUrl) {
        LoginFragment fragment = new LoginFragment();
        fragment.mPhoneNumber = mPhoneNumber;
        fragment.flagUrl = flagUrl;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        start();
        return view;
    }

    private void start() {
        final Realm realm = Realm.getDefaultInstance();
        checkAgreement();
        final ImageView flagImageView = (ImageView) view.findViewById(R.id.mobilephone_cardview_flag_imageview);
        if (flagUrl == null) flagImageView.setVisibility(GONE);
        Picasso.with(getActivity())
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
        final EditText mobilePhoneEditText = (EditText) view.findViewById(R.id.mobilephone_cardview_mobile_phone_edit_txt);
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
        Button quitButton = (Button) view.findViewById(R.id.mobilephone_cardview_quit_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                        .setContentText("Do you want to quit?")
                        .setTitleText(getString(R.string.app_name))
                        .setConfirmText("Yes")
                        .setCancelText("No")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                getActivity().finish();
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
        final TextView accessCodeTextView = (TextView) view.findViewById(R.id.mobilephone_cardview_accesscode_txt);
        final EditText accessCodeEditText = (EditText) view.findViewById(R.id.mobilephone_cardview_accesscode_edit_txt);
        final TextView accessCodeEmailTextView = (TextView) view.findViewById(R.id.mobilephone_cardview_accesscode_email_txt);
        final EditText accessCodeEmailEditText = (EditText) view.findViewById(R.id.mobilephone_cardview_accesscode_email_edit_txt);
        final LinearLayout accessCodeLinearLayout = (LinearLayout) view.findViewById(R.id.mobilephone_cardview_accesscode_layout);
        final Config config = realm.where(Config.class).findFirst();
        if (config.getAccessCode() != null) {
            accessCodeTextView.setVisibility(VISIBLE);
            accessCodeLinearLayout.setVisibility(VISIBLE);
            accessCodeEmailTextView.setVisibility(VISIBLE);
            accessCodeEmailEditText.setVisibility(VISIBLE);
            getAccessCode();
        }
        final Button mobilePhoneCardViewNextButton = (Button) view.findViewById(R.id.mobilephone_cardview_next_button);
        mobilePhoneCardViewNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = validateMobilePhoneForm();
                if (message != null) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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
                        //validateUser();
                    } else {
                        // display error message == accesscode is invalid
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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
                    //validateUser();
                } else {
                    // phone number not retrievable from telephony.
                    accessCodeEmailTextView.setVisibility(VISIBLE);
                    accessCodeEmailEditText.setVisibility(VISIBLE);
                    accessCodeTextView.setVisibility(VISIBLE);
                    accessCodeLinearLayout.setVisibility(VISIBLE);
                    getAccessCode();
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            mListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String validateMobilePhoneForm() {
        final EditText mobilePhoneEditText = (EditText) view.findViewById(R.id.mobilephone_cardview_mobile_phone_edit_txt);
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
        final EditText phoneNumberEditText = (EditText) view.findViewById(R.id.password_cardview_mobile_phone_edit_txt);
        final EditText passwordEditText = (EditText) view.findViewById(R.id.password_cardview_password_edit_txt);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneUtil.parse(phoneNumberEditText.getText().toString(), Locale.getDefault().getCountry());
            phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            return "Invalid phone number";
        }
        Realm realm = Realm.getDefaultInstance();
        final Config config = realm.where(Config.class).findFirst();
        String message = ApplicationUtils.validatePassword(passwordEditText.getText().toString(), config);
        return message;
    }

    private void getAccessCode() {
        final Realm realm = Realm.getDefaultInstance();
        final Config config = realm.where(Config.class).findFirst();
        Button accessCodeButton = (Button) view.findViewById(R.id.mobilephone_cardview_accesscode_get_button);
        final EditText mobilePhoneEditText = (EditText) view.findViewById(R.id.mobilephone_cardview_mobile_phone_edit_txt);
        final EditText emailEditText = (EditText) view.findViewById(R.id.mobilephone_cardview_accesscode_email_edit_txt);
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
                            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
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
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
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

    private void checkAgreement() {
        boolean agreed = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(SimpleLoginActivity.AGREEMENT, false);
        final LinearLayout agreementLayout = (LinearLayout) view.findViewById(R.id.agree_layout);
        final Button mobilePhoneCardViewNextButton = (Button) view.findViewById(R.id.mobilephone_cardview_next_button);

        if(agreed) {
            agreementLayout.setVisibility(View.GONE);
            mobilePhoneCardViewNextButton.setEnabled(true);
            mobilePhoneCardViewNextButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }

        RadioButton yes = (RadioButton) view.findViewById(R.id.agree_layout_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(SimpleLoginActivity.AGREEMENT, true).apply();
                mobilePhoneCardViewNextButton.setEnabled(true);
                mobilePhoneCardViewNextButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

        RadioButton no = (RadioButton) view.findViewById(R.id.agree_layout_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().putBoolean(SimpleLoginActivity.AGREEMENT, false).apply();
                mobilePhoneCardViewNextButton.setEnabled(false);
                mobilePhoneCardViewNextButton.setBackgroundColor(getResources().getColor(R.color.light_gray));

            }
        });
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void onLoginFragmentInteraction();
    }
}

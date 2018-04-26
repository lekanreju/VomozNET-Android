package com.vomozsystems.apps.android.vomoznet;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.splunk.mint.Mint;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.ReferenceData;
import com.vomozsystems.apps.android.vomoznet.entity.ResetPasswordQuestion;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetReferenceDataResponse;
import com.vomozsystems.apps.android.vomoznet.service.VomozGlobalInfo;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.exit;

public class MyProfileActivity extends AppCompatActivity {
    public static ReferenceData referenceData;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText question1Answer;
    private EditText question2Answer;
    private Spinner question1Options;
    private Spinner question2Options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Mint.initAndStartSession(this, "36cc1bd3");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Realm realm = Realm.getDefaultInstance();
        final Config config = realm.where(Config.class).findFirst();

        firstNameEditText = (EditText) findViewById(R.id.edit_first_name);
        lastNameEditText = (EditText) findViewById(R.id.edit_last_name);
        emailEditText = (EditText) findViewById(R.id.edit_email);
        passwordEditText = (EditText) findViewById(R.id.edit_password);
        question1Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer1_edit_txt);
        question2Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer2_edit_txt);

        firstNameEditText.setText(config.getFirstName());
        lastNameEditText.setText(config.getLastName());
        emailEditText.setText(config.getEmail());
        passwordEditText.setText(config.getPassword());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<GetReferenceDataResponse> call = apiInterface.getReferenceData();
        call.enqueue(new Callback<GetReferenceDataResponse>() {
            @Override
            public void onResponse(Call<GetReferenceDataResponse> call, Response<GetReferenceDataResponse> response) {
                if (response.isSuccessful()) {
                    referenceData = response.body().getResponseData();

                    List<String> list = new ArrayList<String>();
                    list.add("Select first security question");
                    question1Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_1);
                    if (null != referenceData.getGroup1Questions())
                        for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup1Questions()) {
                            list.add(resetPasswordQuestion.getQuestion());
                        }
                    ArrayAdapter<String> data1Adapter = new ArrayAdapter<String>(MyProfileActivity.this, android.R.layout.simple_spinner_item, list);
                    data1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    question1Options.setAdapter(data1Adapter);

                    list = new ArrayList<String>();
                    list.add("Select second security question");
                    question2Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_2);
                    if (null != referenceData.getGroup2Questions())
                        for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup2Questions()) {
                            list.add(resetPasswordQuestion.getQuestion());
                        }
                    ArrayAdapter<String> data2Adapter = new ArrayAdapter<String>(MyProfileActivity.this, android.R.layout.simple_spinner_item, list);
                    data2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    question2Options.setAdapter(data2Adapter);


                } else {
                    new SweetAlertDialog(MyProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(MyProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setContentText("Service connectivity issue detected. Please try again later.")
                        .setTitleText(getString(R.string.app_name))
                        .show();
            }
        });

    }

    private void updateMyProfile() {
        String message = validateForm();
        if(message != null) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MyProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setContentText(message)
                    .setTitleText(getString(R.string.app_name));
            sweetAlertDialog.show();
        } else {
            Realm realm = Realm.getDefaultInstance();
            final Config config = realm.where(Config.class).findFirst();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            final VomozGlobalInfo vomozGlobalInfo = new VomozGlobalInfo();
            vomozGlobalInfo.setCallerId(config.getMobilePhone());
            vomozGlobalInfo.setFirstName(firstNameEditText.getText().toString());
            vomozGlobalInfo.setLastName(lastNameEditText.getText().toString());
            vomozGlobalInfo.setEmailAddress(emailEditText.getText().toString());
            vomozGlobalInfo.setPassword(passwordEditText.getText().toString());
            vomozGlobalInfo.setCallerId(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
            vomozGlobalInfo.setResetPasswordQuestion1(referenceData.getGroup1Questions().get(question1Options.getSelectedItemPosition() - 1).getQuestionId());
            vomozGlobalInfo.setResetPasswordQuestion2(referenceData.getGroup2Questions().get(question2Options.getSelectedItemPosition() - 1).getQuestionId());
            vomozGlobalInfo.setResetPasswordAnswer1(question1Answer.getText().toString().replace(" ", "").toLowerCase());
            vomozGlobalInfo.setResetPasswordAnswer2(question2Answer.getText().toString().replace(" ", "").toLowerCase());

            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(MyProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE)
                    .setContentText("Saving profile...Please wait")
                    .setTitleText(getString(R.string.app_name));
            sweetAlertDialog.show();

            Call<BaseServiceResponse> call = apiInterface.updateAllGlobalInfo(vomozGlobalInfo, "", ApplicationUtils.APP_ID);
            call.enqueue(new Callback<BaseServiceResponse>() {
                @Override
                public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                    if (response.isSuccessful()) {

                        sweetAlertDialog.setContentText("Profile saved successfully\n\n");
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });

                    } else {
                        sweetAlertDialog.setContentText("Profile was not saved successfully.\n\n" + response.body().getMessage().getDescription());
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                    }
                }

                @Override
                public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                    sweetAlertDialog.setContentText("Profile cannot be saved successfully.");
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            });
                }
            });
        }
    }

    private String validateForm() {
        String message = null;
        Spinner question1Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_1);
        Spinner question2Options = (Spinner) findViewById(R.id.resetpasswordquestion_cardview_spinner_2);

        EditText question1Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer1_edit_txt);
        EditText question2Answer = (EditText) findViewById(R.id.resetpasswordquestion_cardview_answer2_edit_txt);
        EditText passwordEditText = (EditText) findViewById(R.id.password_cardview_password_edit_txt);
        emailEditText = (EditText) findViewById(R.id.edit_email);
        passwordEditText = (EditText) findViewById(R.id.edit_password);

        if(firstNameEditText.getText().toString().length() < 2) {
            message = "First name is not valid";
        }
        else if(lastNameEditText.getText().toString().length() < 2) {
            message = "Last name is not valid";
        }
        else if (question1Options.getSelectedItemPosition() == 0) {
            message = "Please select the first question";
        }
        else if (question2Options.getSelectedItemPosition() == 0) {
            message = "Please select the second question";
        }
        else if (question1Answer.getText().toString().length() == 0)
            message = "You must provide an answer to the first question";
        else if (question2Answer.getText().toString().length() == 0)
            message = "You must provide an answer to the second question";
        else
            message = ApplicationUtils.validatePassword(passwordEditText.getText().toString());

        return message;
    }
}

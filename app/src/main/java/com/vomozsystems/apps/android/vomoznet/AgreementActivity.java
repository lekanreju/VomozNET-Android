package com.vomozsystems.apps.android.vomoznet;

import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.System.exit;

public class AgreementActivity extends AppCompatActivity {

    boolean agreed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        checkAgreement();
    }

    private void checkAgreement() {
        agreed = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(LoginActivity.AGREEMENT, false);
        final LinearLayout agreementLayout = (LinearLayout) findViewById(R.id.agree_layout);
        final Button nextButton = (Button) findViewById(R.id.agree_next_button);
        final Button quitButton = (Button) findViewById(R.id.agree_quit_button);
        if(agreed) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        RadioButton yes = (RadioButton) findViewById(R.id.agree_layout_yes);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(AgreementActivity.this).edit().putBoolean(LoginActivity.AGREEMENT, true).apply();
                nextButton.setEnabled(true);
                nextButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                //agreementLayout.setVisibility(GONE);
            }
        });

        RadioButton no = (RadioButton) findViewById(R.id.agree_layout_no);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(AgreementActivity.this).edit().putBoolean(LoginActivity.AGREEMENT, false).apply();
                nextButton.setEnabled(false);
                nextButton.setTextColor(Color.LTGRAY);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agreed = PreferenceManager.getDefaultSharedPreferences(AgreementActivity.this).getBoolean(LoginActivity.AGREEMENT, false);
                if(agreed) {
                    Intent intent = new Intent(AgreementActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    new SweetAlertDialog(AgreementActivity.this, SweetAlertDialog.NORMAL_TYPE)
                            .setContentText("You have declined to be identified via your phone number. Quit ?")
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
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(AgreementActivity.this, SweetAlertDialog.NORMAL_TYPE)
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
    }
}

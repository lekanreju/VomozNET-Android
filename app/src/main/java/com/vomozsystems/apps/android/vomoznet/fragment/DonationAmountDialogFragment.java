package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.ContributionType;
import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;

/**
 * Created by leksrej on 7/5/17.
 */

public class DonationAmountDialogFragment extends DialogFragment {

    String textAmount = "0";
    private Button buttonOne;
    private Button buttonTwo;
    private Button buttonThree;
    private Button buttonFour;
    private Button buttonFive;
    private Button buttonSix;
    private Button buttonSeven;
    private Button buttonEight;
    private Button buttonNine;
    private Button buttonZero;
    private Button buttonPound;
    private ImageView buttonBackSpace;
    private TextView pinTextView;
    private NumberFormat numberFormat;
    private Realm realm;
    private TextView destinationTargetTextView;
    private TextView currencyTextView;
    private TextView maxTextView;
    private TextView totalAmountToContribute;
    private ContributionType contributionType;
    private PaymentInfo paymentInfo;
    double maxm = 10000D;
    double minm = 2D;
    public static DonationAmountDialogFragment newInstance(TextView textView, ContributionType contributionType, TextView totalAmountToContribute, PaymentInfo paymentInfo) {
        DonationAmountDialogFragment fragment = new DonationAmountDialogFragment();
        fragment.destinationTargetTextView = textView;
        fragment.totalAmountToContribute = totalAmountToContribute;
        fragment.contributionType = contributionType;
        fragment.paymentInfo = paymentInfo;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Realm realm = Realm.getDefaultInstance();
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(true);
        String currencyCode = "USD";
        String engineType = paymentInfo.getPaymentEngine().get("type");

        if(engineType.equalsIgnoreCase("PAYSTACK")) {
            currencyCode = paymentInfo.getPaymentEngine().get("paystack_currency");
        }
        else if(engineType.equalsIgnoreCase("RAVE")) {
            currencyCode = paymentInfo.getPaymentEngine().get("rave_currency");
        }
        else if(engineType.equalsIgnoreCase("PAYPAL")) {
            currencyCode = paymentInfo.getPaymentEngine().get("paypal_currency");
        }

        try {
            Double amount = numberFormat.parse(pinTextView.getText().toString()).doubleValue();
            if (amount > maxm) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText("Amount to high. Maximum is: " + numberFormat.format(maxm) + currencyCode)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }else if (amount < minm) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getString(R.string.app_name))
                        .setContentText("Amount too small. Minimum is: " + numberFormat.format(minm) + " " + currencyCode)
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }).show();
            }
            else {
                realm.beginTransaction();
                contributionType.setAmount(numberFormat.parse(pinTextView.getText().toString()).doubleValue());
                realm.copyToRealmOrUpdate(contributionType);
                realm.commitTransaction();
                List<ContributionType> contributionTypeList = realm.where(ContributionType.class).findAll();
                Double total = 0D;
                for (ContributionType contributionType : contributionTypeList) {
                    total += contributionType.getAmount();
                }
                totalAmountToContribute.setText(ApplicationUtils.getCurrencySymbol(currencyCode) + numberFormat.format(total) + " " + currencyCode);
                destinationTargetTextView.setText(pinTextView.getText().toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
            if (realm.isInTransaction())
                realm.cancelTransaction();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.amount_dialog, container, false);
        Button closeButton = (Button) view.findViewById(R.id.close_button);
        realm = Realm.getDefaultInstance();
        currencyTextView = (TextView) view.findViewById(R.id.amount_currency);
        maxTextView = (TextView) view.findViewById(R.id.amount_max);
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(true);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        pinTextView = (TextView) view.findViewById(R.id.pin_text_view);
        buttonOne = (Button) view.findViewById(R.id.button_one);
        buttonTwo = (Button) view.findViewById(R.id.button_two);
        buttonThree = (Button) view.findViewById(R.id.button_three);
        buttonFour = (Button) view.findViewById(R.id.button_four);
        buttonFive = (Button) view.findViewById(R.id.button_five);
        buttonSix = (Button) view.findViewById(R.id.button_six);
        buttonSeven = (Button) view.findViewById(R.id.button_seven);
        buttonEight = (Button) view.findViewById(R.id.button_eight);
        buttonNine = (Button) view.findViewById(R.id.button_nine);
        buttonZero = (Button) view.findViewById(R.id.button_zero);

        String engineType = paymentInfo.getPaymentEngine().get("type");
        String currencyCode = "USD";
        if(engineType.equalsIgnoreCase("PAYPAL")) {
            currencyCode = paymentInfo.getPaymentEngine().get("paypal_currency");
            try {
                maxm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paypal_maximum_contribution"));
                minm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paypal_minimum_contribution"));
            }catch(Exception e) {

            }
        }
        else if(engineType.equalsIgnoreCase("PAYSTACK")) {
            currencyCode = paymentInfo.getPaymentEngine().get("paystack_currency");
            try {
                maxm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paystack_maximum_contribution"));
                minm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paystack_minimum_contribution"));
            }catch(Exception e) {

            }
        }
        else if(engineType.equalsIgnoreCase("RAVE")) {
            currencyCode = paymentInfo.getPaymentEngine().get("rave_currency");
            try {
                maxm = Double.parseDouble(paymentInfo.getPaymentEngine().get("rave_maximum_contribution"));
                minm = Double.parseDouble(paymentInfo.getPaymentEngine().get("rave_minimum_contribution"));
            }catch(Exception e) {

            }
        }
        String max = numberFormat.format(minm) + " " + currencyCode;
        currencyTextView.setText("Currency: " + currencyCode);
        maxTextView.setText("Min Amount: " + max);
        buttonBackSpace = (ImageView) view.findViewById(R.id.backspace_button);
        detectAmount(textAmount);
        buttonOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "1";
                detectAmount(textAmount);
            }
        });
        buttonTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "2";
                detectAmount(textAmount);
            }
        });
        buttonThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "3";
                detectAmount(textAmount);
            }
        });
        buttonFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "4";
                detectAmount(textAmount);
            }
        });
        buttonFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "5";
                detectAmount(textAmount);
            }
        });
        buttonSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "6";
                detectAmount(textAmount);
            }
        });
        buttonSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "7";
                detectAmount(textAmount);
            }
        });
        buttonEight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "8";
                detectAmount(textAmount);
            }
        });
        buttonNine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "9";
                detectAmount(textAmount);
            }
        });
        buttonZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textAmount += "0";
                detectAmount(textAmount);
            }
        });
        buttonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textAmount.toString().length() > 0)
                    textAmount = textAmount.substring(0, textAmount.length() - 1);
                detectAmount(textAmount);
            }
        });

        getDialog().setTitle("Connect");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return view;
    }

    private void detectAmount(String amount) {
        try {
            if (Double.valueOf(amount) / 100 <= maxm) {
                pinTextView.setText(numberFormat.format(Double.valueOf(amount) / 100));
            }else {
                textAmount = textAmount.substring(0, textAmount.length() - 1);
            }
        } catch (Exception e) {
            numberFormat.format(Double.valueOf("0") / 100);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            //dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
            dialog.getWindow().setLayout(width, height);
        }
    }
}

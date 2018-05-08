package com.vomozsystems.apps.android.vomoznet.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.vomozsystems.apps.android.vomoznet.GiveActivity;
import com.vomozsystems.apps.android.vomoznet.PaystackActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.RaveActivity;
import com.vomozsystems.apps.android.vomoznet.WizardCallbacks;
import com.vomozsystems.apps.android.vomoznet.entity.BankAccount;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.ContributionType;
import com.vomozsystems.apps.android.vomoznet.entity.CreditCard;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationType;
import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.ConfirmPaymentResponse;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.LogPaymentRequest;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationResponse;
import com.vomozsystems.apps.android.vomoznet.service.MemberInfoRequest;
import com.vomozsystems.apps.android.vomoznet.service.SignUpResponse;
import com.vomozsystems.apps.android.vomoznet.service.UserLoginResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import co.paystack.android.PaystackSdk;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfirmAndSubmitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmAndSubmitFragment extends Fragment {

    private PaymentInfo paymentInfo;
    private BankAccount selectedBankAccount;
    private CreditCard selectedCreditCard;
    private PayPalConfiguration paypalConfig;
    private View view;
    private WizardCallbacks wizardCallbacks;
    private double totalAmount = 0D;
    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public void setPaymentInfo(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public ConfirmAndSubmitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConfirmAndSubmitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmAndSubmitFragment newInstance(PaymentInfo paymentInfo) {
        ConfirmAndSubmitFragment fragment = new ConfirmAndSubmitFragment();
        fragment.paymentInfo = paymentInfo;
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WizardCallbacks) {
            wizardCallbacks = (WizardCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WizardCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        wizardCallbacks = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_confirm_and_submit, container, false);

        wizardCallbacks.setTitle("Give - Confirm and Submit");

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setGroupingUsed(true);

        Button startOverButton = view.findViewById(R.id.completed_start_button);
        startOverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wizardCallbacks.onStartOver(paymentInfo);
            }
        });
        Button cancelButton = view.findViewById(R.id.completed_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        Button cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        Button submitButton = (Button) view.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitContribution();
            }
        });
        TextView organizationTextView = (TextView) view.findViewById(R.id.confirmation_organization_text_view);
        organizationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView amountTextView = (TextView) view.findViewById(R.id.confirmation_total_amount_text_view);
        amountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        TextView cardOrAccountTextView = (TextView) view.findViewById(R.id.confirmation_cardoraccount_text_view);
        cardOrAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ImageView cardOrAccountImageView = (ImageView) view.findViewById(R.id.confirmation_cardoraccount_image_view);
        cardOrAccountImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        Realm realm = Realm.getDefaultInstance();

        if(paymentInfo != null) {
            String currencyCode = "USD";
            String engineType = paymentInfo.getPaymentEngine().get("type");

            if (engineType.equalsIgnoreCase("PAYSTACK")) {
                currencyCode = paymentInfo.getPaymentEngine().get("paystack_currency");
            } else if (engineType.equalsIgnoreCase("RAVE")) {
                currencyCode = paymentInfo.getPaymentEngine().get("rave_currency");
            } else if (engineType.equalsIgnoreCase("PAYPAL")) {
                currencyCode = paymentInfo.getPaymentEngine().get("paypal_currency");
            }

            organizationTextView.setText(paymentInfo.getColorWrapper().getName());
            List<ContributionType> types = realm.where(ContributionType.class).findAll();
            Double total = 0D;
            for (ContributionType contributionType : types) {
                total += contributionType.getAmount();
            }
            amountTextView.setText(ApplicationUtils.getCurrencySymbol(currencyCode) + numberFormat.format(total) + " " + currencyCode);
            CreditCard creditCard = paymentInfo.getCreditCard();

            if (paymentInfo.getPaymentEngine().get("type").equalsIgnoreCase("paystack")) {
                cardOrAccountTextView.setText("Paystack");
                cardOrAccountImageView.setImageResource(R.mipmap.ic_markc);
            } else if (paymentInfo.getPaymentEngine().get("type").equalsIgnoreCase("rave")) {
                cardOrAccountTextView.setText("Rave");
                cardOrAccountImageView.setImageResource(R.mipmap.ic_rave);
            } else if (paymentInfo.getPaymentEngine().get("type").equalsIgnoreCase("paypal")) {
                cardOrAccountTextView.setText("Paypal");
                cardOrAccountImageView.setImageResource(R.mipmap.ic_paypal_color);
            } else if (paymentInfo.getPaymentEngine().get("type").equalsIgnoreCase("check")) {
                selectedBankAccount = new BankAccount();
                selectedBankAccount.setAccountNumber(creditCard.getAccountNumber());
                selectedBankAccount.setRoutingNumber(creditCard.getRoutingNumber());
                cardOrAccountImageView.setImageResource(R.mipmap.ic_bankaccount);
                if (null != selectedBankAccount && null != selectedBankAccount.getAccountNumber()) {
                    String ex = "****-" + selectedBankAccount.getAccountNumber().substring(selectedBankAccount.getAccountNumber().length() - 4);
                    cardOrAccountTextView.setText(ex);
                }
            } else {
                selectedCreditCard = creditCard;
                try {
                    if (selectedCreditCard.getCreditCardNumber().startsWith("4")) {
                        cardOrAccountImageView.setImageResource(R.mipmap.ic_visa);
                    } else if (selectedCreditCard.getCreditCardNumber().startsWith("5")) {
                        cardOrAccountImageView.setImageResource(R.mipmap.ic_mastercard);
                    } else if (selectedCreditCard.getCreditCardNumber().startsWith("3")) {
                        cardOrAccountImageView.setImageResource(R.mipmap.ic_amex);
                    } else {
                        cardOrAccountImageView.setImageResource(R.mipmap.ic_card);
                    }
                    if (null != selectedCreditCard.getCreditCardId()) {
                        if (selectedCreditCard.getLast4Digits().equalsIgnoreCase("0")) {
                            String ex = "****-" + selectedCreditCard.getCreditCardNumber().substring(selectedCreditCard.getCreditCardNumber().length() - 4);
                            cardOrAccountTextView.setText(ex);
                        } else
                            cardOrAccountTextView.setText("****-" + selectedCreditCard.getLast4Digits());
                    } else {
                        String ex = "****-" + selectedCreditCard.getCreditCardNumber().substring(selectedCreditCard.getCreditCardNumber().length() - 4);
                        cardOrAccountTextView.setText(ex);
                    }
                } catch (Exception e) {

                }
            }
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.completed_card_grogressbar);
        final TextView processingTextView = (TextView) view.findViewById(R.id.completed_card_processing_textview);
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        final LinearLayout completedButtonLayout  = view.findViewById(R.id.completed_button_layout);
        processingTextView.setText("Payment not completed successfully!");
        progressBar.setVisibility(GONE);
        completedButtonLayout.setVisibility(VISIBLE);
        if (requestCode == GiveActivity.PAYSTACK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String paymentId = data.getStringExtra(GiveActivity.PAYMENT_ID);
            if(paymentId != null)
                logPayment(paymentId, "paystack_currency");
        }
        else if (requestCode == GiveActivity.RAVE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String paymentId = data.getStringExtra(GiveActivity.PAYMENT_ID);
            if(paymentId != null)
                logPayment(paymentId, "rave_currency");
        }
        else if (requestCode == GiveActivity.PAYPAL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (null != confirm && null != confirm.getProofOfPayment()) {
              String paymentId = confirm.getProofOfPayment().getPaymentId();
              if(paymentId != null)
                  logPayment(paymentId, "paypal_currency");
            }
        }  else {
            processingTextView.setText("Payment not completed successfully");
            completedImageView.setVisibility(VISIBLE);
            completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
            progressBar.setVisibility(GONE);
            completedButtonLayout.setVisibility(VISIBLE);
        }
    }

    private void logPayment(final String paymentId, final String currency) {
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.completed_card_grogressbar);
        final TextView processingTextView = (TextView) view.findViewById(R.id.completed_card_processing_textview);
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        final LinearLayout completedButtonLayout = view.findViewById(R.id.completed_button_layout);
        processingTextView.setText("Processing contribution...Please wait");
        progressBar.setVisibility(VISIBLE);
        completedButtonLayout.setVisibility(GONE);
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        final User user = realm.where(User.class).findFirst();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DonationCenterResponse> call = apiInterface.getMemberDonationCenters(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()), getActivity().getResources().getString(R.string.org_filter), "", "");
        call.enqueue(new Callback<DonationCenterResponse>() {
            @Override
            public void onResponse(Call<DonationCenterResponse> call, Response<DonationCenterResponse> response) {
                if(response.isSuccessful()) {
                    boolean found = false;
                    for(DonationCenter donationCenter1: response.body().getResponseData()) {
                        String txtId = Long.toString(donationCenter1.getTexterCardId());
                        if(user.getTexterCardId().equalsIgnoreCase(txtId)) {
                            found = true;
                            doLog(donationCenter1, paymentId, currency);
                            break;
                        }
                    }
                    if(!found) {
                        String message = "Your contribution was completed but it is PENDING verification. Your payment ID is : " + paymentId;
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Payment submitted, but PENDING verification");
                        dialog1.show();
                        processingTextView.setText(message);
                        completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                        completedImageView.setVisibility(VISIBLE);
                    }
                }else {
                    String message = "Your contribution was completed but it is PENDING verification. Your payment ID is : " + paymentId;
                    SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Payment submitted, but PENDING verification");
                    dialog1.show();
                    processingTextView.setText(message);
                    completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                    completedImageView.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<DonationCenterResponse> call, Throwable t) {
                String message = "Your contribution was completed but it is PENDING verification. Your payment ID is : " + paymentId;
                SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("Payment submitted, but PENDING verification");
                dialog1.show();
                processingTextView.setText(message);
                completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                completedImageView.setVisibility(VISIBLE);
            }
        });
    }

    private void doLog(DonationCenter defaultDonationCenter, final String paymentId, final String currency) {
        final Realm realm = Realm.getDefaultInstance();
        final User user = realm.where(User.class).findFirst();
        final ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.completed_card_grogressbar);
        final TextView processingTextView = (TextView) view.findViewById(R.id.completed_card_processing_textview);
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        final LinearLayout completedButtonLayout = view.findViewById(R.id.completed_button_layout);
        totalAmount = 0D;
        try {
            processingTextView.setText("Processing contribution...Please wait");
            progressBar.setVisibility(VISIBLE);
            completedButtonLayout.setVisibility(GONE);
            MakeDonationInterface makeDonationInterface = ApplicationUtils.getDonationInterface();
            if (null != makeDonationInterface) {
                List<ContributionType> types = realm.where(ContributionType.class).findAll();
                int i = 1;
                double amount1 = 0D;
                double amount2 = 0D;
                double amount3 = 0D;
                String contributionType1 = null;
                String contributionType2 = null;
                String contributionType3 = null;
                if (null != types)
                    for (ContributionType contributionType : types) {
                        totalAmount += contributionType.getAmount();
                        DonationType donationType = realm.where(DonationType.class).equalTo("donationCenterCardId", paymentInfo.getColorWrapper().getCardId()).equalTo("description", contributionType.getDescription()).findFirst();
                        if (i == 1) {
                            amount1 = contributionType.getAmount();
                            contributionType1 = donationType.getCode() + "|||" + donationType.getDescription();
                        }
                        if (i == 2) {
                            amount2 = contributionType.getAmount();
                            contributionType2 = donationType.getCode() + "|||" + donationType.getDescription();
                        }
                        if (i == 3) {
                            amount3 = contributionType.getAmount();
                            contributionType3 = donationType.getCode() + "|||" + donationType.getDescription();
                        }
                        i++;
                    }
                String donateToDefaultDonationCenter = (defaultDonationCenter.getCardId().equals(paymentInfo.getColorWrapper().getCardId()) ? "1" : "0");

                LogPaymentRequest logPaymentRequest = new LogPaymentRequest();
                logPaymentRequest.setUniversalAuthToken(user.getAuthToken());
                logPaymentRequest.setAmount1(amount1);
                logPaymentRequest.setAmount2(amount2);
                logPaymentRequest.setAmount3(amount3);
                logPaymentRequest.setContributionType1(contributionType1);
                logPaymentRequest.setContributionType2(contributionType2);
                logPaymentRequest.setContributionType3(contributionType3);
                logPaymentRequest.setDefaultDonationCenterId(defaultDonationCenter.getCardId());
                logPaymentRequest.setDefaultIsReceiving(donateToDefaultDonationCenter);
                logPaymentRequest.setDefaultMerchantIdCode(defaultDonationCenter.getMerchantIdCode());
                logPaymentRequest.setDefaultUniqueCCId(0L);
                logPaymentRequest.setGiverEmail(user.getEmail());
                logPaymentRequest.setGiverFirstName(user.getFirstName());
                logPaymentRequest.setGiverLastName(user.getLastName());
                logPaymentRequest.setGiverPhoneNumber(user.getMobilePhone());
                logPaymentRequest.setProcesserName(paymentInfo.getPaymentEngine().get("type"));
                logPaymentRequest.setProcesserTotalAmountContributed(Double.toString(totalAmount));
                logPaymentRequest.setProcesserTransactionId(paymentId);
                logPaymentRequest.setProcessorCurrencyCode(paymentInfo.getPaymentEngine().get(currency));
                logPaymentRequest.setReceivingDonationCenterId(paymentInfo.getColorWrapper().getCardId());
                logPaymentRequest.setReceivingMerchantIdCode(paymentInfo.getColorWrapper().getMerchantIdCode());
                logPaymentRequest.setServiceName("LogInlineProcessedContributionsByThisGiver");

                Call<BaseServiceResponse> call = apiInterface.logPayment(logPaymentRequest, "", ApplicationUtils.APP_ID);
                call.enqueue(new Callback<BaseServiceResponse>() {
                    @Override
                    public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                        progressBar.setVisibility(GONE);
                        completedButtonLayout.setVisibility(VISIBLE);
                        if (!response.isSuccessful()) {
                            String message = "Your contribution was completed but it is PENDING verification. Your payment ID is : " + paymentId;
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("Payment submitted, but PENDING verification");
                            dialog1.show();
                            processingTextView.setText(message);
                            completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                            completedImageView.setVisibility(VISIBLE);
                        } else {
                            NumberFormat numberFormat = NumberFormat.getNumberInstance();
                            numberFormat.setMinimumFractionDigits(2);
                            numberFormat.setMaximumFractionDigits(2);
                            numberFormat.setGroupingUsed(true);
                            String message = "Your contribution of " + numberFormat.format(totalAmount) + " " + paymentInfo.getPaymentEngine().get(currency) + " was completed successfully";
                            processingTextView.setText("Completed! \n\n" + message);
                            completedImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
                            completedImageView.setVisibility(VISIBLE);
                            progressBar.setVisibility(GONE);
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .showCancelButton(false)
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            realm.beginTransaction();
                                            realm.delete(ContributionType.class);
                                            realm.commitTransaction();
                                        }
                                    })
                                    .setContentText("Completed successfully");
                            dialog1.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                        completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                        processingTextView.setText("Payment completed successfully...But is being verified.\n\nYour Payment ID is: " + paymentId);
                        progressBar.setVisibility(GONE);
                        completedImageView.setVisibility(VISIBLE);
                        completedButtonLayout.setVisibility(VISIBLE);
                    }
                });

            }
        } catch (Exception e) {
            Log.i(getClass().getSimpleName(), e.getLocalizedMessage());
            completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
            processingTextView.setText("Payment completed successfully...But is being verified.\n\nYour Payment ID is: " + paymentId);
            progressBar.setVisibility(GONE);
            completedImageView.setVisibility(VISIBLE);
            completedButtonLayout.setVisibility(VISIBLE);
        }
    }
    private void submitContribution() {
        CardView completedCardView = view.findViewById(R.id.completed_card_view);
        CardView confirmationCardView = view.findViewById(R.id.confirmation_card_view);
        completedCardView.setVisibility(VISIBLE);
        confirmationCardView.setVisibility(GONE);
        ProgressBar progressBar = view.findViewById(R.id.completed_card_grogressbar);
        TextView processingTextView =  view.findViewById(R.id.completed_card_processing_textview);
        ImageView completedImageView = view.findViewById(R.id.completed_card_imageview);
        progressBar.setVisibility(VISIBLE);
        progressBar.setIndeterminate(true);
        processingTextView.setText("Processing...Please Wait");

        Realm realm = Realm.getDefaultInstance();
        String type = paymentInfo.getPaymentEngine().get("type").toLowerCase();

        DonationCenter defaultDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        if (defaultDonationCenter == null) {
            signUpToADonationCenter(type);
        } else {
            if (type.equalsIgnoreCase("paypal")) {
                makePaypalDonation();
            } else if (type.equalsIgnoreCase("paystack")) {
                makePaystackDonation();
            } else if (type.equalsIgnoreCase("rave")) {
                makeRaveDonation();
            }else {
                makeCardOrCheckDonation(type);
            }
        }
    }

    private void signUpToADonationCenter(final String type) {
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.completed_card_grogressbar);
        final TextView processingTextView = (TextView) view.findViewById(R.id.completed_card_processing_textview);
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        MakeDonationInterface makeDonationInterface = ApplicationUtils.getDonationInterface();
        if (null != makeDonationInterface) {
            Call<SignUpResponse> call1 = makeDonationInterface.signUpMemberToDonationCenter(paymentInfo.getColorWrapper().getCardId(),
                    paymentInfo.getColorWrapper().getMerchantIdCode(),
                    ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()),
                    config.getEmail(),
                    config.getPassword(),
                    config.getFirstName(),
                    config.getLastName(),
                    "SignUpANewMemberToThisDonationCenter");
            call1.enqueue(new Callback<SignUpResponse>() {
                @Override
                public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                    if (response.isSuccessful()) {
                        SignUpResponse signUpResponse = response.body();
                        if (signUpResponse.getStatus().equalsIgnoreCase("1")) {
                            downLoadUserAndMakePayment(type);
                        } else {
                            progressBar.setVisibility(GONE);
                            processingTextView.setText("Cannot connect to " + paymentInfo.getColorWrapper().getName() + "\n\n" + signUpResponse.getFaultCode() + ":" + signUpResponse.getFaultString());
                            completedImageView.setVisibility(VISIBLE);
                            completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("Failure:\n" + signUpResponse.getFaultCode() + ": " + signUpResponse.getFaultString() + "\n\nCannot connect to " + paymentInfo.getColorWrapper().getName());
                            dialog1.show();
                        }
                    } else {
                        progressBar.setVisibility(GONE);
                        completedImageView.setVisibility(VISIBLE);
                        processingTextView.setText("7200 - Cannot connect to " + paymentInfo.getColorWrapper().getName());
                        completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("7200 - Cannot connect to " + paymentInfo.getColorWrapper().getName());
                        dialog1.show();
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    progressBar.setVisibility(GONE);
                    completedImageView.setVisibility(VISIBLE);
                    processingTextView.setText("7100 - Network Failure \n\nCannot connect to " + paymentInfo.getColorWrapper().getName());
                    completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                    SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("7100 - Network Failure\n\nCannot connect to " + paymentInfo.getColorWrapper().getName());
                    dialog1.show();
                }
            });
        }
    }

    private void downLoadUserAndMakePayment(final String type) {
        final Realm realm = Realm.getDefaultInstance();
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.completed_card_grogressbar);
        final TextView processingTextView = (TextView) view.findViewById(R.id.completed_card_processing_textview);
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        Config config = realm.where(Config.class).findFirst();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        MemberInfoRequest memberInfoRequest = new MemberInfoRequest();
        memberInfoRequest.setPhoneNumber(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
        memberInfoRequest.setPassword(config.getPassword());
        memberInfoRequest.setCenterCardId(paymentInfo.getColorWrapper().getCardId());
        Call<UserLoginResponse> call = apiService.login(memberInfoRequest, getActivity().getResources().getString(R.string.org_filter), "", ApplicationUtils.APP_ID);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful() && null != response.body().getResponseData()) {
                    realm.beginTransaction();
                    realm.delete(User.class);
                    User user = response.body().getResponseData();
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();
                    if (null != response.body().getResponseData() && null != response.body().getResponseData().getDonationCenters()) {
                        if (type.equalsIgnoreCase("paypal")) {
                            makePaypalDonation();
                        }  else if (type.equalsIgnoreCase("paystack")) {
                            makePaystackDonation();
                        }  else if (type.equalsIgnoreCase("rave")) {
                            makeRaveDonation();
                        }
                        else
                            makeCardOrCheckDonation(type);
                    }
                } else {
                    progressBar.setVisibility(GONE);
                    completedImageView.setVisibility(VISIBLE);
                    processingTextView.setText("7400 - Failure. \n\nYour Vomoz.NET profile cannot be retrieved.");
                    completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                    SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("7400 - Failure. \n\nYour Vomoz.NET profile cannot be retrieved");
                    dialog1.show();
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                progressBar.setVisibility(GONE);
                completedImageView.setVisibility(VISIBLE);
                processingTextView.setText("7500 - Network Failure. \n\nYour Vomoz.NET profile cannot be retrieved.");
                completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("7500 - Network Failure. \n\nYour Vomoz.NET profile cannot be retrieved.");
                dialog1.show();
            }
        });
    }

    private void makePaystackDonation() {
        if(null != paymentInfo) {
            String publicKey = paymentInfo.getPaymentEngine().get("paystack_public");
            PaystackSdk.setPublicKey(publicKey);
            Intent intent = new Intent(getActivity(), PaystackActivity.class);

            Realm realm = Realm.getDefaultInstance();
            Config config = realm.where(Config.class).findFirst();
            String liveString = paymentInfo.getPaymentEngine().get("rave_live_mode");
            boolean live = null != liveString && liveString.equalsIgnoreCase("1");

            double totalAmount = 0D;
            List<ContributionType> types = realm.where(ContributionType.class).findAll();
            if (null != types)
                for (ContributionType contributionType : types) {
                    totalAmount += contributionType.getAmount();
                }
            intent.putExtra(GiveActivity.TOTAL_AMOUNT, totalAmount);
            intent.putExtra(GiveActivity.AMOUNT_CURRENCY, paymentInfo.getPaymentEngine().get("paystack_currency"));
            intent.putExtra(GiveActivity.PUBLIC_KEY, paymentInfo.getPaymentEngine().get("paystack_public"));
            intent.putExtra(GiveActivity.EMAIL, config.getEmail());
            intent.putExtra(GiveActivity.SECRET_KEY, paymentInfo.getPaymentEngine().get("paystack_secret"));
            intent.putExtra(GiveActivity.AMOUNT_COUNTRY, paymentInfo.getPaymentEngine().get("paystack_country"));
            intent.putExtra(GiveActivity.PAYMENT_NARRATION, getString(R.string.app_name) + " donation to " + paymentInfo.getColorWrapper().getName());
            intent.putExtra(GiveActivity.FIRST_NAME, config.getFirstName());
            intent.putExtra(GiveActivity.LAST_NAME, config.getLastName());
            intent.putExtra(GiveActivity.IS_LIVE, live);

            startActivityForResult(intent, GiveActivity.PAYSTACK_REQUEST_CODE);
        }
    }

    private void makeRaveDonation() {
        if(null != paymentInfo) {
            Realm realm = Realm.getDefaultInstance();
            Config config = realm.where(Config.class).findFirst();
            Intent intent = new Intent(getActivity(), RaveActivity.class);
            String liveString = paymentInfo.getPaymentEngine().get("rave_live_mode");
            boolean live = null != liveString && liveString.equalsIgnoreCase("1");

            double totalAmount = 0D;
            List<ContributionType> types = realm.where(ContributionType.class).findAll();
            if (null != types)
                for (ContributionType contributionType : types) {
                    totalAmount += contributionType.getAmount();
                }

            intent.putExtra(GiveActivity.TOTAL_AMOUNT, totalAmount);
            intent.putExtra(GiveActivity.AMOUNT_CURRENCY, paymentInfo.getPaymentEngine().get("rave_currency"));
            intent.putExtra(GiveActivity.PUBLIC_KEY, paymentInfo.getPaymentEngine().get("rave_public_key"));
            intent.putExtra(GiveActivity.EMAIL, config.getEmail());
            intent.putExtra(GiveActivity.SECRET_KEY, paymentInfo.getPaymentEngine().get("rave_secret_key"));
            intent.putExtra(GiveActivity.AMOUNT_COUNTRY, paymentInfo.getPaymentEngine().get("rave_country"));
            intent.putExtra(GiveActivity.PAYMENT_NARRATION, getString(R.string.app_name) + " donation to " + paymentInfo.getColorWrapper().getName());
            intent.putExtra(GiveActivity.FIRST_NAME, config.getFirstName());
            intent.putExtra(GiveActivity.LAST_NAME, config.getLastName());
            intent.putExtra(GiveActivity.IS_LIVE, live);
            startActivityForResult(intent, GiveActivity.RAVE_REQUEST_CODE);
        }
    }

    private void makePaypalDonation() {
        if (null != paymentInfo) {
            String paypalClientId = paymentInfo.getPaymentEngine().get("paypal_client_id");
            if (null != paypalClientId) {
                String liveMode = paymentInfo.getPaymentEngine().get("paypal_live_mode");
                if (null != liveMode && liveMode.equalsIgnoreCase("1"))
                    paypalConfig = new PayPalConfiguration()
                            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                            .clientId(paypalClientId);
                else
                    paypalConfig = new PayPalConfiguration()
                            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
                            .clientId(paypalClientId);
                String message = null;
                double totalAmount = 0D;
                Realm realm = Realm.getDefaultInstance();
                List<ContributionType> types = realm.where(ContributionType.class).findAll();
                if (null != types)
                    for (ContributionType contributionType : types) {
                        totalAmount += contributionType.getAmount();
                    }

                Intent paypalIntent = new Intent(getActivity(), PayPalService.class);
                paypalIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
                getActivity().startService(paypalIntent);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(totalAmount)), Currency.getInstance(Locale.getDefault()).getCurrencyCode(), "Vomoz Payment", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                startActivityForResult(intent, GiveActivity.PAYPAL_REQUEST_CODE);

            } else {
                SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("This organization cannot receive contributions via Paypal at this time.");
                dialog.show();
            }
        } else {
            SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText("No organization was selected");
            dialog.show();
        }
    }

    private void makeCardOrCheckDonation(final String paymentMethod) {
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        completedImageView.setImageDrawable(null);
        final Realm realm = Realm.getDefaultInstance();
        final DonationCenter defaultDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        final MakeDonationInterface makeDonationInterface = ApplicationUtils.getDonationInterface();
        if (null != makeDonationInterface) {
            if (null != defaultDonationCenter) {
                doDonation(paymentMethod);
            } else {
                Config config = realm.where(Config.class).findFirst();
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                MemberInfoRequest memberInfoRequest = new MemberInfoRequest();
                memberInfoRequest.setPhoneNumber(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
                memberInfoRequest.setPassword(config.getPassword());
                memberInfoRequest.setCenterCardId(paymentInfo.getColorWrapper().getCardId());
                Call<UserLoginResponse> call = apiService.login(memberInfoRequest, getActivity().getResources().getString(R.string.org_filter), "", ApplicationUtils.APP_ID);
                call.enqueue(new Callback<UserLoginResponse>() {
                    @Override
                    public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                        if (response.isSuccessful()) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(response.body().getResponseData());
                            realm.commitTransaction();
                            doDonation(paymentMethod);
                        } else {
                            // cannot load
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("Failure - Your profile cannot be loaded");
                            dialog1.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                        // network failure
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Network Failure - Your profile cannot be loaded");
                        dialog1.show();
                    }
                });

            }
        }
    }

    private void doDonation(final String paymentMethod) {
        final Realm realm = Realm.getDefaultInstance();
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.completed_card_grogressbar);
        final TextView processingTextView = (TextView) view.findViewById(R.id.completed_card_processing_textview);
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        final DonationCenter defaultDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        Double amount1, amount2, amount3;
        String contributionType1, contributionType2, contributionType3;
        final MakeDonationInterface makeDonationInterface = ApplicationUtils.getDonationInterface();
        final Config config = realm.where(Config.class).findFirst();
        Long defaultDonationCenterId = (defaultDonationCenter == null ? 1L : defaultDonationCenter.getCardId());
        String defaultMerchantIdCode = (defaultDonationCenter == null ? "A00000" : defaultDonationCenter.getMerchantIdCode());
        final Long receivingDonationCenterId = paymentInfo.getColorWrapper().getCardId();
        final String receivingMerchantIdCode = paymentInfo.getColorWrapper().getMerchantIdCode();
        final User user = realm.where(User.class).findFirst();
        final String defaultAuthToken = user.getAuthToken();
        final String donateToDefaultDonationCenter = (defaultDonationCenterId.equals(receivingDonationCenterId) ? "1" : "0");
        amount1 = 0D;
        amount2 = 0D;
        amount3 = 0D;
        contributionType1 = null;
        contributionType2 = null;
        contributionType3 = null;
        String serviceName = null;
        Double totalAmount = 0D;
        int i = 1;
        if (0D != amount1 && amount1.equals(0D)) amount1 = null;
        if (0D != amount2 && amount2.equals(0D)) amount2 = null;
        if (0D != amount3 && amount3.equals(0D)) amount3 = null;
        List<ContributionType> types = realm.where(ContributionType.class).findAll();
        if (null != types)
            for (ContributionType contributionType : types) {
                totalAmount += contributionType.getAmount();
                DonationType donationType = realm.where(DonationType.class).equalTo("donationCenterCardId", paymentInfo.getColorWrapper().getCardId()).equalTo("description", contributionType.getDescription()).findFirst();
                if (i == 1) {
                    amount1 = contributionType.getAmount();
                    contributionType1 = donationType.getCode() + "|||" + donationType.getDescription();
                }
                if (i == 2) {
                    amount2 = contributionType.getAmount();
                    contributionType2 = donationType.getCode() + "|||" + donationType.getDescription();
                }
                if (i == 3) {
                    amount3 = contributionType.getAmount();
                    contributionType3 = donationType.getCode() + "|||" + donationType.getDescription();
                }
                i++;
            }

        Call<MakeDonationResponse> call = null;
        if (paymentMethod.equals("check")) {
            String accountNumber = selectedBankAccount.getAccountNumber();
            String routingNumber = selectedBankAccount.getRoutingNumber();
            call = makeDonationInterface.makeDonationWithChecking(defaultAuthToken,
                    defaultDonationCenterId,
                    defaultMerchantIdCode,
                    receivingDonationCenterId,
                    receivingMerchantIdCode,
                    donateToDefaultDonationCenter,
                    ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()),
                    config.getFirstName(),
                    config.getLastName(),
                    config.getEmail(),
                    (selectedBankAccount.getSaveCard() ? "1" : "0"),
                    amount1, amount2, amount3,
                    contributionType1, contributionType2, contributionType3,
                    routingNumber, accountNumber, (null == selectedBankAccount.getId() ? "ContributeWithANewCheckOrCheckingAcctByThisGiverAdvance" : "ContributeWithAnExistingCCByThisGiverAdvance"));

        } else if (null == selectedCreditCard.getId()) {
            String cardSave = ((null != selectedCreditCard.getSaveCard() && selectedCreditCard.getSaveCard().booleanValue()) ? "1" : "0");
            serviceName = "ContributeWithANewCCByThisGiverAdvance";
            call = makeDonationInterface.makeDonationWithNewCard(defaultAuthToken,
                    defaultDonationCenterId,
                    defaultMerchantIdCode,
                    receivingDonationCenterId,
                    receivingMerchantIdCode,
                    donateToDefaultDonationCenter,
                    ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()),
                    config.getFirstName(),
                    config.getLastName(),
                    config.getEmail(),
                    selectedCreditCard.getFirstName(),
                    selectedCreditCard.getLastName(),
                    selectedCreditCard.getCreditCardNumber(),
                    selectedCreditCard.getExpiration(),
                    selectedCreditCard.getCcv(),
                    cardSave,
                    amount1, amount2, amount3,
                    contributionType1, contributionType2, contributionType3,
                    serviceName);
        } else {
            String cardSourceUniversalAuthToken = selectedCreditCard.getAuthToken();
            String cardSourceDonationCenterId = (selectedCreditCard.getDonationCenterId()==null?"0":Integer.toString(selectedCreditCard.getDonationCenterId()));
            String cardSourceMerchantIdCode = selectedCreditCard.getMerchantIdCode();
            String cardSourceUniqueId = selectedCreditCard.getId()+"";
            serviceName = "ContributeWithAnExistingCCByThisGiverAdvance";
            call = makeDonationInterface.makeDonationWithExistingCard(defaultAuthToken,
                    defaultDonationCenterId,
                    defaultMerchantIdCode,
                    receivingDonationCenterId,
                    receivingMerchantIdCode,
                    selectedCreditCard.getId(),
                    (cardSourceDonationCenterId.equals(receivingDonationCenterId) ? "1" : "0"),
                    ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()),
                    config.getFirstName(),
                    config.getLastName(),
                    config.getEmail(),
                    amount1, amount2, amount3,
                    contributionType1, contributionType2, contributionType3,
                    cardSourceUniversalAuthToken,
                    cardSourceDonationCenterId,
                    cardSourceMerchantIdCode,
                    cardSourceUniqueId,
                    serviceName);
        }
        assert call != null;
        call.enqueue(new Callback<MakeDonationResponse>() {
            @Override
            public void onResponse(Call<MakeDonationResponse> call, Response<MakeDonationResponse> response) {
                Log.d(getClass().getSimpleName(), ">>>>>>>");
                progressBar.setVisibility(GONE);
                processingTextView.setText("");
                completedImageView.setVisibility(VISIBLE);

                Button buttonStart = view.findViewById(R.id.completed_start_button);
                buttonStart.setVisibility(VISIBLE);
                buttonStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        wizardCallbacks.onNext(GiveActivity.SUBMIT_PAGE, paymentInfo);
                    }
                });

                Button buttonCancel = view.findViewById(R.id.completed_cancel_button);
                buttonCancel.setVisibility(VISIBLE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        wizardCallbacks.onFinish(paymentInfo);
                    }
                });

                if (response.isSuccessful()) {
                    String message = null;
                    int alertType;
                    final MakeDonationResponse makeDonationResponse = response.body();
                    if (null != makeDonationResponse && null != makeDonationResponse.getResult() && makeDonationResponse.getResult().equalsIgnoreCase("SUCCESS")) {
                        message = makeDonationResponse.getInformation().getCardMessage();
                        alertType = SweetAlertDialog.SUCCESS_TYPE;
                        processingTextView.setText(message + "\n\nYour donation was processed successfully!");
                        completedImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
                        realm.beginTransaction();
                        realm.delete(ContributionType.class);
                        realm.commitTransaction();
                        downloadUser();
                        //updateUserInfo(Double.valueOf(makeDonationResponse.getInformation().getNewBalance().replace(",", "")));
                    } else if (null != makeDonationResponse && null != makeDonationResponse.getFaultString()) {
                        message = makeDonationResponse.getFaultCode() + ": " + makeDonationResponse.getFaultString();
                        alertType = SweetAlertDialog.ERROR_TYPE;
                        processingTextView.setText("Your payment was not successfully submitted: \n\n " + message);
                        completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                    } else {
                        alertType = SweetAlertDialog.ERROR_TYPE;
                        if (null != makeDonationResponse && null != makeDonationResponse.getInformation()) {
                            message = makeDonationResponse.getInformation().getCardMessage();
                        } else {
                            message = "Your payment was not completed successfully";
                        }
                        processingTextView.setText("Your payment was not successfully processed: \n\n " + message);
                        completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                    }
                } else {
                    try {
                        String json = response.errorBody().string();
                        BaseServiceResponse baseServiceResponse = ApiClient.getError(json);
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Payment was not submited successfully" + "\n" +
                                        "TransactionID : " + baseServiceResponse.getTransactionId());
                        dialog1.show();
                    } catch (Exception e) {
                        String message = "Your payment was not submitted successfully";
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText(message);
                        dialog1.show();
                    }
                    completedImageView.setImageResource(R.drawable.ic_error_black_24dp);

                }
            }

            @Override
            public void onFailure(Call<MakeDonationResponse> call, Throwable t) {
                progressBar.setVisibility(GONE);
                completedImageView.setVisibility(VISIBLE);
                completedImageView.setImageResource(R.drawable.ic_error_black_24dp);
                processingTextView.setText("Your payment was not submitted successfully \n\nNetwork failure");
                String message = "Your payment was not submitted successfully";
                SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText(message);
                dialog1.show();
            }
        });
    }

    private void downloadUser() {
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        if (null != donationCenter) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            MemberInfoRequest memberInfoRequest = new MemberInfoRequest();
            memberInfoRequest.setPhoneNumber(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
            memberInfoRequest.setPassword(config.getPassword());
            memberInfoRequest.setCenterCardId(donationCenter.getCardId());
            Call<UserLoginResponse> call = apiService.login(memberInfoRequest, getActivity().getResources().getString(R.string.org_filter),"", ApplicationUtils.APP_ID);
            call.enqueue(new Callback<UserLoginResponse>() {
                @Override
                public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                    if (response.isSuccessful()) {
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(response.body().getResponseData());
                        realm.commitTransaction();
                    } else {
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("7100 - Failure - Your profile cannot be downloaded");
                        dialog1.show();
                    }
                }

                @Override
                public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                    SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("7100 - Network Failure - Your profile cannot be downloaded");
                    dialog1.show();
                }
            });
        }
    }

    private void logPayment2(final String paymentId, String currency) {
        final Realm realm = Realm.getDefaultInstance();
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.completed_card_grogressbar);
        final TextView processingTextView = (TextView) view.findViewById(R.id.completed_card_processing_textview);
        final ImageView completedImageView = (ImageView) view.findViewById(R.id.completed_card_imageview);
        final LinearLayout completedButtonLayout = view.findViewById(R.id.completed_button_layout);
        try {
            processingTextView.setText("Payment not completed successfully!");
            progressBar.setVisibility(GONE);
            completedButtonLayout.setVisibility(VISIBLE);
            DonationCenter defaultDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            MakeDonationInterface makeDonationInterface = ApplicationUtils.getDonationInterface();
            if (null != makeDonationInterface) {
                double totalAmount = 0D;
                List<ContributionType> types = realm.where(ContributionType.class).findAll();
                int i = 1;
                double amount1 = 0D;
                double amount2 = 0D;
                double amount3 = 0D;
                String contributionType1 = null;
                String contributionType2 = null;
                String contributionType3 = null;
                if (null != types)
                    for (ContributionType contributionType : types) {
                        totalAmount += contributionType.getAmount();
                        DonationType donationType = realm.where(DonationType.class).equalTo("donationCenterCardId", paymentInfo.getColorWrapper().getCardId()).equalTo("description", contributionType.getDescription()).findFirst();
                        if (i == 1) {
                            amount1 = contributionType.getAmount();
                            contributionType1 = donationType.getCode() + "|||" + donationType.getDescription();
                        }
                        if (i == 2) {
                            amount2 = contributionType.getAmount();
                            contributionType2 = donationType.getCode() + "|||" + donationType.getDescription();
                        }
                        if (i == 3) {
                            amount3 = contributionType.getAmount();
                            contributionType3 = donationType.getCode() + "|||" + donationType.getDescription();
                        }
                        i++;
                    }
                User user = realm.where(User.class).findFirst();
                String donateToDefaultDonationCenter = (defaultDonationCenter.getCardId().equals(paymentInfo.getColorWrapper().getCardId()) ? "1" : "0");
                Call<ConfirmPaymentResponse> call = makeDonationInterface.confirmPayment(
                        user.getAuthToken(),
                        paymentId,
                        Double.toString(totalAmount),
                        paymentInfo.getPaymentEngine().get("type"),
                        paymentInfo.getPaymentEngine().get(currency),
                        defaultDonationCenter.getCardId(),
                        defaultDonationCenter.getMerchantIdCode(),
                        paymentInfo.getColorWrapper().getCardId(),
                        paymentInfo.getColorWrapper().getMerchantIdCode(),
                        0L,
                        donateToDefaultDonationCenter,
                        user.getMobilePhone(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        amount1,
                        amount2,
                        amount3,
                        contributionType1,
                        contributionType2,
                        contributionType3,
                        "LogInlineProcessedContributionsByThisGiver");
                call.enqueue(new Callback<ConfirmPaymentResponse>() {
                    @Override
                    public void onResponse(Call<ConfirmPaymentResponse> call, Response<ConfirmPaymentResponse> response) {
                        final NumberFormat formatter = NumberFormat.getCurrencyInstance();
                        if (response.isSuccessful()) {
                            final ConfirmPaymentResponse confirmPaymentResponse = response.body();
                            if (confirmPaymentResponse.getStatus().equalsIgnoreCase("2")) {
                                SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.app_name))
                                        .setContentText("Your contribution was completed but it is PENDING verification. Your payment ID is : " + paymentId);
                                dialog1.show();
                                completedImageView.setVisibility(VISIBLE);
                                completedButtonLayout.setVisibility(VISIBLE);
                            } else {
                                if (confirmPaymentResponse.getOtherInformation() != null) {
                                    processingTextView.setText("Completed!");
                                    completedImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
                                    completedImageView.setVisibility(VISIBLE);
                                    progressBar.setVisibility(GONE);
                                    SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getResources().getString(R.string.app_name))
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismissWithAnimation();
                                                    realm.beginTransaction();
                                                    realm.delete(ContributionType.class);
                                                    realm.commitTransaction();
                                                    //updateUserInfo(Double.valueOf(confirmPaymentResponse.getOtherInformation().getNewBalance().replace(",", "")));
                                                }
                                            })
                                            .setContentText("Your contribution of " + formatter.format(confirmPaymentResponse.getOtherInformation().getAmountContributed()) + " was completed successfully!");
                                    dialog1.show();
                                } else {
                                    SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.app_name))
                                            .setContentText("Your contribution was completed but it is PENDING verification. Your payment ID is : " + paymentId);
                                    dialog1.show();
                                    completedImageView.setVisibility(VISIBLE);
                                    completedButtonLayout.setVisibility(VISIBLE);
                                    //updateUserInfo(Double.valueOf(confirmPaymentResponse.getOtherInformation().getNewBalance()));
                                }
                            }
                        }else {
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("Your contribution was completed but it is PENDING verification. Your payment ID is : " + paymentId);
                            dialog1.show();
                            completedImageView.setVisibility(VISIBLE);
                            completedButtonLayout.setVisibility(VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ConfirmPaymentResponse> call, Throwable t) {
                        SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Your contribution was NOT completed successfully");
                        dialog1.show();
                        t.getMessage();
                        completedImageView.setVisibility(VISIBLE);
                        completedButtonLayout.setVisibility(VISIBLE);
                    }
                });

            }
        } catch (Exception e) {
            Log.i(getClass().getSimpleName(), e.getLocalizedMessage());
            processingTextView.setText("Payment completed successfully...But is being verified.\n\nYour Payment ID is: " + paymentId);
            progressBar.setVisibility(GONE);
            completedImageView.setVisibility(VISIBLE);
            completedButtonLayout.setVisibility(VISIBLE);
        }
    }
}

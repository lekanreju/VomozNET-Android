package com.vomozsystems.apps.android.vomoznet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.flutterwave.raveandroid.Utils;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class RaveActivity extends AppCompatActivity {

    Button startPayBtn;
    List<Meta> meta = new ArrayList<>();
    private TextView messageTextView;

    private String email;
    private Double totalAmount;
    private String publicKey;
    private String secretKey;
    private String transactionRef;
    private String narration;
    private String currency;
    private String country;
    private String firstName;
    private String lastName;
    private boolean isLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rave);
        Mint.initAndStartSession(this, "36cc1bd3");
        email = getIntent().getStringExtra(GiveActivity.EMAIL);
        totalAmount = getIntent().getDoubleExtra(GiveActivity.TOTAL_AMOUNT, 0D);
        publicKey = getIntent().getStringExtra(GiveActivity.PUBLIC_KEY);
        secretKey = getIntent().getStringExtra(GiveActivity.SECRET_KEY);
        currency = getIntent().getStringExtra(GiveActivity.AMOUNT_CURRENCY);
        country = getIntent().getStringExtra(GiveActivity.AMOUNT_COUNTRY);
        isLive = getIntent().getBooleanExtra(GiveActivity.IS_LIVE, false);
        transactionRef = getString(R.string.app_name).toUpperCase().replace(" ", "") + "-" + Calendar.getInstance().getTimeInMillis();
        narration = getIntent().getStringExtra(GiveActivity.PAYMENT_NARRATION);
        firstName = getIntent().getStringExtra(GiveActivity.FIRST_NAME);
        lastName = getIntent().getStringExtra(GiveActivity.LAST_NAME);
        messageTextView = findViewById(R.id.text_reference);
        startPayBtn = (Button) findViewById(R.id.startPaymentBtn);
        TextView amountTextView = findViewById(R.id.contribution_amount_textview);
        TextView descriptionTextView = findViewById(R.id.contribution_description_textview);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        amountTextView.setText(numberFormat.format(totalAmount) + " " + currency.toUpperCase());
        descriptionTextView.setText(getString(R.string.app_name) + " Donation");

        meta.add(new Meta("test key 1", "test value 1"));
        meta.add(new Meta("test key 2", "test value 2"));

        startPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEntries();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void validateEntries() {
        clearErrors();

        boolean valid = true;

        if (null != totalAmount && totalAmount > 0) {

        }else {
            messageTextView.setError("A valid email is required");
        }

        //check for compulsory fields
        if (!Utils.isEmailValid(email)) {
            valid = false;
            messageTextView.setError("A valid email is required");
        }

        if (publicKey.length() < 1){
            valid = false;
            messageTextView.setError("A valid public key is required");
        }

        if (secretKey.length() < 1){
            valid = false;
            messageTextView.setError("A valid secret key is required");
        }

        if (transactionRef.length() < 1){
            valid = false;
            messageTextView.setError("A valid txRef key is required");
        }

        if (currency.length() < 1){
            valid = false;
            messageTextView.setError("A valid currency code is required");
        }

        if (country.length() < 1){
            valid = false;
            messageTextView.setError("A valid country code is required");
        }

        if (valid) {
            new RavePayManager(this).setAmount(totalAmount)
                    .setCountry(country)
                    .setCurrency(currency)
                    .setEmail(email)
                    .setfName(firstName)
                    .setlName(lastName)
                    .setNarration(narration)
                    .setPublicKey(publicKey)
                    .setSecretKey(secretKey)
                    .setTxRef(transactionRef)
                    .acceptAccountPayments(true)
                    .acceptCardPayments(true)
                    .onStagingEnv(!isLive)
                    .setMeta(meta)
//                    .withTheme(R.style.TestNewTheme)
                    .initialize();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (message != null) {
                Log.d("rave response", message);
            }
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                try {
                    JSONObject jsonObject = new JSONObject(message);
                    jsonObject = jsonObject.getJSONObject("data");
                    String transRef = jsonObject.getString("flw_ref");
                    Toast.makeText(this, "SUCCESS - Your contribution was processed successfully!" , Toast.LENGTH_SHORT).show();
                    Intent mdata = new Intent();
                    mdata.putExtra(GiveActivity.PAYMENT_ID, transRef);
                    setResult(RESULT_OK, mdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void clearErrors() {

    }

}

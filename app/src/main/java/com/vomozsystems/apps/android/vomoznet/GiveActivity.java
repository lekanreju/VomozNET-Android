package com.vomozsystems.apps.android.vomoznet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.splunk.mint.Mint;
import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;
import com.vomozsystems.apps.android.vomoznet.fragment.BaseSearchFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.ChooseReceiverFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.ConfirmAndSubmitFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.ContributionAmountFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.HowToGiveFragment;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link HomeActivity}.
 */
public class GiveActivity extends AppCompatActivity implements BaseSearchFragment.BaseExampleFragmentCallbacks, WizardCallbacks{

    public static final String RECEIVER_PAGE = "RECEIVER";
    public static final String PAYMENT_ENGINE_PAGE = "PAYMENT_ENGINE";
    public static final String AMOUNTS_PAGE = "AMOUNTS_PAGE";
    public static final String SUBMIT_PAGE = "SUBMIT_PAGE";

    public static final Integer PAYPAL_REQUEST_CODE = 10000;
    public static final Integer PAYSTACK_REQUEST_CODE = 2000;
    public static final Integer RAVE_REQUEST_CODE = 3000;
    public static final String TOTAL_AMOUNT = "total_amount";
    public static final String TOTAL_AMOUNT_DESCRIPTION = "total_amount_description";
    public static final String AMOUNT_CURRENCY = "currency";
    public static final String PUBLIC_KEY = "public_key";
    public static final String EMAIL = "email";
    public static final String SECRET_KEY = "secret_key";
    public static final String AMOUNT_COUNTRY = "amount_country";
    public static final String PAYMENT_NARRATION = "payment_narration";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String IS_LIVE = "is_live";
    public static final String PAYMENT_ID = "payment_id";

    private HowToGiveFragment howToGiveFragment;
    private ContributionAmountFragment contributionAmountFragment;
    private ConfirmAndSubmitFragment confirmAndSubmitFragment;
    private ChooseReceiverFragment chooseReceiverFragment;

    private PaymentInfo paymentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give);
        Mint.initAndStartSession(this, "36cc1bd3");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        howToGiveFragment = HowToGiveFragment.newInstance(1);
        contributionAmountFragment = ContributionAmountFragment.newInstance();
        confirmAndSubmitFragment = ConfirmAndSubmitFragment.newInstance();
        chooseReceiverFragment = ChooseReceiverFragment.newInstance(new PaymentInfo());

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ChooseReceiverFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ChooseReceiverFragment.ARG_ITEM_ID));
            PaymentInfo paymentInfo = new PaymentInfo();
            ChooseReceiverFragment fragment = ChooseReceiverFragment.newInstance(paymentInfo);
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNext(String currentPageName, PaymentInfo paymentInfo) {
        if(currentPageName.equalsIgnoreCase(RECEIVER_PAGE)) {
            howToGiveFragment.setPaymentInfo(paymentInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, howToGiveFragment)
                    .commit();
        }else if(currentPageName.equalsIgnoreCase(PAYMENT_ENGINE_PAGE)) {
            contributionAmountFragment.setPaymentInfo(paymentInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, contributionAmountFragment)
                    .commit();
        }else if(currentPageName.equalsIgnoreCase(AMOUNTS_PAGE)) {
            confirmAndSubmitFragment.setPaymentInfo(paymentInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, confirmAndSubmitFragment)
                    .commit();
        }else if(currentPageName.equalsIgnoreCase(SUBMIT_PAGE)) {

        }
    }

    @Override
    public void onPrevious(String currentPageName, PaymentInfo paymentInfo) {
        if(currentPageName.equalsIgnoreCase(PAYMENT_ENGINE_PAGE)) {
            chooseReceiverFragment.setPaymentInfo(paymentInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, chooseReceiverFragment)
                    .commit();
        }else if(currentPageName.equalsIgnoreCase(AMOUNTS_PAGE)) {
            howToGiveFragment.setPaymentInfo(paymentInfo);
            getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, howToGiveFragment)
                    .commit();
        }
    }

    @Override
    public void onCancel(PaymentInfo paymentInfo) {
        finish();
    }

    @Override
    public void onFinish(PaymentInfo paymentInfo) {
        finish();
    }

    @Override
    public void onStartOver(PaymentInfo paymentInfo) {
        this.paymentInfo = paymentInfo;
        howToGiveFragment = HowToGiveFragment.newInstance(1);
        contributionAmountFragment = ContributionAmountFragment.newInstance();
        confirmAndSubmitFragment = ConfirmAndSubmitFragment.newInstance();
        chooseReceiverFragment = ChooseReceiverFragment.newInstance(new PaymentInfo());
        getSupportFragmentManager().beginTransaction().replace(R.id.item_detail_container, chooseReceiverFragment)
                .commit();
    }

    @Override
    public void setTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}

package com.vomozsystems.apps.android.vomoznet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.splunk.mint.Mint;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationHistory;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.fragment.ChooseDefaultDonationCenterDialogFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.DonationHistoryFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.HomeFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.MyProfileFragment;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.service.MemberInfoRequest;
import com.vomozsystems.apps.android.vomoznet.service.RegistrationRequest;
import com.vomozsystems.apps.android.vomoznet.service.SignUpResponse;
import com.vomozsystems.apps.android.vomoznet.service.UserLoginResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.exit;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener, DonationHistoryFragment.OnDonationHistoryListFragmentInteractionListener, MyProfileFragment.OnMyProfileFragmentInteractionListener{


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    //private SectionsPagerAdapter mSectionsPagerAdapter;
    private int selectedOption = 0;
    private HomeFragment homeFragment;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        Mint.initAndStartSession(MainActivity.this, "36cc1bd3");
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        //mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
//        mViewPager = (ViewPager) findViewById(R.id.container);
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//        homeFragment = HomeFragment.newInstance();
//        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
//
//        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    @Override
    public void onResume() {
        super.onResume();
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        if(null != config && null != config.getLoggedIn() && config.getLoggedIn()) {
            sendCloudRegistrationToken();
            int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

                DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
                if(donationCenter == null) {
                        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                        Call<DonationCenterResponse> call2 = apiInterface.getAll(getResources().getString(R.string.org_filter));
                        call2.enqueue(new Callback<DonationCenterResponse>() {
                            @Override
                            public void onResponse(Call<DonationCenterResponse> call, final Response<DonationCenterResponse> response) {
                                if (response.isSuccessful()) {
                                    realm.beginTransaction();
                                    realm.delete(DonationCenter.class);
                                    realm.copyToRealmOrUpdate(response.body().getResponseData());
                                    realm.commitTransaction();
                                    setHomeDonationCenter();
                                    sendCloudRegistrationToken();
                                }
                            }

                            @Override
                            public void onFailure(Call<DonationCenterResponse> call, Throwable t) {

                            }
                        });

                }

            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onHomeFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        int currentItem = mViewPager.getCurrentItem();
        if(currentItem > 0) {
            mViewPager.setCurrentItem(currentItem - 1);
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onMyProfileFragmentInteraction(Uri uri) {
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onDonationHistoryListFragmentInteraction(DonationHistory item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_signoff) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText("Do you want to sign out?")
                    .setConfirmText("Sign Out")
                    .setCancelText("No")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.cancel();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Realm realm = Realm.getDefaultInstance();
                            Config config = realm.where(Config.class).findFirst();
                            realm.beginTransaction();
                            config.setLoggedIn(false);
                            realm.commitTransaction();
                            sDialog.dismissWithAnimation();
                            exit(0);
                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }
        
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return homeFragment;
                case 1:
                    return MyProfileFragment.newInstance("","");
                case 2:
                    return DonationHistoryFragment.newInstance();

            }
            return new Fragment();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Home";
                case 1:
                    return "My Profile";
                case 2:
                    return "History";
            }
            return null;
        }
    }

    private void setHomeDonationCenter() {
        Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        final List<DonationCenter> donationCenterList = realm.copyFromRealm(realm.where(DonationCenter.class).findAll());
        if(null != donationCenterList && donationCenterList.size()>0) {
            DonationCenter homeDonationCenter = null;
            for(DonationCenter donationCenter1: donationCenterList) {
                if(donationCenter1.getCardId().equals(config.getCurrentDonationCenterCardId())) {
                    homeDonationCenter = donationCenter1;
                    break;
                }
            }
            if(homeDonationCenter!=null) {
                realm.beginTransaction();
                homeDonationCenter.setHomeDonationCenter(true);
                realm.copyToRealmOrUpdate(homeDonationCenter);
                realm.commitTransaction();
                downLoadUser(homeDonationCenter);
            }
            else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                // Set the dialog title
//                final String[] items = new String[donationCenterList.size()];
//                for (int i = 0; i < donationCenterList.size(); i++) {
//                    items[i] = donationCenterList.get(i).getName();
//                }
//                builder.setTitle("Choose Default Donation Center")
//                        // Specify the list array, the items to be selected by default (null for none),
//                        // and the listener through which to receive callbacks when items are selected
//                        .setSingleChoiceItems(items, selectedOption, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                selectedOption = which;
//                            }
//                        })
//                        // Set the action buttons
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//                                DonationCenter donationCenter = donationCenterList.get(selectedOption);
//                                signUpToADonationCenter(donationCenter);
//
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int id) {
//
//                            }
//                        });
//                AlertDialog dialog = builder.create();
//                dialog.show();

                ChooseDefaultDonationCenterDialogFragment fragment = ChooseDefaultDonationCenterDialogFragment.newInstance(donationCenterList, this);
                fragment.show(getSupportFragmentManager(), "");
            }
        }else {
            SweetAlertDialog dialog1 = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText("Network Failure\n\n" + getResources().getString(R.string.org_type) + " configuration problem");
            dialog1.show();
        }
    }

    public void signUpToADonationCenter(final DonationCenter donationCenter) {
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        MakeDonationInterface makeDonationInterface = ApplicationUtils.getDonationInterface();
        if (null != makeDonationInterface) {
            Call<SignUpResponse> call1 = makeDonationInterface.signUpMemberToDonationCenter(donationCenter.getCardId(),
                    donationCenter.getMerchantIdCode(),
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
                            downLoadUser(donationCenter);
                        } else {
                            SweetAlertDialog dialog1 = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("Failure:\n" + signUpResponse.getFaultCode() + ": " + signUpResponse.getFaultString() + "\n\nCannot connect to " + donationCenter.getName());
                            dialog1.show();
                        }
                    } else {
                        SweetAlertDialog dialog1 = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("7200 - Cannot connect to " + donationCenter.getName());
                        dialog1.show();
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponse> call, Throwable t) {
                    SweetAlertDialog dialog1 = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("7100 - Network Failure\n\nCannot connect to " + donationCenter.getName());
                    dialog1.show();
                }
            });
        }
    }

    private void downLoadUser(final DonationCenter donationCenter) {
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        MemberInfoRequest memberInfoRequest = new MemberInfoRequest();
        memberInfoRequest.setPhoneNumber(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
        memberInfoRequest.setPassword(config.getPassword());
        memberInfoRequest.setCenterCardId(donationCenter.getCardId());
        Call<UserLoginResponse> call = apiService.login(memberInfoRequest, getResources().getString(R.string.org_filter),"", ApplicationUtils.APP_ID);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (response.isSuccessful() && null != response.body().getResponseData()) {
                    realm.beginTransaction();
                    realm.delete(User.class);
                    User user = response.body().getResponseData();
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();
                    realm.beginTransaction();
                    List<DonationCenter> centers = realm.where(DonationCenter.class).findAll();
                    for(DonationCenter donationCenter1: centers) {
                        if(!donationCenter1.getCardId().equals(donationCenter.getCardId())) {
                            donationCenter1.setHomeDonationCenter(false);
                        }else {
                            donationCenter1.setHomeDonationCenter(true);
                        }
                    }
                    realm.copyToRealmOrUpdate(centers);
                    realm.commitTransaction();
                    DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
                    if(donationCenter != null) {
                        homeFragment.onResume();
                    }
                } else {
                    SweetAlertDialog dialog1 = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("7400 - Failure. \n\nYour Vomoz.NET profile cannot be retrieved");
                    dialog1.show();
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                SweetAlertDialog dialog1 = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("7500 - Network Failure. \n\nYour Vomoz.NET profile cannot be retrieved.");
                dialog1.show();
            }
        });
    }

    private void sendCloudRegistrationToken() {
        boolean saved = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(VomozPayFirebaseInstanceIDService.REGISTRATION_TOKEN_SAVED, false);
        if (!saved) {
            Realm realm = Realm.getDefaultInstance();
            Config config = realm.where(Config.class).findFirst();
            RegistrationRequest request = new RegistrationRequest();
            request.setCallerId(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
            request.setAppOs("android");
            String token = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString(VomozPayFirebaseInstanceIDService.REGISTRATION_TOKEN, null);

            request.setRegistrationToken(token);
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<BaseServiceResponse> call1 = apiInterface.sendCloudMessagingRegistrationForGlobal(request, "", ApplicationUtils.APP_ID);
            call1.enqueue(new Callback<BaseServiceResponse>() {
                @Override
                public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                    Log.d(getClass().getSimpleName(), "Registration was successful...");
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean(VomozPayFirebaseInstanceIDService.REGISTRATION_TOKEN_SAVED, true).apply();
                }

                @Override
                public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                    Log.d(getClass().getSimpleName(), "Registration FAILED...");
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean(VomozPayFirebaseInstanceIDService.REGISTRATION_TOKEN_SAVED, false).apply();
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putString(VomozPayFirebaseInstanceIDService.REGISTRATION_TOKEN, null).apply();
                }
            });
        }
    }
}

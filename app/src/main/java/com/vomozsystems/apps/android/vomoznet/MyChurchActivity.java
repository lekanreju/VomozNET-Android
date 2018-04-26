package com.vomozsystems.apps.android.vomoznet;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.firebase.messaging.FirebaseMessaging;
import com.splunk.mint.Mint;
import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.adapter.DonationTopicDataAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenterTopic;
import com.vomozsystems.apps.android.vomoznet.entity.Event;
import com.vomozsystems.apps.android.vomoznet.entity.Item;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.fragment.AttendanceFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.ChangeDonationCenterDialogFrament;
import com.vomozsystems.apps.android.vomoznet.fragment.DirectoryFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.EventFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.ItemFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.VideoFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.dummy.DummyContent;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationCenterTopicResponse;
import com.vomozsystems.apps.android.vomoznet.youtube.DeveloperKey;
import com.vomozsystems.apps.android.vomoznet.youtube.VideoEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.exit;

public class MyChurchActivity extends AppCompatActivity implements VideoFragment.OnVideoListFragmentInteractionListener, DirectoryFragment.OnListFragmentInteractionListener, AttendanceFragment.OnAttendanceListFragmentInteractionListener, EventFragment.OnEventListFragmentInteractionListener, ItemFragment.OnItemFragmentInteractionListener {

    private static final int REQ_START_STANDALONE_PLAYER = 1;
    private static final int REQ_RESOLVE_SERVICE_MISSING = 2;

    private static final String VIDEO_ID = "cdgQpa1pUUE";
    private static final String PLAYLIST_ID =  "7E952A67F31C58A3";
    private static final ArrayList<String> VIDEO_IDS = new ArrayList<String>(Arrays.asList(
            new String[] {"cdgQpa1pUUE", "8aCYZ3gXfy8", "zMabEyrtPRg"}));

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 3500;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private EventFragment eventFragment;
    private DirectoryFragment directoryFragment;
    private AttendanceFragment attendanceFragment;
    private ItemFragment mediaItemFragment, directoryItemFragment, formItemFragment;
    private VideoFragment videoFragment;
    
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private DonationTopicDataAdapter donationTopicDataAdapter;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.app_name) + " - Location Permission")
                        .setMessage("The Attendance module of the app needs your permission to access device location. Without this permission, the Attendance will not be disabled.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MyChurchActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_REQUEST_COARSE_LOCATION: {
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    Log.d(getClass().getSimpleName(), "coarse location permission granted");
//                } else {
//                    Intent intent = new Intent();
//                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    Uri uri = Uri.fromParts("package", getPackageName(), null);
//                    intent.setData(uri);
//                    startActivity(intent);
//                }
//            }
//        }
//    }

    public void refreshView() {
        ImageView imageView = findViewById(R.id.organization_image);
        TextView organizationNameTextView = findViewById(R.id.organization_name);
        TextView organizationAddressTextView = findViewById(R.id.organization_address);
        TextView organizationTelephoneTextView = findViewById(R.id.organization_telephone);
        Button changeOrganzationButton = findViewById(R.id.change_organization_button);
        String changeOrg = getResources().getString(R.string.change_center);
        if(null != changeOrg && changeOrg.equalsIgnoreCase("yes")) {
            changeOrganzationButton.setVisibility(View.VISIBLE);
        }
        else {
            changeOrganzationButton.setVisibility(View.GONE);
        }

        changeOrganzationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeDonationCenterDialogFrament frament = ChangeDonationCenterDialogFrament.newInstance(MyChurchActivity.this);
                frament.show(getSupportFragmentManager(), "");
            }
        });
        Button notificationSettingButton = (Button) findViewById(R.id.subscribe_button);
        notificationSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subscribe();
            }
        });
        Realm realm = Realm.getDefaultInstance();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        if (null != donationCenter && null != donationCenter.getName()) {
            String name = donationCenter.getName();
            if (donationCenter.getName().length() > 20)
                name = donationCenter.getShortName();
            organizationNameTextView.setText(name);
            String donationCenterLogoUrl = ApiClient.BASE_IMAGE_URL + "/" + donationCenter.getLogoName();
            if (null != donationCenterLogoUrl && !donationCenter.getLogoName().equalsIgnoreCase("0")) {
                Picasso.with(this)
                        .load(donationCenterLogoUrl)
                        .into(imageView);
            }

            organizationTelephoneTextView.setText(donationCenter.getTelephoneNumber());
            organizationAddressTextView.setText(donationCenter.getAddress());

            eventFragment.refreshView();
            attendanceFragment.refreshView();
            mediaItemFragment.refreshView();
            formItemFragment.refreshView();
            directoryFragment.refreshView();
            videoFragment.refreshView();

        }else {
            new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText("You are not connected to any organization/church yet. You will be connected to this organization once a donation/contribution is made ")
                    .setConfirmText("OK")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    }).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_START_STANDALONE_PLAYER && resultCode != RESULT_OK) {
            YouTubeInitializationResult errorReason =
                    YouTubeStandalonePlayer.getReturnedInitializationResult(data);
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(this, 0).show();
            } else {
                String errorMessage =
                        String.format(getString(R.string.error_player), errorReason.toString());
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean canResolveIntent(Intent intent) {
        List<ResolveInfo> resolveInfo = getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private int parseInt(String text, int defaultValue) {
        if (!TextUtils.isEmpty(text)) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                // fall through
            }
        }
        return defaultValue;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshView();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("Location Permission");
//                builder.setMessage(getResources().getString(R.string.app_name) + " needs location permissions to activate the Attendance module. \n\nClick \"OK\", then \"Permissions\" and then switch on \"Location\" ");
//                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//                        }
//                    }
//                });
//                builder.setNegativeButton(android.R.string.no, null);
//                builder.show();
//            }
//
//        }

        checkLocationPermission();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_church);
        Mint.initAndStartSession(this, "36cc1bd3");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        autoSubscribe();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_church, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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

    @Override
    public void onEventListFragmentInteraction(Event event) {

    }

    @Override
    public void onItemFragmentInteraction(Item item) {

    }

    @Override
    public void onAttendanceListFragmentInteraction(Event item) {

    }

    @Override
    public void onListFragmentInteraction(Personal item) {

    }

    @Override
    public void onVideoListFragmentInteraction(VideoEntry item) {
        boolean autoplay = false;
        boolean lightboxMode = true;

        Intent intent = YouTubeStandalonePlayer.createVideoIntent(
                    this, DeveloperKey.DEVELOPER_KEY, VIDEO_ID, 1000, autoplay, lightboxMode);

        if (intent != null) {
            if (canResolveIntent(intent)) {
                startActivityForResult(intent, REQ_START_STANDALONE_PLAYER);
            } else {
                // Could not resolve the intent - must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(this, REQ_RESOLVE_SERVICE_MISSING).show();
            }
        }
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
            View rootView = inflater.inflate(R.layout.fragment_my_church, container, false);
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
        int screenSize;
        int numberOfColumns = 3;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            screenSize = getScreenWidth(MyChurchActivity.this);
            switch (screenSize) {
                case Configuration.SCREENLAYOUT_SIZE_LARGE:
                    numberOfColumns = 4;
                    break;
                case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    numberOfColumns = 3;
                    break;
                case Configuration.SCREENLAYOUT_SIZE_SMALL:
                    numberOfColumns = 2;
                    break;
                default:
            }

            directoryFragment = DirectoryFragment.newInstance(2);
            eventFragment = EventFragment.newInstance(1);
            attendanceFragment = AttendanceFragment.newInstance();
            videoFragment = VideoFragment.newInstance(numberOfColumns);
            mediaItemFragment = ItemFragment.newInstance(numberOfColumns, "MEDIA");
            formItemFragment = ItemFragment.newInstance(numberOfColumns, "FORM");
        }
//FORM, MEDIA, LINK, DIRECTORY_SERVICE
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return eventFragment;
                case 1:
                    return attendanceFragment;
                case 2:
                    return videoFragment;
                case 3:
                    return formItemFragment;
                case 4 :
                    return  directoryFragment;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 5;
        }
    }

    public static int getScreenWidth(Context context) {
        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize;
    }

    protected void subscribe() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.topic_subscription_layout);
        dialog.setTitle("Subscribe");
        final RecyclerView mRecyclerView = (RecyclerView) dialog.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Realm realm = Realm.getDefaultInstance();
        final User user = realm.where(User.class).findFirst();
        if(user != null) {
            DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            if (donationCenter != null) {
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<GetDonationCenterTopicResponse> call = apiService.getDonationCenterTopicsForUser(donationCenter.getCardId(), Long.valueOf(user.getTexterCardId()));

                call.enqueue(new Callback<GetDonationCenterTopicResponse>() {
                    @Override
                    public void onResponse(Call<GetDonationCenterTopicResponse> call, Response<GetDonationCenterTopicResponse> response) {
                        if (response.isSuccessful()) {
                            List<DonationCenterTopic> topics = response.body().getResponseData();
                            if (null != topics && topics.size() > 0) {
                                Map<Long, Boolean> map = new HashMap<Long, Boolean>();
                                List<DonationCenterTopic> list = realm.where(DonationCenterTopic.class).findAll();
                                if (null != list && list.size() > 0) {
                                    for (DonationCenterTopic topic : list) {
                                        map.put(topic.getId(), topic.getSelected());
                                    }
                                }
                                realm.beginTransaction();

                                if (null != topics && topics.size() > 0) {
                                    for (DonationCenterTopic topic : topics) {
                                        topic.setTopicName(topic.getTopicName().replace(" ", "_"));
                                        Boolean value = map.get(topic.getId());
                                        if (null == value) {
                                            FirebaseMessaging.getInstance().subscribeToTopic(topic.getTopicName());
                                            topic.setSelected(true);
                                        } else
                                            topic.setSelected(value);
                                    }
                                }
                                realm.copyToRealmOrUpdate(topics);
                                realm.commitTransaction();
                                donationTopicDataAdapter = new DonationTopicDataAdapter(topics, realm);
                                mRecyclerView.setAdapter(donationTopicDataAdapter);
                            } else {
                                dialog.dismiss();
                                new SweetAlertDialog(MyChurchActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(getResources().getString(R.string.app_name))
                                        .setContentText("No groups found")
                                        .show();

                            }
                        } else {
                            dialog.dismiss();
                            new SweetAlertDialog(MyChurchActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("No groups found")
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDonationCenterTopicResponse> call, Throwable t) {
                        new SweetAlertDialog(MyChurchActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Request not completed successfully")
                                .show();
                        dialog.dismiss();
                    }
                });
                // create an Object for Adapter

                Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                Button btnSelection = (Button) dialog.findViewById(R.id.btnShow);
                btnSelection.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (null != donationTopicDataAdapter) {
                            String data = "";
                            List<DonationCenterTopic> stList = donationTopicDataAdapter.getTopics();

                            for (int i = 0; i < stList.size(); i++) {
                                DonationCenterTopic topic = stList.get(i);
                                if (topic.getSelected() == true) {
                                    data = data + "\n" + topic.getTopicName().toString();
                                    try {
                                        FirebaseMessaging.getInstance().subscribeToTopic(topic.getTopicName());
                                    } catch (Exception e) {

                                    }
                                } else {
                                    try {
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic.getTopicName());
                                    } catch (Exception e) {

                                    }
                                }
                            }
                            new SweetAlertDialog(MyChurchActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("Subscribed to groups: " + "\n" + data)
                                    .show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        }
        dialog.show();
    }

    private void autoSubscribe() {
        final Realm realm = Realm.getDefaultInstance();
        final User user = realm.where(User.class).findFirst();
        if(user != null) {
            DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            if (donationCenter != null) {
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call<GetDonationCenterTopicResponse> call = apiService.getDonationCenterTopicsForUser(donationCenter.getCardId(), Long.valueOf(user.getTexterCardId()));

                call.enqueue(new Callback<GetDonationCenterTopicResponse>() {
                    @Override
                    public void onResponse(Call<GetDonationCenterTopicResponse> call, Response<GetDonationCenterTopicResponse> response) {
                        if (response.isSuccessful()) {
                            List<DonationCenterTopic> topics = response.body().getResponseData();
                            if (null != topics && topics.size() > 0) {
                                Map<Long, Boolean> map = new HashMap<Long, Boolean>();
                                List<DonationCenterTopic> list = realm.where(DonationCenterTopic.class).findAll();
                                if (null != list && list.size() > 0) {
                                    for (DonationCenterTopic topic : list) {
                                        map.put(topic.getId(), topic.getSelected());
                                    }
                                }
                                realm.beginTransaction();

                                if (null != topics && topics.size() > 0) {
                                    for (DonationCenterTopic topic : topics) {
                                        topic.setTopicName(topic.getTopicName().replace(" ", "_"));
                                        Boolean value = map.get(topic.getId());
                                        if (null == value) {
                                            FirebaseMessaging.getInstance().subscribeToTopic(topic.getTopicName());
                                            topic.setSelected(true);
                                        } else
                                            topic.setSelected(value);
                                    }
                                }
                                realm.copyToRealmOrUpdate(topics);
                                realm.commitTransaction();

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDonationCenterTopicResponse> call, Throwable t) {

                    }
                });
                // create an Object for Adapter
            }
        }
    }
}

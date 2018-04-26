package com.vomozsystems.apps.android.vomoznet;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;

import com.splunk.mint.Mint;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationHistory;
import com.vomozsystems.apps.android.vomoznet.fragment.DonationHistoryFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.HomeFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.MyProfileFragment;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link GiveActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class HomeActivity extends AppCompatActivity implements HomeFragment.OnHomeFragmentInteractionListener, DonationHistoryFragment.OnDonationHistoryListFragmentInteractionListener, MyProfileFragment.OnMyProfileFragmentInteractionListener{

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private int numberOfColumns = 0;
    private boolean mTwoPane;
    private LinearLayout homeLayout;
    private LinearLayout historyLayout;
    private LinearLayout profileLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Mint.initAndStartSession(this, "36cc1bd3");
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

//        View recyclerView = findViewById(R.id.item_list);
//        assert recyclerView != null;
//        setupRecyclerView((RecyclerView) recyclerView);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        if(null != config && null != config.getLoggedIn() && config.getLoggedIn()) {

//            showUserDetails();
            getUserDonationCenters();
        }else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        int screenSize = getScreenWidth(this);
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
    }

    @Override
    public void onHomeFragmentInteraction(Uri uri) {

    }

    @Override
    public void onMyProfileFragmentInteraction(Uri uri) {
        Log.i(getClass().getSimpleName(), "");
    }

    @Override
    public void onDonationHistoryListFragmentInteraction(DonationHistory item) {

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
                    return HomeFragment.newInstance();
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
    }

    private void getUserDonationCenters() {
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DonationCenterResponse> call = apiService.getDonationCenters(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()), getResources().getString(R.string.org_filter),"", "");
        call.enqueue(new Callback<DonationCenterResponse>() {
            @Override
            public void onResponse(Call<DonationCenterResponse> call, Response<DonationCenterResponse> response) {
                if(response.isSuccessful() && null != response.body() & null != response.body().getResponseData()) {
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(response.body().getResponseData());
                    realm.commitTransaction();
                }
            }

            @Override
            public void onFailure(Call<DonationCenterResponse> call, Throwable t) {
                Log.i("", getClass().getSimpleName());
            }
        });

    }

    public static int getScreenWidth(Context context) {
        int screenSize = context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        return screenSize;
    }
}

package com.vomozsystems.apps.android.vomoznet.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.GiveActivity;
import com.vomozsystems.apps.android.vomoznet.MainActivity;
import com.vomozsystems.apps.android.vomoznet.MyChurchActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.AmountContributed;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Slider;
import com.vomozsystems.apps.android.vomoznet.entity.TotalDonation;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterSlidersResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetAllBalanceResponse;
import com.vomozsystems.apps.android.vomoznet.service.MemberInfoRequest;
import com.vomozsystems.apps.android.vomoznet.service.ServiceGenerator;
import com.vomozsystems.apps.android.vomoznet.service.UserLoginResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;
import com.vomozsystems.apps.android.vomoznet.utility.FilePath;
import com.vomozsystems.apps.android.vomoznet.utility.ImageUtils;
import com.vomozsystems.apps.android.vomoznet.utility.Utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnHomeFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int TIMEOUT_VALUE = 150000;
    private CircularImageView imgPerson;
    private SliderLayout mDemoSlider;
    protected DonationCenter donationCenter;
    private OnHomeFragmentInteractionListener mListener;
    private RecyclerView recyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        final Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        Config config = realm.where(Config.class).findFirst();

        TextView contributionsLabelTextView = view.findViewById(R.id.contributions_label_textview);
        final TextView contributionsLabelTextViewMessage = view.findViewById(R.id.contributions_label_textview_message);
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
//        contributionsLabelTextView.setText("Total " + year + " Contributions");
//        contributionsLabelTextViewMessage.setText("No contributions found for " + year);

        showUserDetails(view);

        if(null != user && null != user.getTexterCardId()) {
            loadPersonImage(user);

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            String phoneNumber = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone());
            Call<GetAllBalanceResponse> call = apiInterface.getUserAllBalancesForOrgFilter(phoneNumber, getResources().getString(R.string.org_filter), "", ApplicationUtils.APP_ID);
            call.enqueue(new Callback<GetAllBalanceResponse>() {
                @Override
                public void onResponse(Call<GetAllBalanceResponse> call, Response<GetAllBalanceResponse> response) {
                    if (response.isSuccessful() && null != response.body()) {
                        AmountContributed amountContributed = response.body().getResponseData();
                        List<TotalDonation> totalDonations = new ArrayList<TotalDonation>();
                        if (null != amountContributed && amountContributed.getAmountInCedi() > 0) {
                            TotalDonation totalDonation = new TotalDonation();
                            totalDonation.setCurrency("GHC");
                            totalDonation.setTotalAmount(amountContributed.getAmountInCedi());
                            totalDonations.add(totalDonation);
                            contributionsLabelTextViewMessage.setVisibility(GONE);
                        }
                        if (null != amountContributed && amountContributed.getAmountInDollars() > 0) {
                            TotalDonation totalDonation = new TotalDonation();
                            totalDonation.setCurrency("USD");
                            totalDonation.setTotalAmount(amountContributed.getAmountInDollars());
                            totalDonations.add(totalDonation);
                            contributionsLabelTextViewMessage.setVisibility(GONE);
                        }
                        if (null != amountContributed && amountContributed.getAmountInEuro() > 0) {
                            TotalDonation totalDonation = new TotalDonation();
                            totalDonation.setCurrency("EUR");
                            totalDonation.setTotalAmount(amountContributed.getAmountInEuro());
                            totalDonations.add(totalDonation);
                            contributionsLabelTextViewMessage.setVisibility(GONE);
                        }
                        if (null != amountContributed && amountContributed.getAmountInNaira() > 0) {
                            TotalDonation totalDonation = new TotalDonation();
                            totalDonation.setCurrency("NGN");
                            totalDonation.setTotalAmount(amountContributed.getAmountInNaira());
                            totalDonations.add(totalDonation);
                            contributionsLabelTextViewMessage.setVisibility(GONE);
                        }
                        if (null != amountContributed && amountContributed.getAmountInPounds() > 0) {
                            TotalDonation totalDonation = new TotalDonation();
                            totalDonation.setCurrency("GBP");
                            totalDonation.setTotalAmount(amountContributed.getAmountInPounds());
                            totalDonations.add(totalDonation);
                            contributionsLabelTextViewMessage.setVisibility(GONE);
                        }
//                        setupRecyclerView(recyclerView, totalDonations);
                    }else {
                        //contributionsLabelTextViewMessage.setText("No contributions found");
                    }
                }

                @Override
                public void onFailure(Call<GetAllBalanceResponse> call, Throwable t) {

                }
            });
        }

        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        Long cardId = 0L;
        String merchantIdCode = "";
        if(donationCenter != null) {
            cardId = donationCenter.getCardId();
            merchantIdCode = donationCenter.getMerchantIdCode();
        }
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DonationCenterSlidersResponse> call = apiInterface.getSlidersNew(cardId, merchantIdCode, "", "");
        call.enqueue(new Callback<DonationCenterSlidersResponse>() {
            @Override
            public void onResponse(Call<DonationCenterSlidersResponse> call, Response<DonationCenterSlidersResponse> response) {
                if(response.isSuccessful()) {
                    HashMap<String,String> url_maps = new HashMap<String, String>();
                    int i = 0;
                    for(Slider slider: response.body().getResponseData()) {
                        i++;
                        if(slider.getUrl()!=null && slider.getUrl().equalsIgnoreCase("0"))
                            url_maps.put(i+"", slider.getName());
                        else
                            url_maps.put(i+"", slider.getUrl());
                    }

                    for (Map.Entry<String, String> entry : url_maps.entrySet()) {
                        TextSliderView textSliderView = new TextSliderView(getActivity());
                        // initialize a SliderLayout
                        textSliderView
                                .description("")
                                .image(entry.getValue())
                                .setScaleType(BaseSliderView.ScaleType.Fit)
                                .setOnSliderClickListener(HomeFragment.this);

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra",entry.getKey());

                        mDemoSlider.addSlider(textSliderView);
                    }

                    mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                    mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                    mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                    mDemoSlider.setDuration(5000);
                    mDemoSlider.addOnPageChangeListener(HomeFragment.this);
                }
            }

            @Override
            public void onFailure(Call<DonationCenterSlidersResponse> call, Throwable t) {
                Log.i(getClass().getSimpleName(), "");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
//        recyclerView = view.findViewById(R.id.item_list);
        mDemoSlider = (SliderLayout)view.findViewById(R.id.slider);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDonationHistoryFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<TotalDonation> totalDonations) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(totalDonations, getActivity()));
    }

    private void setupSlider() {

    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

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
    public interface OnHomeFragmentInteractionListener {
        // TODO: Update argument type and name
        void onHomeFragmentInteraction(Uri uri);
    }

    private void showUserDetails(View view){
        Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        if(config != null && config.getFirstName() != null && config.getLastName() != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phoneNumber = null;
            TextView nameTextView = (TextView) view.findViewById(R.id.text_name);
            TextView mobileTextView = (TextView) view.findViewById(R.id.text_mobile_phone);
            nameTextView.setText(config.getFirstName() + " " + config.getLastName());

            try {
                phoneNumber = phoneUtil.parse(config.getMobilePhone(), Locale.getDefault().getCountry());
                mobileTextView.setText(phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
            } catch (NumberParseException e) {

            }
        }

        imgPerson = view.findViewById(R.id.img_user_profile);
        imgPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(imgPerson, "profile");
            }
        });

    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private NumberFormat numberFormat;
        private Activity mParentActivity;
        private List<TotalDonation> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TotalDonation item = (TotalDonation) view.getTag();

            }
        };

        SimpleItemRecyclerViewAdapter(List<TotalDonation> items, Activity activity) {
            mValues = items;
            numberFormat  = NumberFormat.getNumberInstance();
            numberFormat.setGroupingUsed(true);
            numberFormat.setMaximumFractionDigits(2);
            numberFormat.setMinimumFractionDigits(2);
            mParentActivity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mTotalAmountView.setText(numberFormat.format(mValues.get(position).getTotalAmount()) + " " + mValues.get(position).getCurrency());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mTotalAmountView;

            ViewHolder(View view) {
                super(view);
                mTotalAmountView = (TextView) view.findViewById(R.id.text_amount);
            }
        }
    }

    @Override
    public void finish() {
        final Realm realm = Realm.getDefaultInstance();
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final MemberInfoRequest request = new MemberInfoRequest();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        request.setCenterCardId(donationCenter.getCardId());
        Config config = realm.where(Config.class).findFirst();
        String savedPhone = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()); //PreferenceManager.getDefaultSharedPreferences(BaseActivity.this).getString(SimpleLoginActivity.PHONE_LABEL, null);
        String savedPassword = config.getPassword(); //PreferenceManager.getDefaultSharedPreferences(BaseActivity.this).getString(SimpleLoginActivity.PASSWORD_LABEL, null);
        request.setPhoneNumber(savedPhone);
        request.setPassword(savedPassword);
        request.setCenterCardId(donationCenter.getCardId());
        final Call<UserLoginResponse> call = apiService.login(request, getActivity().getResources().getString(R.string.org_filter),"", ApplicationUtils.APP_ID);
        call.enqueue(new Callback<UserLoginResponse>() {
            @Override
            public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                if (null != response && response.isSuccessful()) {
                    realm.beginTransaction();
                    realm.delete(User.class);
                    User user = response.body().getResponseData();
                    realm.copyToRealmOrUpdate(user);
                    realm.commitTransaction();
                    loadPersonImage(user);
                }
            }

            @Override
            public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "File was not uploaded successfully ", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void loadPersonImage(User user) {
        final Realm realm = Realm.getDefaultInstance();
        if (null != user && null != user.getProfileImage() && !user.getProfileImage().equals("0")) {
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress);
            progressBar.setVisibility(View.VISIBLE);
            String url = ServiceGenerator.PROFILE_PICS_BASE_URL + "/" + user.getProfileImage().charAt(0) + "/" + user.getTexterCardId().charAt(0) + "/" + user.getProfileImage();
            try {
                Picasso.with(getActivity())
                        .load(url)
                        .resize(70, 70)
                        .centerCrop().noPlaceholder()
                        //.placeholder(R.mipmap.ic_user_default)
                        .into(imgPerson, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                if (progressBar != null) {
                                    progressBar.setVisibility(GONE);
                                }
                            }

                            @Override
                            public void onError() {
                                if (progressBar != null) {
                                    progressBar.setVisibility(GONE);
                                }
                            }
                        });
            } catch (Exception e) {
                if (progressBar != null) {
                    progressBar.setVisibility(GONE);
                }

            }
        }
    }
}

/**
 * HashMap<String,String> url_maps = new HashMap<String, String>();
 url_maps.put("Apostle Suleman", "https://apps1.vomozsystems.com/vomoz/sliders/ofm/4.jpg");
 url_maps.put("Support Our Crusades", "https://apps1.vomozsystems.com/vomoz/sliders/ofm/crusade-long-banner.jpg");
 url_maps.put("Give Your Offering", "https://apps1.vomozsystems.com/vomoz/sliders/ofm/offering-long-banner.jpg");
 url_maps.put("Give Your Tithe", "https://apps1.vomozsystems.com/vomoz/sliders/ofm/tithe-long-banner.jpg");

 for(String name : url_maps.keySet()){
 TextSliderView textSliderView = new TextSliderView(getActivity());
 // initialize a SliderLayout
 textSliderView
 .description(name)
 .image(url_maps.get(name))
 .setScaleType(BaseSliderView.ScaleType.Fit)
 .setOnSliderClickListener(this);

 //add your extra information
 textSliderView.bundle(new Bundle());
 textSliderView.getBundle()
 .putString("extra",name);

 mDemoSlider.addSlider(textSliderView);
 }

 mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
 mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
 mDemoSlider.setCustomAnimation(new DescriptionAnimation());
 mDemoSlider.setDuration(4000);
 mDemoSlider.addOnPageChangeListener(this);
 */

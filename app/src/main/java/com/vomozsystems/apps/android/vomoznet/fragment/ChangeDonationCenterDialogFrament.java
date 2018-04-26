package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.vomozsystems.apps.android.vomoznet.MyChurchActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyOrganizationRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.ChangeDonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leksrej on 3/19/18.
 */

public class ChangeDonationCenterDialogFrament extends DialogFragment {

    private DonationCenter selectedDonationCenter;
    private MyChurchActivity myChurchActivity;
    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(myChurchActivity !=null)
            myChurchActivity.refreshView();
    }

    public void startStopChange(boolean start) {
        ProgressBar progressBar = view.findViewById(R.id.progress_dialog);
        if(start) {
             progressBar.setVisibility(View.VISIBLE);
        }else
            progressBar.setVisibility(View.GONE);
    }
    public static ChangeDonationCenterDialogFrament newInstance(MyChurchActivity myChurchActivity) {
        ChangeDonationCenterDialogFrament fragment = new ChangeDonationCenterDialogFrament();
        fragment.myChurchActivity = myChurchActivity;
        return fragment;
    }

    public void changeOrganization(DonationCenter newDonationCenter) {
        Realm realm = Realm.getDefaultInstance();
        final Long newDonationCenterCardId = newDonationCenter.getCardId();
        final String newMerchantIdCode = newDonationCenter.getMerchantIdCode();
        final User user = realm.where(User.class).findFirst();
        final String universalAuthToken = user.getAuthToken();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        final String giverPhoneNumber = user.getMobilePhone();
        final String giverLastName = user.getLastName();
        final String giverFirstName = user.getFirstName();
        final String giverEmail = user.getEmail();

        MakeDonationInterface apiInterface = ApplicationUtils.getDonationInterface();
        Call<ChangeDonationCenterResponse> call = apiInterface.changeOrganization(
                universalAuthToken,donationCenter.getCardId(), donationCenter.getMerchantIdCode(),newDonationCenterCardId,newMerchantIdCode,
                giverPhoneNumber,giverFirstName,giverLastName,giverEmail,"SwitchTheDefaultOrganizationOfThisUser");

        call.enqueue(new Callback<ChangeDonationCenterResponse>() {
            @Override
            public void onResponse(Call<ChangeDonationCenterResponse> call, Response<ChangeDonationCenterResponse> response) {
                if(response.isSuccessful()) {
                    ChangeDonationCenterResponse changeDonationCenterResponse = response.body();
                    if(changeDonationCenterResponse.getStatus().equalsIgnoreCase("1")) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("SUCCESS : " + changeDonationCenterResponse.getOtherInformation().getReturnMessage())
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                }).show();
                    } else {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("FAILURE - " + changeDonationCenterResponse.getFaultCode() +":"+changeDonationCenterResponse.getFaultString())
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                }).show();
                    }
                }else {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Your organization was not changed successfully")
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
            public void onFailure(Call<ChangeDonationCenterResponse> call, Throwable t) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("Your organization was not changed successfully")
                        .setConfirmText("OK")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        }).show();
            }
        });
    }
//AIzaSyDZJzXzrbChP3CojvXhNPB9o8FsHlkpIpI
    private List<DonationCenter> donationCenters;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.change_donation_center_dialog, container, false);
        final Realm realm = Realm.getDefaultInstance();
        final DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String filter = getString(R.string.org_filter);
        Call<DonationCenterResponse> call2 = apiInterface.getAll(filter);
        call2.enqueue(new Callback<DonationCenterResponse>() {
            @Override
            public void onResponse(Call<DonationCenterResponse> call, final Response<DonationCenterResponse> response) {
                if (response.isSuccessful()) {
                    realm.beginTransaction();
                    for(DonationCenter donationCenter1: response.body().getResponseData()) {
                        if(donationCenter.getCardId().equals(donationCenter1.getCardId())) {
                            donationCenter1.setHomeDonationCenter(donationCenter.getHomeDonationCenter());
                        }
                    }
                    realm.copyToRealmOrUpdate(response.body().getResponseData());
                    realm.commitTransaction();

                    donationCenters = response.body().getResponseData();
                    RecyclerView recyclerView = view.findViewById(R.id.donation_center_list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    final MyOrganizationRecyclerViewAdapter adapter = new MyOrganizationRecyclerViewAdapter(getActivity(), donationCenters, ChangeDonationCenterDialogFrament.this);
                    recyclerView.setAdapter(adapter);

                    Button close = view.findViewById(R.id.close);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dismiss();
                        }
                    });
                    final EditText searchEditText = view.findViewById(R.id.search_text);
                    searchEditText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.toString().length() > 2) {
                                List<DonationCenter> tempClients = new ArrayList<DonationCenter>();
                                if(donationCenters!=null) {
                                    for (DonationCenter donationCenter : donationCenters) {
                                        if (s.length() > 2) {
                                            try {
                                                if (donationCenter.getName().toLowerCase().contains(s.toString().toLowerCase())
                                                        || donationCenter.getAddress().toLowerCase().contains(s.toString().toLowerCase())
                                                        || donationCenter.getShortName().toLowerCase().contains(s.toString().toLowerCase())
                                                        || donationCenter.getTelephoneNumber().toLowerCase().contains(s.toString().toLowerCase())) {
                                                    tempClients.add(donationCenter);
                                                }
                                            } catch (Exception e) {

                                            }
                                        }
                                    }
                                    adapter.setmValues(tempClients);
                                    if (null != tempClients && tempClients.size() > 0) {
                                        selectedDonationCenter = tempClients.get(0);
                                        ApplicationUtils.hideSoftKeyboard(getActivity());
                                    }
                                }
                            } else {
                                adapter.setmValues(donationCenters);
                                selectedDonationCenter = null;
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                    });
                }
            }

            @Override
            public void onFailure(Call<DonationCenterResponse> call, Throwable t) {

            }
        });



        return view;
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

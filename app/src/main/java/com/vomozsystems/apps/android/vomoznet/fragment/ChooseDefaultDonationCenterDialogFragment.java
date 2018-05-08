package com.vomozsystems.apps.android.vomoznet.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

import com.vomozsystems.apps.android.vomoznet.MainActivity;
import com.vomozsystems.apps.android.vomoznet.MyChurchActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyOrganizationRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.adapter.SetDefaultDonationCenterRecyclerViewAdapter;
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
 * A simple {@link Fragment} subclass.
 */
public class ChooseDefaultDonationCenterDialogFragment extends DialogFragment {

    private View view;
    private DonationCenter selectedDonationCenter;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }


    public static ChooseDefaultDonationCenterDialogFragment newInstance(List<DonationCenter> donationCenters, MainActivity mainActivity) {
        ChooseDefaultDonationCenterDialogFragment fragment = new ChooseDefaultDonationCenterDialogFragment();
        fragment.donationCenters = donationCenters;
        fragment.mainActivity = mainActivity;
        return fragment;
    }


    //AIzaSyDZJzXzrbChP3CojvXhNPB9o8FsHlkpIpI
    private List<DonationCenter> donationCenters;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_default_donation_center_dialog, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.donation_center_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final SetDefaultDonationCenterRecyclerViewAdapter adapter = new SetDefaultDonationCenterRecyclerViewAdapter(mainActivity, donationCenters);
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

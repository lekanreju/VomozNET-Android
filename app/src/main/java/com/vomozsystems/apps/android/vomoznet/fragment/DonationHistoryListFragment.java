package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyDonationHistoryRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationHistory;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationStatementResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.utility.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by leksrej on 8/21/16.
 */
public class DonationHistoryListFragment extends DialogFragment {
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(MakeDonationInterface.readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(MakeDonationInterface.connectTimeOut, TimeUnit.SECONDS)
            .build();
    private View view;
    private RecyclerView recyclerView;
    private MyDonationHistoryRecyclerViewAdapter adapter;
    private Date startDate;
    private Date endDate;
    private String code;
    private Realm realm;
    private List<DonationHistory> donationHistoryHistories;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<DonationHistory> getDonationHistoryHistories() {
        return donationHistoryHistories;
    }

    public void setDonationHistoryHistories(List<DonationHistory> donationHistoryHistories) {
        this.donationHistoryHistories = donationHistoryHistories;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public MyDonationHistoryRecyclerViewAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MyDonationHistoryRecyclerViewAdapter adapter) {
        this.adapter = adapter;
    }

    private MakeDonationInterface getDonationInterface() {
        final String SERVER_URL = MakeDonationInterface.SERVER_URL;
        Retrofit retrofit = null;
        Gson gson = new GsonBuilder().create();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            MakeDonationInterface makeDonationInterface = retrofit.create(MakeDonationInterface.class);
            return makeDonationInterface;
        }
        return null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final DonationCenter defaultDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        if (null != defaultDonationCenter) {

            String authToken = "";//PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
            MakeDonationInterface makeDonationInterface = getDonationInterface();
            Call<GetDonationStatementResponse> call = makeDonationInterface.getPDFStatement(authToken, defaultDonationCenter.getCardId(), defaultDonationCenter.getMerchantIdCode(), "All", dateFormat.format(startDate), dateFormat.format(endDate), "GeneratePDFContributionsReportWithTimeRangeForThisGiver");
            call.enqueue(new Callback<GetDonationStatementResponse>() {
                @Override
                public void onResponse(Call<GetDonationStatementResponse> call, Response<GetDonationStatementResponse> response) {
                    if (response.isSuccessful() && null != response.body()) {
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GetDonationStatementResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Donation History")
                .setIcon(R.drawable.ic_launcher)
                .setPositiveButton("Print PDF",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                final DonationHistoryPDFFragment dialogFragment = new DonationHistoryPDFFragment();
                                dialogFragment.setUrl("https://vz.vomoz.net/vz/xDst/2/2/?vziStMt=Xzk3MTQ4NTMwODUyMC5wZGY=");
                                dialogFragment.show(getFragmentManager(), "Donation_History");

                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                );

        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.list_donation_history, null);
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new MyDonationHistoryRecyclerViewAdapter(getActivity(), donationHistoryHistories, null);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onResume() {
        // Set the width of the dialog proportional to 90% of the screen width
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        super.onResume();

        realm = Realm.getDefaultInstance();

    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    public void refresh(List<DonationHistory> donationHistoryHistories) {
        adapter.setmValues(donationHistoryHistories);
        adapter.notifyDataSetChanged();
    }
}

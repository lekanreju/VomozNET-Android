package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyDonationHistoryRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationHistory;
import com.vomozsystems.apps.android.vomoznet.entity.DonationType;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationHistoryRequest;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationHistoryResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationStatementResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DonationHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DonationHistoryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(MakeDonationInterface.readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(MakeDonationInterface.connectTimeOut, TimeUnit.SECONDS)
            .build();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnDonationHistoryListFragmentInteractionListener mListener;
    private Realm realm;
    private Spinner spinnerDonationCenters;
    private TextView edtTxtStartDate, edtTxtEndDate;
    private int mYear, mMonth, mDay;
    private List<DonationType> donationTypes;
    private Button btnGeneratePdf;
    private GetDonationHistoryRequest request;
    private DonationHistoryListFragment donationHistoryListFragment;
    private DonationHistoryPDFFragment donationHistoryPDFFragment;
    private RecyclerView recyclerView;
    private MyDonationHistoryRecyclerViewAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private FloatingActionButton fab;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM-d-yyyy");
    private SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd-yyyy");
    private String startDate, endDate;
    private String code = null;
    private TextView emptyView;
    private Button btnExport;
    private DonationCenter selectedDonationCenter;
    private List<DonationHistory> donationHistoryList;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DonationHistoryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static DonationHistoryFragment newInstance() {
        DonationHistoryFragment fragment = new DonationHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donation_history, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        btnExport = (Button) view.findViewById(R.id.btn_export);
        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm = Realm.getDefaultInstance();
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat fmt = new SimpleDateFormat("MM-dd-yyyy");
                final DonationCenter defaultDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
                if(donationHistoryList == null) {
                    final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText(getResources().getString(R.string.app_name))
                            .setContentText("Your statement cannot be generated. No contributions found");
                    dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                            text.setSingleLine(false);
                        }
                    });
                    dialog1.show();
                }
                else if (null != defaultDonationCenter) {
                    try {
                        User user = realm.where(User.class).findFirst();
                        if (null != user) {
                            String authToken = user.getAuthToken();//PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
                            MakeDonationInterface makeDonationInterface = getDonationInterface();
                            Call<GetDonationStatementResponse> call = makeDonationInterface.getPDFStatement(authToken, defaultDonationCenter.getCardId(), defaultDonationCenter.getMerchantIdCode(), "All", dateFormat.format(fmt.parse(startDate)), dateFormat.format(fmt.parse(endDate)), "GeneratePDFContributionsReportWithTimeRangeForThisGiver");
                            call.enqueue(new Callback<GetDonationStatementResponse>() {
                                @Override
                                public void onResponse(Call<GetDonationStatementResponse> call, final Response<GetDonationStatementResponse> response) {
                                    if (response.isSuccessful() && null != response.body()) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            public void run() {
                                                try {
                                                    final DonationHistoryPDFFragment dialogFragment = new DonationHistoryPDFFragment();
                                                    dialogFragment.setUrl(response.body().getStatementUrl());
                                                    dialogFragment.show(getFragmentManager(), "Donation_History");
                                                } catch (Exception e) {
                                                    final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText(getResources().getString(R.string.app_name))
                                                            .setContentText("Your statement cannot be generated. \n Please try again or contact our support line.");
                                                    dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                                        @Override
                                                        public void onShow(DialogInterface dialogInterface) {
                                                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                                            text.setSingleLine(false);
                                                        }
                                                    });
                                                    dialog1.show();
                                                }
                                            }
                                        });

                                    } else {
                                        final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText(getResources().getString(R.string.app_name))
                                                .setContentText("Your statement cannot be generated!! \n\n " + response.body().getFaultCode() + ": " + response.body().getFaultString());
                                        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                            @Override
                                            public void onShow(DialogInterface dialogInterface) {
                                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                                text.setSingleLine(false);
                                            }
                                        });
                                        dialog1.show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GetDonationStatementResponse> call, Throwable t) {
                                    final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.app_name))
                                            .setContentText("Your statement cannot be generated. \n Please try again or contact our support line.");
                                    dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                            text.setSingleLine(false);
                                        }
                                    });
                                    dialog1.show();
                                }
                            });
                        } else {
                            final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText("User profile cannot be retrieved. \n Please try again or contact our support line.");
                            dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                    TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                    text.setSingleLine(false);
                                }
                            });
                            dialog1.show();
                        }
                    } catch (ParseException p) {
                        final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Your statement cannot be generated. \n Please try again or contact our support line.");
                        dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                text.setSingleLine(false);
                            }
                        });
                        dialog1.show();
                    }
                }
            }
        });

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -30);
        startDate = dateFormat2.format(c.getTime());
        endDate = dateFormat2.format(new Date());
//        btnExport.setEnabled(true);
        showHistory(startDate, endDate, "All", "No contributions found within the last 30 days", false);


//        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_type);
//        final RadioButton radioSpecify = (RadioButton) view.findViewById(R.id.radio_date_range);
        final LinearLayout layoutRange = (LinearLayout) view.findViewById(R.id.date_range_layout);

        Button btnGenerate = (Button) view.findViewById(R.id.btn_generate);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnExport.setEnabled(true);
//                int selectedId = radioGroup.getCheckedRadioButtonId();
//                if (selectedId == radioSpecify.getId()) {
                if (spinnerDonationCenters.getSelectedItemPosition() > 0)
                    selectedDonationCenter = realm.where(DonationCenter.class).equalTo("name", spinnerDonationCenters.getSelectedItem().toString()).findFirst();
                else {
                    selectedDonationCenter = new DonationCenter();
                    selectedDonationCenter.setCardId(0L);
                }
                if (null != edtTxtStartDate.getText() && edtTxtStartDate.getText().length() > 0 && null != edtTxtEndDate.getText() && edtTxtEndDate.getText().length() > 0) {

                        try {
                            realm = Realm.getDefaultInstance();
                            Date dt1 = dateFormat.parse(edtTxtStartDate.getText().toString());
                            startDate = dateFormat2.format(dt1);
                            dt1 = dateFormat.parse(edtTxtEndDate.getText().toString());
                            endDate = dateFormat2.format(dt1);
                            btnExport.setEnabled(true);
                            showHistory(startDate, endDate, "All", "No transactions found between " + edtTxtStartDate.getText().toString() + " and " + edtTxtEndDate.getText().toString(), true);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Calendar c = Calendar.getInstance();
                        c.add(Calendar.DAY_OF_MONTH, -30);
                        startDate = dateFormat2.format(c.getTime());
                        endDate = dateFormat2.format(new Date());
                        btnExport.setEnabled(true);
                        showHistory(startDate, endDate, "All", "No contributions found within the last 30 days", true);
                    }
            }
        });

        spinnerDonationCenters = (Spinner) view.findViewById(R.id.spinner_donation_centers);
        spinnerDonationCenters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                realm = Realm.getDefaultInstance();
                selectedDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
                if (spinnerDonationCenters.getSelectedItemPosition() > 0)
                    selectedDonationCenter = realm.where(DonationCenter.class).equalTo("name", spinnerDonationCenters.getSelectedItem().toString()).findFirst();
                else
                {
                    selectedDonationCenter = new DonationCenter();
                    selectedDonationCenter.setCardId(0L);
                }
                showHistory(startDate, endDate, "All", "No contributions found within the last 30 days", true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtTxtStartDate = (TextView) view.findViewById(R.id.edittxt_start_date);
        edtTxtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                edtTxtStartDate.setText(getMonthName(monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        edtTxtEndDate = (TextView) view.findViewById(R.id.edittxt_end_date);
        edtTxtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String mm = (monthOfYear < 10 ? "0" + monthOfYear : monthOfYear + "");
                                edtTxtEndDate.setText(getMonthName(monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        edtTxtEndDate.setText("");
        edtTxtStartDate.setText("");

//        radioSpecify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //layoutRange.setVisibility(View.VISIBLE);
//                edtTxtEndDate.setEnabled(true);
//                edtTxtStartDate.setTextColor(getResources().getColor(R.color.white));
//                edtTxtStartDate.setEnabled(true);
//                edtTxtEndDate.setTextColor(getResources().getColor(R.color.white));
//                adapter = new MyDonationHistoryRecyclerViewAdapter(getActivity(), new ArrayList<DonationHistory>(), null);
//                recyclerView.setAdapter(adapter);
//                //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
//                adapter.notifyDataSetChanged();
//                btnExport.setEnabled(false);
//            }
//        });
//
//        RadioButton radioLast30Days = (RadioButton) view.findViewById(R.id.radio_30_Days);
//        radioLast30Days.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //layoutRange.setVisibility(View.GONE);
//                edtTxtEndDate.setEnabled(false);
//                edtTxtEndDate.setTextColor(getResources().getColor(R.color.dark_gray));
//                edtTxtEndDate.setText("");
//                edtTxtStartDate.setEnabled(false);
//                edtTxtStartDate.setTextColor(getResources().getColor(R.color.dark_gray));
//                edtTxtStartDate.setText("");
//            }
//        });

        realm = Realm.getDefaultInstance();
        List<DonationCenter> donationCenters = realm.where(DonationCenter.class).findAll();
        if (null != donationCenters) {
            List<String> names = new ArrayList<String>();
            names.add("All " + getResources().getString(R.string.org_type_plural));
            for (DonationCenter donationCenter : donationCenters) {
                names.add(donationCenter.getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, names);
            adapter.setDropDownViewResource(R.layout.spinner_drop_down);
            spinnerDonationCenters.setAdapter(adapter);
        }
        return view;
    }

    private String getMonthName(int monthNumber) {
        String[] months = new DateFormatSymbols().getShortMonths();
        int n = monthNumber - 1;
        return (n >= 0 && n <= 11) ? months[n] : "wrong number";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDonationHistoryListFragmentInteractionListener) {
            mListener = (OnDonationHistoryListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDonationHistoryListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showHistory(final String startDate, final String endDate, String code, final String message, final boolean showMessage) {
        btnExport.setEnabled(true);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        DonationCenter defaultDonationCenter = selectedDonationCenter;//realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        long donationCenterCardId = 0;
        if (null != defaultDonationCenter)
            donationCenterCardId = defaultDonationCenter.getCardId();
        else
            donationCenterCardId = 0;
        donationHistoryList = null;
        request = new GetDonationHistoryRequest();
        String mobilePhone = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone());
        request.setCallerId(mobilePhone);
        request.setDonationCenterCardId(donationCenterCardId);
        try {
            User user = realm.where(User.class).findFirst();
            if(null != user) {
                request.setTexterCardId(Long.parseLong(user.getTexterCardId()));
                request.setEndDate(endDate);
                request.setStartDate(startDate);
                request.setDonationType(code);
                Call<GetDonationHistoryResponse> call = apiService.getDonationsHistory(request, user.getAuthToken(), ApplicationUtils.APP_ID);
                call.enqueue(new Callback<GetDonationHistoryResponse>() {
                    @Override
                    public void onResponse(Call<GetDonationHistoryResponse> call, Response<GetDonationHistoryResponse> response) {
                        try {
                            if (response.isSuccessful()) {

                                if (null != response.body().getResponseData() && response.body().getResponseData().size() > 0) {
                                    emptyView.setVisibility(View.GONE);
                                    recyclerView.setVisibility(VISIBLE);
                                    donationHistoryList = response.body().getResponseData();
                                    adapter = new MyDonationHistoryRecyclerViewAdapter(getActivity(), response.body().getResponseData(), null);
                                    recyclerView.setAdapter(adapter);
                                    //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                                    adapter.notifyDataSetChanged();
                                    btnExport.setEnabled(true);

                                } else {
                                    adapter = new MyDonationHistoryRecyclerViewAdapter(getActivity(), new ArrayList<DonationHistory>(), null);
                                    recyclerView.setAdapter(adapter);
                                    //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                                    adapter.notifyDataSetChanged();
                                    btnExport.setEnabled(true);
                                    emptyView.setVisibility(VISIBLE);
                                    recyclerView.setVisibility(GONE);
                                }
                            } else {
                                try {
                                    adapter = new MyDonationHistoryRecyclerViewAdapter(getActivity(), new ArrayList<DonationHistory>(), null);
                                    recyclerView.setAdapter(adapter);
                                    emptyView.setVisibility(VISIBLE);
                                    recyclerView.setVisibility(GONE);
                                    //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
                                    adapter.notifyDataSetChanged();
                                    btnExport.setEnabled(true);
                                    String json = response.errorBody().string();
                                    BaseServiceResponse baseServiceResponse = ApiClient.getError(json);
                                    final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.app_name));
                                    dialog1.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    dialog1.setContentText(baseServiceResponse.getMessage().getDescription() + "\n" +
                                            "TransactionID : " + baseServiceResponse.getTransactionId());
                                    dialog1.setConfirmText("Ok");
                                    dialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            dialog1.dismiss();
                                        }
                                    });
                                    dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                            text.setSingleLine(false);
                                        }
                                    });
                                } catch (Exception e) {
                                    emptyView.setVisibility(VISIBLE);
                                    recyclerView.setVisibility(GONE);
                                    final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText(getResources().getString(R.string.app_name));
                                    dialog1.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    dialog1.setContentText("Donation history cannot be displayed");
                                    dialog1.setConfirmText("Ok");
                                    dialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            dialog1.dismiss();
                                        }
                                    });
                                    dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
                                            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                            TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                            text.setSingleLine(false);
                                        }
                                    });
                                }
                            }
                        } catch (Exception e) {
                            emptyView.setVisibility(VISIBLE);
                            recyclerView.setVisibility(GONE);
                            final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name));
                            dialog1.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            dialog1.setContentText("Donation history cannot be displayed");
                            dialog1.setConfirmText("Ok");
                            dialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog1.dismiss();
                                }
                            });
                            dialog1.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    SweetAlertDialog alertDialog = (SweetAlertDialog) dialog1;
                                    TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                    text.setSingleLine(false);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDonationHistoryResponse> call, Throwable t) {
                        emptyView.setVisibility(VISIBLE);
                        recyclerView.setVisibility(GONE);
                        Log.d(getClass().getSimpleName(), "Login Failure >>>>>>");
                        final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Error fetching donations");
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                                TextView text = (TextView) alertDialog.findViewById(R.id.title_text);
                                text.setSingleLine(false);
                            }
                        });
                        dialog.show();

                    }
                });
            }else if(showMessage){
                emptyView.setVisibility(VISIBLE);
                recyclerView.setVisibility(GONE);
                SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("No donations found");
                dialog.show();
            }
        } catch (Exception e) {
            Log.i("", getClass().getSimpleName());
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnDonationHistoryListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDonationHistoryListFragmentInteraction(DonationHistory item);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new DonationHistoryListFragment();
            else if (position == 1)
                return new DonationHistoryPDFFragment();
            else
                return null;
        }

        @Override
        public int getCount() {
            return 2;           // As there are only 3 Tabs
        }

    }
}

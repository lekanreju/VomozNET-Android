package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.Saveable;
import com.vomozsystems.apps.android.vomoznet.entity.Career;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.Data;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberRequest;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetReferenceDataResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMyCareerInfoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileCareerInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileCareerInfoFragment extends Fragment implements Saveable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SweetAlertDialog dialog;
    private OnMyCareerInfoFragmentInteractionListener mListener;
    private Realm realm;
    private EditText occupationEditText;
    private EditText employmentStatusEditText;
    private Career career;
    private List<Data> careerTypes;

    public ProfileCareerInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileCareerInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileCareerInfoFragment newInstance() {
        ProfileCareerInfoFragment fragment = new ProfileCareerInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        realm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.fragment_my_career_info, container, false);

        occupationEditText = (EditText) view.findViewById(R.id.edit_occupation);
        employmentStatusEditText = (EditText) view.findViewById(R.id.edit_employment_status);

        enableTextFields(false);

        employmentStatusEditText.setInputType(0);
        employmentStatusEditText.setFocusable(false);
        employmentStatusEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
                final CharSequence[] items = {
                        "Employed", "Unemployed", "Student", "Self Employed", "Retired", "Other"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Employment Status");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        employmentStatusEditText.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<GetReferenceDataResponse> call2 = apiService.getReferenceData();
        call2.enqueue(new Callback<GetReferenceDataResponse>() {
            @Override
            public void onResponse(Call<GetReferenceDataResponse> call, Response<GetReferenceDataResponse> response) {
                if (response.isSuccessful()) {
                    GetReferenceDataResponse resp = response.body();
                    if (resp != null) {
                        careerTypes = resp.getResponseData().getCareerTypes();

                        final String[] items = new String[careerTypes.size()];
                        int i = 0;
                        for (Data data : careerTypes) {
                            items[i] = data.getName();
                            i++;
                        }
                        occupationEditText.setInputType(0);
                        occupationEditText.setFocusable(false);
                        occupationEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                enableTextFields(true);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Select Occupation");
                                builder.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        occupationEditText.setText(items[item]);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<GetReferenceDataResponse> call, Throwable t) {
                Log.d(getClass().getSimpleName(), "ERROR Received " + t.getMessage());
            }
        });

        return view;
    }

    public void getInfo() {
        View view = getView();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        String phone = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone());
        String password = config.getPassword();
        GetMemberRequest getMemberRequest = new GetMemberRequest();
        getMemberRequest.setPhoneNumber(phone);
        getMemberRequest.setPassword(password);
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        if (null != donationCenter) {
            getMemberRequest.setCenterCardId(String.valueOf(donationCenter.getCardId()));
            final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText("Loading...Please Wait");
            dialog.show();
            String authToken = "";//PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
            Call<GetMemberResponse> call = apiService.getMemberCareerInfo(getMemberRequest, authToken, ApplicationUtils.APP_ID);
            final TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
            final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
            scrollView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<GetMemberResponse>() {
                @Override
                public void onResponse(Call<GetMemberResponse> call, Response<GetMemberResponse> response) {
                    if (response.isSuccessful()) {
                        if (null != response.body() && null != response.body().getResponseData() && null != response.body().getResponseData().getCareer()) {
                            scrollView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            career = response.body().getResponseData().getCareer();
                            if (career != null) {

                                if (career.getEmploymentStatus().equalsIgnoreCase("SE")) {
                                    employmentStatusEditText.setText("Self Employed");
                                } else if (career.getEmploymentStatus().equalsIgnoreCase("E")) {
                                    employmentStatusEditText.setText("Employed");
                                } else if (career.getEmploymentStatus().equalsIgnoreCase("U")) {
                                    employmentStatusEditText.setText("Unemployed");
                                } else if (career.getEmploymentStatus().equalsIgnoreCase("S")) {
                                    employmentStatusEditText.setText("Student");
                                } else if (career.getEmploymentStatus().equalsIgnoreCase("R")) {
                                    employmentStatusEditText.setText("Retired");
                                } else if (career.getEmploymentStatus().equalsIgnoreCase("O")) {
                                    employmentStatusEditText.setText("Other");
                                }

                                if (null != careerTypes)
                                    for (Data data : careerTypes) {
                                        if (null != data && career.getOccupation().equals(data.getCode())) {
                                            occupationEditText.setText(data.getName());
                                            break;
                                        }
                                    }
                            }
                        }
                        dialog.dismiss();
//                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                        dialog.setContentText("Your career info was retrieved successfully");
//                        dialog.setConfirmText("Ok");
//                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                dialog.dismiss();
//                            }
//                        });
                    } else {
                        scrollView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        try {
                            String json = response.errorBody().string();
                            BaseServiceResponse baseServiceResponse = ApiClient.getError(json);
                            dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            dialog.setContentText("Your career info was NOT retrieved successfully \n" + baseServiceResponse.getMessage().getDescription() + "\n" +
                                    "TransactionID : " + baseServiceResponse.getTransactionId());
                            dialog.setConfirmText("Ok");
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                }
                            });

                        } catch (Exception e) {

                        }
                    }
                }

                @Override
                public void onFailure(Call<GetMemberResponse> call, Throwable t) {
                    scrollView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    dialog.setContentText("Your career info cannot NOT retrieved !");
                    dialog.setConfirmText("Ok");
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMyCareerInfoFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyCareerInfoFragmentInteractionListener) {
            mListener = (OnMyCareerInfoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private String validate() {
        return null;
    }

    @Override
    public boolean update() {
        enableTextFields(true);
        String validationMessage = validate();
        if (null != validationMessage) {
            SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText(validationMessage);
            dialog.show();
            return false;
        } else {
            career.setOccupation(occupationEditText.getText().toString());

            if (null != careerTypes)
                for (Data data : careerTypes) {
                    if (null != data && occupationEditText.getText().toString().equals(data.getName())) {
                        career.setOccupation(data.getCode());
                        break;
                    }
                }

            if (employmentStatusEditText.getText().toString().equals("Self Employed"))
                career.setEmploymentStatus("SE");
            else if (employmentStatusEditText.getText().toString().equals("Employed"))
                career.setEmploymentStatus("E");
            else if (employmentStatusEditText.getText().toString().equals("Unemployed"))
                career.setEmploymentStatus("U");
            else if (employmentStatusEditText.getText().toString().equals("Student"))
                career.setEmploymentStatus("S");
            else if (employmentStatusEditText.getText().toString().equals("Retired"))
                career.setEmploymentStatus("R");
            else if (employmentStatusEditText.getText().toString().equals("Other"))
                career.setEmploymentStatus("O");


            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            realm = Realm.getDefaultInstance();
            DonationCenter homeCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            if (null != homeCenter) {
                career.setCenterCardId(homeCenter.getCardId());
                String authToken = "";//PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("Saving...");
                dialog.show();
                Call<BaseServiceResponse> call = apiService.updateCareerInfo(career, authToken, "");
                call.enqueue(new Callback<BaseServiceResponse>() {
                    @Override
                    public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                        Log.d(getClass().getSimpleName(), ">>>>>> SUCCESS: " + "CareerInfo Update Completed");
                        if (response.isSuccessful()) {
                            enableTextFields(false);
                            dialog.setContentText("Your career information was saved successfully");
                            dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                }
                            });
                            if (mListener != null) {
                                mListener.onMyCareerInfoFragmentInteraction(null);
                            }

                        } else {
                            try {
                                String json = response.errorBody().string();
                                BaseServiceResponse baseServiceResponse = ApiClient.getError(json);
                                dialog.setContentText(baseServiceResponse.getMessage().getDescription() + "\n" +
                                        "TransactionID : " + baseServiceResponse.getTransactionId());
                                dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                    }
                                });

                                edit();
                            } catch (Exception e) {
                                dialog.setContentText("Your request was not completed successfully");
                                dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseServiceResponse> call, Throwable t) {

                        Log.d(getClass().getSimpleName(), ">>>>>> FAILURE: " + "Personal Info Sync Failed" + " <<<<<<<");
                        dialog.setContentText("Your personal information was not saved successfully");
                        dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dialog.dismiss();
                            }
                        });
                        edit();
                    }
                });
            } else {
                try {
                    dialog.setContentText("Your organization cannot be retrieved");
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.dismiss();
                        }
                    });
                } catch (Exception e) {

                }
            }
            return true;
        }
    }

    @Override
    public void edit() {
        enableTextFields(true);
    }


    @Override
    public void enableTextFields(boolean value) {
        employmentStatusEditText.setEnabled(value);
        employmentStatusEditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
        occupationEditText.setEnabled(value);
        occupationEditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
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
    public interface OnMyCareerInfoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMyCareerInfoFragmentInteraction(Uri uri);
    }
}

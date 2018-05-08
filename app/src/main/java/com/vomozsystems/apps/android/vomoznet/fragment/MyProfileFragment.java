package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.ReferenceData;
import com.vomozsystems.apps.android.vomoznet.entity.ResetPasswordQuestion;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetReferenceDataResponse;
import com.vomozsystems.apps.android.vomoznet.service.VomozGlobalInfo;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.exit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMyProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyProfileFragment extends Fragment {

    // TODO: Rename and change types of parameters
    private Spinner question1Options;
    private Spinner question2Options;
    public static ReferenceData referenceData;


    private OnMyProfileFragmentInteractionListener mListener;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyProfileFragment newInstance(String param1, String param2) {
        MyProfileFragment fragment = new MyProfileFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        EditText firstNameEditText = (EditText) view.findViewById(R.id.edit_first_name);
        EditText lastNameEditText = (EditText) view.findViewById(R.id.edit_last_name);
        EditText emailEditText = (EditText) view.findViewById(R.id.edit_email);
        EditText passwordEditText = (EditText) view.findViewById(R.id.edit_password);
        final EditText question1Answer = (EditText) view.findViewById(R.id.resetpasswordquestion_cardview_answer1_edit_txt);
        final EditText question2Answer = (EditText) view.findViewById(R.id.resetpasswordquestion_cardview_answer2_edit_txt);
        Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();

        Button updateMyProfileButton = view.findViewById(R.id.contribution_button);
        updateMyProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View mView) {
                updateMyProfile(view);
            }
        });
        if(config != null) {
            firstNameEditText.setText(config.getFirstName());
            lastNameEditText.setText(config.getLastName());
            emailEditText.setText(config.getEmail());
            passwordEditText.setText(config.getPassword());

            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<GetReferenceDataResponse> call = apiInterface.getReferenceData();
            call.enqueue(new Callback<GetReferenceDataResponse>() {
                @Override
                public void onResponse(Call<GetReferenceDataResponse> call, Response<GetReferenceDataResponse> response) {
                    if (response.isSuccessful()) {
                        referenceData = response.body().getResponseData();

                        List<String> list = new ArrayList<String>();
                        list.add("Select first security question");
                        question1Options = (Spinner) getView().findViewById(R.id.resetpasswordquestion_cardview_spinner_1);
                        if (null != referenceData.getGroup1Questions())
                            for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup1Questions()) {
                                list.add(resetPasswordQuestion.getQuestion());
                            }

                        ArrayAdapter<String> data1Adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
                        data1Adapter.setDropDownViewResource(R.layout.spinner_drop_down);
                        question1Options.setAdapter(data1Adapter);

                        list = new ArrayList<String>();
                        list.add("Select second security question");
                        question2Options = (Spinner) getView().findViewById(R.id.resetpasswordquestion_cardview_spinner_2);
                        if (null != referenceData.getGroup2Questions())
                            for (ResetPasswordQuestion resetPasswordQuestion : referenceData.getGroup2Questions()) {
                                list.add(resetPasswordQuestion.getQuestion());
                            }
                        ArrayAdapter<String> data2Adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, list);
                        data2Adapter.setDropDownViewResource(R.layout.spinner_drop_down);
                        question2Options.setAdapter(data2Adapter);

                    } else {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setContentText("Service connectivity issue detected. Please try again later.")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        exit(0);
                                    }
                                })
                                .setTitleText(getString(R.string.app_name))
                                .show();

                    }
                }

                @Override
                public void onFailure(Call<GetReferenceDataResponse> call, Throwable t) {
                    //Unable to add window -- token android.os.BinderProxy@2b66a43 is not valid; is your activity running?
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Service connectivity issue detected. Please try again later.")
                            .setTitleText(getString(R.string.app_name))
                            .show();
                }
            });
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onMyProfileFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyProfileFragmentInteractionListener) {
            mListener = (OnMyProfileFragmentInteractionListener) context;
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
    public interface OnMyProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMyProfileFragmentInteraction(Uri uri);
    }

    private void updateMyProfile(View view) {
        String message = validateForm(view);
        if(message != null) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setContentText(message)
                    .setTitleText(getString(R.string.app_name));
            sweetAlertDialog.show();
        } else {
            final Realm realm = Realm.getDefaultInstance();
            final Config config = realm.where(Config.class).findFirst();
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            final VomozGlobalInfo vomozGlobalInfo = new VomozGlobalInfo();

            final DonationCenter defaultDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            final User user = realm.where(User.class).findFirst();

            EditText firstNameEditText = (EditText) view.findViewById(R.id.edit_first_name);
            EditText lastNameEditText = (EditText) view.findViewById(R.id.edit_last_name);
            EditText emailEditText = (EditText) view.findViewById(R.id.edit_email);
            EditText passwordEditText = (EditText) view.findViewById(R.id.edit_password);
            EditText question1Answer = (EditText) view.findViewById(R.id.resetpasswordquestion_cardview_answer1_edit_txt);
            EditText question2Answer = (EditText) view.findViewById(R.id.resetpasswordquestion_cardview_answer2_edit_txt);

            vomozGlobalInfo.setCallerId(config.getMobilePhone());
            vomozGlobalInfo.setFirstName(firstNameEditText.getText().toString());
            vomozGlobalInfo.setLastName(lastNameEditText.getText().toString());
            vomozGlobalInfo.setEmailAddress(emailEditText.getText().toString());
            vomozGlobalInfo.setPassword(passwordEditText.getText().toString());
            vomozGlobalInfo.setCallerId(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
            vomozGlobalInfo.setResetPasswordQuestion1(referenceData.getGroup1Questions().get(question1Options.getSelectedItemPosition() - 1).getQuestionId());
            vomozGlobalInfo.setResetPasswordQuestion2(referenceData.getGroup2Questions().get(question2Options.getSelectedItemPosition() - 1).getQuestionId());
            vomozGlobalInfo.setResetPasswordAnswer1(question1Answer.getText().toString().replace(" ", "").toLowerCase());
            vomozGlobalInfo.setResetPasswordAnswer2(question2Answer.getText().toString().replace(" ", "").toLowerCase());

            if(defaultDonationCenter != null) {
                vomozGlobalInfo.setDonationCenterCardId(defaultDonationCenter.getCardId());
            }
            if(user != null && user.getTexterCardId() != null) {
                vomozGlobalInfo.setTexterCardId(Long.valueOf(user.getTexterCardId()));
            }
            vomozGlobalInfo.setUpdateVzNet(true);

            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                    .setContentText("Saving profile...Please wait")
                    .setTitleText(getString(R.string.app_name));
            sweetAlertDialog.show();

            Call<BaseServiceResponse> call = apiInterface.updateAllGlobalInfo(vomozGlobalInfo, "", ApplicationUtils.APP_ID);
            call.enqueue(new Callback<BaseServiceResponse>() {
                @Override
                public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                    if (response.isSuccessful()) {

                        sweetAlertDialog.setContentText("Profile saved successfully\n\n");
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        sweetAlertDialog.setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        realm.beginTransaction();
                                        if(vomozGlobalInfo.getFirstName() != null && vomozGlobalInfo.getLastName() != null) {
                                            config.setFirstName(vomozGlobalInfo.getFirstName());
                                            config.setLastName(vomozGlobalInfo.getLastName());
                                            config.setEmail(vomozGlobalInfo.getEmailAddress());
                                            config.setPassword(vomozGlobalInfo.getPassword());
                                            config.setSecurityQuestion1Id(vomozGlobalInfo.getResetPasswordQuestion1());
                                            config.setSecurityQuestion2Id(vomozGlobalInfo.getResetPasswordQuestion2());
                                            config.setSecurityQuestion1Answer(vomozGlobalInfo.getResetPasswordAnswer1());
                                            config.setSecurityQuestion2Answer(vomozGlobalInfo.getResetPasswordAnswer2());
                                        }
                                        realm.commitTransaction();
                                    mListener.onMyProfileFragmentInteraction(null);
                                    }
                                });

                    } else {
                        sweetAlertDialog.setContentText("Profile was not saved successfully.\n\n" + response.body().getMessage().getDescription());
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        sweetAlertDialog.setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                    }
                }

                @Override
                public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                    sweetAlertDialog.setContentText("Profile cannot be saved successfully.");
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    sweetAlertDialog.setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            });
                }
            });
        }
    }

    private String validateForm(View view) {
        String message = null;
        Spinner question1Options = (Spinner) view.findViewById(R.id.resetpasswordquestion_cardview_spinner_1);
        Spinner question2Options = (Spinner) view.findViewById(R.id.resetpasswordquestion_cardview_spinner_2);

        EditText firstNameEditText = (EditText) view.findViewById(R.id.edit_first_name);
        EditText lastNameEditText = (EditText) view.findViewById(R.id.edit_last_name);

        EditText question1Answer = (EditText) view.findViewById(R.id.resetpasswordquestion_cardview_answer1_edit_txt);
        EditText question2Answer = (EditText) view.findViewById(R.id.resetpasswordquestion_cardview_answer2_edit_txt);
        EditText passwordEditText = (EditText) view.findViewById(R.id.password_cardview_password_edit_txt);
        passwordEditText = (EditText) view.findViewById(R.id.edit_password);

        Realm realm = Realm.getDefaultInstance();
        final Config config = realm.where(Config.class).findFirst();

        if(firstNameEditText.getText().toString().length() < 2) {
            message = "First name is not valid";
        }
        else if(lastNameEditText.getText().toString().length() < 2) {
            message = "Last name is not valid";
        }
        else if (question1Options.getSelectedItemPosition() == 0) {
            message = "Choose first question";
        }
        else if (question2Options.getSelectedItemPosition() == 0) {
            message = "Choose second question";
        }
        else if (question1Answer.getText().toString().length() == 0)
            message = "Answer first question";
        else if (question2Answer.getText().toString().length() == 0)
            message = "Answer second question";
        else
            message = ApplicationUtils.validatePassword(passwordEditText.getText().toString(), config);

        return message;
    }
}

package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.Saveable;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.Contact;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberRequest;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnMyContactInfoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileContactsInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileContactsInfoFragment extends Fragment implements Saveable {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnMyContactInfoFragmentInteractionListener mListener;
    private EditText firstName1EditText;
    private EditText firstName2EditText;
    private EditText lastName1EditText;
    private EditText lastName2EditText;
    private EditText phone1EditText;
    private EditText phone2EditText;
    private EditText email1EditText;
    private EditText email2EditText;
    private Contact contact;
    private Realm realm;
    private SweetAlertDialog dialog;

    public ProfileContactsInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProfileContactsInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileContactsInfoFragment newInstance() {
        ProfileContactsInfoFragment fragment = new ProfileContactsInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_contacts_info, container, false);
        firstName1EditText = (EditText) view.findViewById(R.id.edit_contact1_first_name);
        firstName1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        firstName2EditText = (EditText) view.findViewById(R.id.edit_contact2_first_name);
        firstName2EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        lastName1EditText = (EditText) view.findViewById(R.id.edit_contact1_last_name);
        lastName1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        lastName2EditText = (EditText) view.findViewById(R.id.edit_contact2_last_name);
        lastName2EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        phone1EditText = (EditText) view.findViewById(R.id.edit_contact1_phone);
        phone1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        phone2EditText = (EditText) view.findViewById(R.id.edit_contact2_phone);
        phone2EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        email1EditText = (EditText) view.findViewById(R.id.edit_contact1_email);
        email1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        email2EditText = (EditText) view.findViewById(R.id.edit_contact2_email);
        email1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        enableTextFields(false);

        realm = Realm.getDefaultInstance();
        return view;
    }

    public void getInfo() {
        View view = getView();
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        String phone = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone());
        String password = config.getPassword();
        final GetMemberRequest getMemberRequest = new GetMemberRequest();
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
            Call<GetMemberResponse> call = apiService.getMemberContactInfo(getMemberRequest, authToken, ApplicationUtils.APP_ID);
            final TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
            final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
            scrollView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<GetMemberResponse>() {
                @Override
                public void onResponse(Call<GetMemberResponse> call, Response<GetMemberResponse> response) {
                    if (response.isSuccessful()) {
                        if (null != response.body() && null != response.body().getResponseData() && null != response.body().getResponseData().getEmergencyContact()) {
                            scrollView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            contact = response.body().getResponseData().getEmergencyContact();
                            if (contact != null) {
                                firstName1EditText.setText(contact.getContact1FirstName());
                                firstName2EditText.setText(contact.getContact2FirstName());
                                lastName1EditText.setText(contact.getContact1LastName());
                                lastName2EditText.setText(contact.getContact2LastName());
                                email1EditText.setText(contact.getContact1Email());
                                email2EditText.setText(contact.getContact2Email());
                                phone1EditText.setText(contact.getContact1Phone());
                                phone2EditText.setText(contact.getContact2Phone());
                            }
                        }
                        dialog.dismiss();
//                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                        dialog.setContentText("Your contacts info was retrieved successfully");
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
                            dialog.setContentText("Your contacts info was NOT retrieved successfully \n" + baseServiceResponse.getMessage().getDescription() + "\n" +
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
                    dialog.setContentText("Your contacts info was NOT retrieved successfully");
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
            mListener.onMyContactInfoFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMyContactInfoFragmentInteractionListener) {
            mListener = (OnMyContactInfoFragmentInteractionListener) context;
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

    public String validate() {
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
            contact.setContact1Email(email1EditText.getText().toString());
            contact.setContact2Email(email2EditText.getText().toString());
            contact.setContact1FirstName(firstName1EditText.getText().toString());
            contact.setContact2FirstName(firstName2EditText.getText().toString());
            contact.setContact1LastName(lastName1EditText.getText().toString());
            contact.setContact2LastName(lastName2EditText.getText().toString());
            contact.setContact1Phone(phone1EditText.getText().toString());
            contact.setContact2Phone(phone2EditText.getText().toString());

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            realm = Realm.getDefaultInstance();
            DonationCenter homeCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            if (null != homeCenter) {
                contact.setCenterCardId(homeCenter.getCardId());
                String authToken = "";//PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("Saving...");
                dialog.show();
                Call<BaseServiceResponse> call = apiService.updateContactInfo(contact, authToken, "");
                call.enqueue(new Callback<BaseServiceResponse>() {
                    @Override
                    public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                        Log.d(getClass().getSimpleName(), ">>>>>> SUCCESS: " + "PersonalInfo Update Completed");
                        if (response.isSuccessful()) {
                            enableTextFields(false);
                            dialog.setContentText("Your contact information was saved successfully");
                            dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                }
                            });
                            if (mListener != null) {
                                mListener.onMyContactInfoFragmentInteraction(null);
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
        firstName1EditText.setEnabled(value);
        firstName1EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
        lastName1EditText.setEnabled(value);
        lastName1EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
        email1EditText.setEnabled(value);
        email1EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
        phone1EditText.setEnabled(value);
        phone1EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));

        firstName2EditText.setEnabled(value);
        firstName2EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
        lastName2EditText.setEnabled(value);
        lastName2EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
        email2EditText.setEnabled(value);
        email2EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
        phone2EditText.setEnabled(value);
        phone2EditText.setBackgroundResource((value ? R.drawable.enable_edit_text : R.drawable.disable_edit_text));
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
    public interface OnMyContactInfoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMyContactInfoFragmentInteraction(Uri uri);
    }
}

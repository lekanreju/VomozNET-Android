package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.Saveable;
import com.vomozsystems.apps.android.vomoznet.SimpleLoginActivity;
import com.vomozsystems.apps.android.vomoznet.entity.Address;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetCountryStatesResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberRequest;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.text.InputType.TYPE_CLASS_TEXT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddressProfileFragment.OnAddressProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddressProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddressProfileFragment extends Fragment implements Saveable {
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(MakeDonationInterface.readTimeOut, TimeUnit.SECONDS)
            .connectTimeout(MakeDonationInterface.connectTimeOut, TimeUnit.SECONDS)
            .build();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SweetAlertDialog dialog;
    private Realm realm;
    private User user;
    private EditText addressLine1EditText;
    private EditText addressLine2EditText;
    private EditText cityOrTownEditText;
    private EditText stateOrProvinceEditText;
    private EditText countryEditText;
    private EditText zipOrPostalCodeEditText;
    private Address address;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnAddressProfileFragmentInteractionListener mListener;
    private View view;
    public AddressProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MyAddressInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddressProfileFragment newInstance() {
        AddressProfileFragment fragment = new AddressProfileFragment();
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
        view = inflater.inflate(R.layout.fragment_address_profile, container, false);

        addressLine1EditText = (EditText) view.findViewById(R.id.edit_address_line1);
        addressLine1EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        addressLine2EditText = (EditText) view.findViewById(R.id.edit_address_line2);
        addressLine2EditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        cityOrTownEditText = (EditText) view.findViewById(R.id.edit_city);
        cityOrTownEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        stateOrProvinceEditText = (EditText) view.findViewById(R.id.edit_state);
        countryEditText = (EditText) view.findViewById(R.id.edit_country);
        zipOrPostalCodeEditText = (EditText) view.findViewById(R.id.edit_zip);

        enableTextFields(false);

        stateOrProvinceEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
                try {
                    String selectedCountry = countryEditText.getText().toString();
                    MakeDonationInterface makeDonationInterface = getDonationInterface();
                    Call<GetCountryStatesResponse> call = makeDonationInterface.getCountryStates(selectedCountry, "ListStatesOrProvinceAvailableForThisCountry");
                    call.enqueue(new Callback<GetCountryStatesResponse>() {
                        @Override
                        public void onResponse(Call<GetCountryStatesResponse> call, Response<GetCountryStatesResponse> response) {
                            if (response.isSuccessful() && null != response.body().getStatesOrProvices()) {
                                stateOrProvinceEditText.setInputType(0);
                                stateOrProvinceEditText.setFocusable(false);
                                final CharSequence[] items = new CharSequence[response.body().getStatesOrProvices().size()];
                                int i = 0;
                                for (String string : response.body().getStatesOrProvices()) {
                                    items[i] = string;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Select State");
                                builder.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        stateOrProvinceEditText.setText(items[item]);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                stateOrProvinceEditText.setInputType(TYPE_CLASS_TEXT);
                                stateOrProvinceEditText.setFocusable(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<GetCountryStatesResponse> call, Throwable t) {
                            Log.i(getClass().getSimpleName(), "Error");
                            stateOrProvinceEditText.setInputType(TYPE_CLASS_TEXT);
                            stateOrProvinceEditText.setFocusable(true);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        countryEditText.setInputType(0);
        countryEditText.setFocusable(false);
        countryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
                final CharSequence[] items = {
                        "US", "Canada", "Nigeria"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Country");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        countryEditText.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        enableTextFields(false);

        getInfo();
        return view;
    }

    public void getInfo() {

        Button saveButton = (Button) view.findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });

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
            String authToken = "";
            PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
            Call<GetMemberResponse> call = apiService.getMemberAddressInfo(getMemberRequest, authToken, ApplicationUtils.APP_ID);
            final TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
            final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
            scrollView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<GetMemberResponse>() {
                @Override
                public void onResponse(Call<GetMemberResponse> call, Response<GetMemberResponse> response) {
                    if (response.isSuccessful()) {
                        if (null != response.body() && null != response.body().getResponseData() && null != response.body().getResponseData().getAddress()) {
                            scrollView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            address = response.body().getResponseData().getAddress();
                            if (address != null) {
                                addressLine1EditText.setText(address.getAddressLine1());
                                addressLine2EditText.setText(address.getAddressLine2());
                                cityOrTownEditText.setText(address.getCityOrTown());
                                stateOrProvinceEditText.setText(address.getStateOrProvince());
                                zipOrPostalCodeEditText.setText(address.getZipOrPostCode());
                                countryEditText.setText(address.getCountry());
                            }
                        }
                        dialog.dismiss();
//                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                        dialog.setContentText("Your address info was retrieved successfully");
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
                            dialog.setContentText("Your address info was retrieved successfully \n" + baseServiceResponse.getMessage().getDescription() + "\n" +
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
                    dialog.setContentText("Your address info was NOT retrieved!");
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
    public void onButtonPressed(User user) {
        if (mListener != null) {
            mListener.onAddressProfileFragmentInteraction(user);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddressProfileFragmentInteractionListener) {
            mListener = (OnAddressProfileFragmentInteractionListener) context;
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

    @Override
    public boolean update() {
        enableTextFields(false);

        String validationMessage = validate();
        if (null != validationMessage) {
            enableTextFields(true);
            SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText(validationMessage);
            dialog.show();
            return false;
        } else {
            address.setAddressLine1(addressLine1EditText.getText().toString());
            address.setAddressLine2(addressLine2EditText.getText().toString());
            address.setCityOrTown(cityOrTownEditText.getText().toString());
            address.setStateOrProvince(stateOrProvinceEditText.getText().toString());
            address.setZipOrPostCode(zipOrPostalCodeEditText.getText().toString());
            address.setCountry(countryEditText.getText().toString());

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            realm = Realm.getDefaultInstance();
            DonationCenter homeCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            if (null != homeCenter) {
                address.setCenterCardId(homeCenter.getCardId());
                String authToken = "";//PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("Saving...");
                dialog.show();
                Call<BaseServiceResponse> call = apiService.updateAddressInfo(address, authToken, "");
                call.enqueue(new Callback<BaseServiceResponse>() {
                    @Override
                    public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                        Log.d(getClass().getSimpleName(), ">>>>>> SUCCESS: " + "PersonalInfo Update Completed");
                        if (response.isSuccessful()) {
                            dialog.setContentText("Your address information was saved successfully");
                            dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                }
                            });
                            if (mListener != null) {
                                mListener.onAddressProfileFragmentInteraction(user);
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
        //addressLine1EditText.setEnabled(value);
        addressLine1EditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //addressLine2EditText.setEnabled(value);
        addressLine2EditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //cityOrTownEditText.setEnabled(value);
        cityOrTownEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //stateOrProvinceEditText.setEnabled(value);
        stateOrProvinceEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //countryEditText.setEnabled(value);
        countryEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //zipOrPostalCodeEditText.setEnabled(value);
        zipOrPostalCodeEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
    }

    public String validate() {
        if (addressLine1EditText.getText().toString().length() == 0) {
            addressLine1EditText.setSelected(true);
            return "Address Line 1 cannot be empty";
        }
        if (cityOrTownEditText.getText().toString().length() == 0) {
            cityOrTownEditText.setSelected(true);
            return "City cannot be empty";
        }
        if (zipOrPostalCodeEditText.getText().toString().length() == 0) {
            zipOrPostalCodeEditText.setSelected(true);
            return "Zip/Postal code cannot be empty.";
        }
        if (ApplicationUtils.isNumeric(addressLine1EditText.getText().toString())) {
            addressLine1EditText.setSelected(true);
            return "Invalid Address Line 1.";
        }
        if (ApplicationUtils.isNumeric(cityOrTownEditText.getText().toString())) {
            cityOrTownEditText.setSelected(true);
            return "Invalid City.";
        }
        return null;
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
    public interface OnAddressProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAddressProfileFragmentInteraction(User user);
    }
}

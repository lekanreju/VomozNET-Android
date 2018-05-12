package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.Saveable;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberRequest;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalProfileFragment.OnPersonalProfileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalProfileFragment extends Fragment implements Saveable {

    private SweetAlertDialog dialog;
    private Realm realm;
    private User user;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMM-d-yyyy", Locale.getDefault());
    private Personal personal;
    private SimpleDateFormat fmt = new SimpleDateFormat("yyyy-mm-dd");
    private TextView emptyView;
    private EditText birthDateEditText;
    private EditText genderEditText;
    private EditText maritalStatusEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText middleNameEditText;
    private EditText primaryEmailEditText;
    private EditText secondaryEmailEditText;
    private EditText weddingDateEditText;
    private EditText homePhoneEditText;
    private int mYear, mMonth, mDay;
    private EditText titleEditText;
    private Personal savedPersonal;
    private DatePickerDialog.OnDateSetListener birthDateListener;
    private DatePickerDialog.OnDateSetListener weddingDateListener;
    private OnPersonalProfileFragmentInteractionListener mListener;
    private LayoutInflater inflater;
    private ViewGroup container;
    public PersonalProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param
     * @return A new instance of fragment PersonalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalProfileFragment newInstance() {
        PersonalProfileFragment fragment = new PersonalProfileFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyPersonalInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonalProfileFragment newInstance(String param1, String param2) {
        PersonalProfileFragment fragment = new PersonalProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        realm = Realm.getDefaultInstance();
        this.inflater = inflater;
        this.container = container;
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);

        Button saveButton = (Button) view.findViewById(R.id.btn_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        titleEditText = (EditText) view.findViewById(R.id.edit_title);
        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        maritalStatusEditText = (EditText) view.findViewById(R.id.edit_marital_status);
        genderEditText = (EditText) view.findViewById(R.id.edit_gender);
        birthDateEditText = (EditText) view.findViewById(R.id.edit_birth_date);

        weddingDateEditText = (EditText) view.findViewById(R.id.edit_wedding_date);
        firstNameEditText = (EditText) view.findViewById(R.id.edit_first_name);
        firstNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        lastNameEditText = (EditText) view.findViewById(R.id.edit_last_name);
        lastNameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });
        middleNameEditText = (EditText) view.findViewById(R.id.edit_middle_name);
        primaryEmailEditText = (EditText) view.findViewById(R.id.edit_primary_email);
        secondaryEmailEditText = (EditText) view.findViewById(R.id.edit_secondary_email);
        homePhoneEditText = (EditText) view.findViewById(R.id.edit_home_phone);
        homePhoneEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
            }
        });

        enableTextFields(false);

        firstNameEditText.setInputType(0);
        firstNameEditText.setFocusable(false);

        middleNameEditText.setInputType(0);
        middleNameEditText.setFocusable(false);

        titleEditText.setInputType(0);
        titleEditText.setFocusable(false);
        titleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
                final CharSequence[] items = {
                        "Mr", "Mrs", "Ms", "Dr", "Prof", "Rev", "Pastor"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Title");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        titleEditText.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        maritalStatusEditText.setInputType(0);
        maritalStatusEditText.setFocusable(false);
        maritalStatusEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
                final CharSequence[] items = {
                        "Married", "Single", "Divorced", "Widow/Widower", "Other", "Undeclared"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Marital Status");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        maritalStatusEditText.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        genderEditText.setInputType(0);
        genderEditText.setFocusable(false);
        genderEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableTextFields(true);
                final CharSequence[] items = {
                        "Male", "Female", "Unknown"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Genger");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        genderEditText.setText(items[item]);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        birthDateEditText.setInputType(0);
        birthDateEditText.setFocusable(false);
        weddingDateEditText.setInputType(0);
        weddingDateEditText.setFocusable(false);


        getInfo();
        return view;
    }

    public void getInfo() {
        View view = inflater.inflate(R.layout.fragment_personal_profile, container, false);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        final GetMemberRequest getMemberRequest = new GetMemberRequest();
        realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        String phone = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone());
        String password = config.getPassword();
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
            Call<GetMemberResponse> call = apiService.getMemberPersonalInfo(getMemberRequest, "", ApplicationUtils.APP_ID);
            final TextView emptyView = (TextView) view.findViewById(R.id.empty_view);
            final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
            scrollView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<GetMemberResponse>() {
                @Override
                public void onResponse(Call<GetMemberResponse> call, Response<GetMemberResponse> response) {
                    if (response.isSuccessful()) {
                        if (null != response.body() && null != response.body().getResponseData() && null != response.body().getResponseData().getPersonal()) {
                            scrollView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                            personal = response.body().getResponseData().getPersonal();
                            savedPersonal = personal;
                            if (personal != null) {
                                lastNameEditText.setText(personal.getLastName());
                                homePhoneEditText.setText(personal.getHomePhone());
                                firstNameEditText.setText(personal.getFirstName());
                                middleNameEditText.setText(personal.getMiddleName());
                                primaryEmailEditText.setText(personal.getPrimaryEmail());
                                secondaryEmailEditText.setText(personal.getSecondaryEmail());
                                if (null != personal.getMaritalStatus()) {
                                    if (personal.getMaritalStatus().equalsIgnoreCase("U"))
                                        maritalStatusEditText.setText("Undeclared");
                                    else if (personal.getMaritalStatus().equalsIgnoreCase("M"))
                                        maritalStatusEditText.setText("Married");
                                    else if (personal.getMaritalStatus().equalsIgnoreCase("D"))
                                        maritalStatusEditText.setText("Divorced");
                                    else if (personal.getMaritalStatus().equalsIgnoreCase("W"))
                                        maritalStatusEditText.setText("Widow/Widower");
                                    else if (personal.getMaritalStatus().equalsIgnoreCase("S"))
                                        maritalStatusEditText.setText("Single");
                                    else if (personal.getMaritalStatus().equalsIgnoreCase("O"))
                                        maritalStatusEditText.setText("Others");
                                }
                                if (null != personal.getGender()) {
                                    if (personal.getGender().equalsIgnoreCase("M"))
                                        genderEditText.setText("Male");
                                    else if (personal.getMaritalStatus().equalsIgnoreCase("F"))
                                        genderEditText.setText("Female");
                                    else
                                        genderEditText.setText("Unknown");
                                }
                                Date defaultDate = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    defaultDate = sdf.parse("01/01/1920");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (personal.getBirthDate() != null && personal.getBirthDate().before(defaultDate))
                                    personal.setBirthDate(defaultDate);
                                if (personal.getWeddingDate() != null && personal.getWeddingDate().before(defaultDate))
                                    personal.setWeddingDate(defaultDate);
                                birthDateEditText.setText((personal.getBirthDate() != null ? formatter.format(personal.getBirthDate()) : ApplicationUtils.BLANK_DATE));
                                weddingDateEditText.setText((personal.getWeddingDate() != null ? formatter.format(personal.getWeddingDate()) : ApplicationUtils.BLANK_DATE));
                                titleEditText.setText((personal.getTitle() != null ? personal.getTitle().toString() : ""));

                                birthDateEditText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        enableTextFields(true);
                                        final Calendar c = Calendar.getInstance();
                                        mYear = c.get(Calendar.YEAR);
                                        mMonth = c.get(Calendar.MONTH);
                                        mDay = c.get(Calendar.DAY_OF_MONTH);
                                        if (!birthDateEditText.getText().toString().equals("0") && !birthDateEditText.getText().toString().equals("")) {
                                            Calendar cc = Calendar.getInstance();
                                            try {
                                                cc.setTime(formatter.parse(birthDateEditText.getText().toString()));
                                                mYear = cc.get(Calendar.YEAR);
                                                mMonth = cc.get(Calendar.MONTH);
                                                mDay = cc.get(Calendar.DAY_OF_MONTH);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (null != personal.getBirthDate()) {
                                            Calendar cc = Calendar.getInstance();
                                            cc.setTime(personal.getBirthDate());
                                            mYear = cc.get(Calendar.YEAR);
                                            mMonth = cc.get(Calendar.MONTH);
                                            mDay = cc.get(Calendar.DAY_OF_MONTH);
                                        }
                                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                                    new DatePickerDialog.OnDateSetListener() {
                                                        @Override
                                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                            birthDateEditText.setText(ApplicationUtils.getMonthName(monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                                                            //fab.requestFocus();
                                                        }
                                                    }, mYear, mMonth, mDay);
                                            datePickerDialog.show();

                                    }
                                });

                                weddingDateEditText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        enableTextFields(true);
                                        final Calendar c = Calendar.getInstance();
                                        mYear = c.get(Calendar.YEAR);
                                        mMonth = c.get(Calendar.MONTH);
                                        mDay = c.get(Calendar.DAY_OF_MONTH);
                                        if (!weddingDateEditText.getText().toString().equals("0") && !weddingDateEditText.getText().toString().equals("")) {
                                            Calendar cc = Calendar.getInstance();
                                            try {
                                                cc.setTime(formatter.parse(weddingDateEditText.getText().toString()));
                                                mYear = cc.get(Calendar.YEAR);
                                                mMonth = cc.get(Calendar.MONTH);
                                                mDay = cc.get(Calendar.DAY_OF_MONTH);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                        } else if (null != personal.getWeddingDate()) {
                                            Calendar cc = Calendar.getInstance();
                                            cc.setTime(personal.getBirthDate());
                                            mYear = cc.get(Calendar.YEAR);
                                            mMonth = cc.get(Calendar.MONTH);
                                            mDay = cc.get(Calendar.DAY_OF_MONTH);
                                        }
                                         DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                                    new DatePickerDialog.OnDateSetListener() {
                                                        @Override
                                                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                                            weddingDateEditText.setText(ApplicationUtils.getMonthName(monthOfYear + 1) + "-" + dayOfMonth + "-" + year);
                                                            //fab.requestFocus();
                                                        }
                                                    }, mYear, mMonth, mDay);
                                            datePickerDialog.show();

                                    }
                                });
                            }
                        }
                        dialog.dismiss();
//                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                        dialog.setContentText("Your profile info was retrieved successfully");
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
                            dialog.setContentText("Your profile info was NOT retrieved successfully \n" + baseServiceResponse.getMessage().getDescription() + "\n" +
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
                    dialog.setContentText("Your profile info was NOT retrieved successfully");
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
    @Override
    public void enableTextFields(boolean value) {
        //titleEditText.setEnabled(value);
        titleEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //maritalStatusEditText.setEnabled(value);
        maritalStatusEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //genderEditText.setEnabled(value);
        genderEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //birthDateEditText.setEnabled(value);
        birthDateEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //weddingDateEditText.setEnabled(value);
        weddingDateEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //firstNameEditText.setEnabled(value);
        firstNameEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //lastNameEditText.setEnabled(value);
        lastNameEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //middleNameEditText.setEnabled(value);
        middleNameEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //primaryEmailEditText.setEnabled(value);
        primaryEmailEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //secondaryEmailEditText.setEnabled(value);
        secondaryEmailEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
        //homePhoneEditText.setEnabled(value);
        homePhoneEditText.setTextColor(value? getActivity().getResources().getColor(R.color.white): getActivity().getResources().getColor(R.color.dark_gray));
    }

    public String validate() {
        if (!ApplicationUtils.isValidName(firstNameEditText.getText().toString())) {
            firstNameEditText.setSelected(true);
            return "FirstName is invalid. Must be at least 2 charaters";
        }
        if (ApplicationUtils.isNumeric(lastNameEditText.getText().toString())) {
            lastNameEditText.setSelected(true);
            return "FirstName is invalid.";
        }
        if (!ApplicationUtils.isValidName(lastNameEditText.getText().toString())) {
            lastNameEditText.setSelected(true);
            return "LastName is invalid. Must be at least 2 charaters";
        }
        if (ApplicationUtils.isNumeric(lastNameEditText.getText().toString())) {
            lastNameEditText.setSelected(true);
            return "LastName is invalid. ";
        }
        if (primaryEmailEditText.getText().toString().length() > 0 && !ApplicationUtils.isValidEmailAddress(primaryEmailEditText.getText().toString())) {
            primaryEmailEditText.setSelected(true);
            return "Primary email is invalid.";
        }
        if (secondaryEmailEditText.getText().toString().length() > 0 && !ApplicationUtils.isValidEmailAddress(secondaryEmailEditText.getText().toString())) {
            secondaryEmailEditText.setSelected(true);
            return "Secondary email is invalid.";
        }
        if (homePhoneEditText.getText().toString().length() > 0 && !ApplicationUtils.isValidPhoneNumber(homePhoneEditText.getText().toString())) {
            homePhoneEditText.setSelected(true);
            return "Home phone is invalid.";
        }
        SimpleDateFormat fmt = new SimpleDateFormat("MMM-d-yyyy");
        try {
            Date birthDate = fmt.parse(birthDateEditText.getText().toString());
            if (null == birthDate) {
                birthDateEditText.setSelected(true);
                return "Please choose a valid date";
            } else if (birthDate.after(new Date())) {
                birthDateEditText.setSelected(true);
                return "Birthdate cannot be in the future";
            }
        } catch (ParseException e) {

        }
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPersonalProfileFragmentInteractionListener) {
            mListener = (OnPersonalProfileFragmentInteractionListener) context;
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
        enableTextFields(true);
        String validationMessage = validate();
        if (null != validationMessage) {
            SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText(validationMessage);
            dialog.show();
            return false;
        } else {
            personal.setFirstName(firstNameEditText.getText().toString());
            personal.setLastName(lastNameEditText.getText().toString());
            personal.setMiddleName(middleNameEditText.getText().toString());
            personal.setPrimaryEmail(primaryEmailEditText.getText().toString());
            personal.setSecondaryEmail(secondaryEmailEditText.getText().toString());
            personal.setHomePhone(homePhoneEditText.getText().toString());
            personal.setTitle(titleEditText.getText().toString());
            try {
                String bdate = birthDateEditText.getText().toString();
                personal.setBirthDate(formatter.parse(bdate));

            } catch (Exception e) {
            }
            try {
                personal.setWeddingDate(formatter.parse(weddingDateEditText.getText().toString()));
            } catch (Exception e) {
            }

            if (genderEditText.getText().toString().equalsIgnoreCase("Male"))
                personal.setGender("M");
            else if (genderEditText.getText().toString().equalsIgnoreCase("Female"))
                personal.setGender("F");
            else
                personal.setGender("N");

            if (maritalStatusEditText.getText().toString().equalsIgnoreCase("Married"))
                personal.setGender("M");
            else if (genderEditText.getText().toString().equalsIgnoreCase("Single"))
                personal.setGender("S");
            else if (genderEditText.getText().toString().equalsIgnoreCase("Undeclared"))
                personal.setGender("U");
            else if (genderEditText.getText().toString().equalsIgnoreCase("Divorced"))
                personal.setGender("D");
            else if (genderEditText.getText().toString().equalsIgnoreCase("Widow/Widower"))
                personal.setGender("W");
            else
                personal.setGender("O");

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            realm = Realm.getDefaultInstance();
            DonationCenter homeCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            if (null != homeCenter) {
                personal.setCenterCardId(homeCenter.getCardId());
                String authToken = "";//PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(SimpleLoginActivity.AUTH_TOKEN_LABEL, null);
                dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                        .setTitleText(getResources().getString(R.string.app_name))
                        .setContentText("Saving...");
                dialog.show();
                Call<BaseServiceResponse> call = apiService.updatePersonalInfo(personal, authToken, "");
                call.enqueue(new Callback<BaseServiceResponse>() {
                    @Override
                    public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                        Log.d(getClass().getSimpleName(), ">>>>>> SUCCESS: " + "PersonalInfo Update Completed");
                        if (response.isSuccessful()) {
                            enableTextFields(false);
                            realm = Realm.getDefaultInstance();
                            User user = realm.where(User.class).findFirst();
                            if (null != user) {
                                realm.beginTransaction();
                                user.setFirstName(personal.getFirstName());
                                user.setLastName(personal.getLastName());
                                user.setEmail(personal.getPrimaryEmail());
                                realm.copyToRealmOrUpdate(user);
                                realm.commitTransaction();
                                mListener.onPersonalProfileFragmentInteraction(user);
                            }

                            dialog.setContentText("Your personal information was saved successfully");
                            dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.dismiss();
                                }
                            });
                            if (mListener != null) {
                                mListener.onPersonalProfileFragmentInteraction(user);
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
    public interface OnPersonalProfileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPersonalProfileFragmentInteraction(User user);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            //blah
        }
    }
}

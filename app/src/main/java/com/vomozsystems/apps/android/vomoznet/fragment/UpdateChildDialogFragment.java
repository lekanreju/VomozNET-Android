package com.vomozsystems.apps.android.vomoznet.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.MyChurchActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Child;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leksrej on 5/11/18.
 */

public class UpdateChildDialogFragment extends DialogFragment {

    private View view;
    private Child child;
    private int mYear, mMonth, mDay;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMM-d-yyyy", Locale.getDefault());
    private KidFragment kidFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        kidFragment.onResume();
    }

    public static UpdateChildDialogFragment newInstance(Child child, KidFragment kidFragment) {
        UpdateChildDialogFragment fragment = new UpdateChildDialogFragment();
        fragment.child = child;
        fragment.kidFragment = kidFragment;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.add_update_child_dialog, container, false);
        final Realm realm = Realm.getDefaultInstance();
        final DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        TextView organizationNameTextView = view.findViewById(R.id.organization_name);
        TextView organizationPhoneTextView = view.findViewById(R.id.organization_telephone);
        ImageView imageView = view.findViewById(R.id.organization_image);
        if (null != donationCenter && null != donationCenter.getName()) {
            String name = donationCenter.getName();
            if (donationCenter.getName().length() > 20)
                name = donationCenter.getShortName();
            organizationNameTextView.setText(name);
            String donationCenterLogoUrl = ApiClient.BASE_IMAGE_URL + "/" + donationCenter.getLogoName();
            if (null != donationCenterLogoUrl && !donationCenter.getLogoName().equalsIgnoreCase("0")) {
                Picasso.with(getActivity())
                        .load(donationCenterLogoUrl)
                        .into(imageView);
            }

            organizationNameTextView.setText(donationCenter.getName());

        }

        final EditText firstNameEditText = view.findViewById(R.id.kid_first_name);
        final EditText lastNameEditText = view.findViewById(R.id.kid_last_name);
        final EditText middleNameEditText = view.findViewById(R.id.kid_middle_name);
        final EditText preferredNameEditText = view.findViewById(R.id.kid_preferred_name);
        final EditText birthDateEditText = view.findViewById(R.id.kid_birth_date);
        final EditText ageGroupEditText = view.findViewById(R.id.kid_age_group);
        final EditText schoolNameEditText = view.findViewById(R.id.kid_school_name);
        final EditText gradeEditText = view.findViewById(R.id.kid_grade);
        final EditText genderEditText = view.findViewById(R.id.kid_gender);
        final EditText suffixEditText = view.findViewById(R.id.kid_suffix);

        birthDateEditText.setInputType(0);
        birthDateEditText.setFocusable(false);

        genderEditText.setInputType(0);
        genderEditText.setFocusable(false);

        genderEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = {
                        "Female", "Male"
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Gender");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        genderEditText.setText(items[item].toString().substring(0,1));
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        birthDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                } else if (null != child && null != child.getBirthDate()) {
                    Calendar cc = Calendar.getInstance();
                    cc.setTimeInMillis(Long.valueOf(child.getBirthDate()));
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
        if(child != null) {
            firstNameEditText.setText(child.getFirstName());
            lastNameEditText.setText(child.getLastName());
            middleNameEditText.setText(child.getMiddleName());
            preferredNameEditText.setText(child.getPreferredName());
            ageGroupEditText.setText(child.getAgeGroup());
            schoolNameEditText.setText(child.getSchoolName());
            gradeEditText.setText(child.getGrade());
            if(child.getBirthDate() != null && child.getBirthDate().equalsIgnoreCase("0")) {
                Calendar cc = Calendar.getInstance();
                cc.setTimeInMillis(Long.valueOf(child.getBirthDate()));
                birthDateEditText.setText(formatter.format(cc.getTime()));
            }
        }

        Button cancelButton = (Button) view.findViewById(R.id.kid_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button saveButton = (Button) view.findViewById(R.id.kid_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(child == null)
                    child = new Child();

                Config config = realm.where(Config.class).findFirst();
                child.setFirstName(firstNameEditText.getText().toString());
                child.setLastName(lastNameEditText.getText().toString());
                child.setMiddleName(middleNameEditText.getText().toString());
                child.setPreferredName(preferredNameEditText.getText().toString());
                child.setAgeGroup(ageGroupEditText.getText().toString());
                child.setSchoolName(schoolNameEditText.getText().toString());
                child.setGrade(gradeEditText.getText().toString());
                child.setDonationCenterCardId(donationCenter.getCardId());
                child.setParent1TexterCardId(donationCenter.getTexterCardId());
                child.setPassword(config.getPassword());
                child.setCallerId(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
                child.setMerchantIdCode(donationCenter.getMerchantIdCode());
                child.setGender(genderEditText.getText().toString());
                child.setSuffix(suffixEditText.getText().toString());

                try {
                    Calendar cc = Calendar.getInstance();
                    cc.setTime(formatter.parse(birthDateEditText.getText().toString()));
                    child.setBirthDate(String.valueOf(cc.getTimeInMillis()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (child.getFirstName().length()==0) {
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("First name is expected")
                            .setTitleText(getResources().getString(R.string.app_name));
                    sweetAlertDialog.show();

                } else if(child.getLastName().length()==0) {
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Last name is expected")
                            .setTitleText(getResources().getString(R.string.app_name));
                    sweetAlertDialog.show();
                } else if(child.getBirthDate().length()==0) {
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setContentText("Birth date is expected")
                            .setTitleText(getResources().getString(R.string.app_name));
                    sweetAlertDialog.show();
                } else {
                    final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
                            .setContentText("")
                            .setTitleText(getResources().getString(R.string.org_filter));
                    sweetAlertDialog.show();

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<BaseServiceResponse> call = null;

                    if (child.getId() == null)
                        call = apiInterface.createChild(child, "", "");
                    else
                        call = apiInterface.updateChild(child, "", "");
                    call.enqueue(new Callback<BaseServiceResponse>() {
                        @Override
                        public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                            if (response.isSuccessful()) {
                                sweetAlertDialog.setContentText("Child information saved successfully");
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                sweetAlertDialog.setConfirmText("OK");
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                            } else {
                                sweetAlertDialog.setContentText("Child information NOT saved successfully");
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setConfirmText("OK");
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                            sweetAlertDialog.setContentText("Network Failure");
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setConfirmText("OK");
                            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                        }
                    });
                }
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
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 1);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.65);
            dialog.getWindow().setLayout(width, height);
        }
    }
}

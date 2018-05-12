package com.vomozsystems.apps.android.vomoznet.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.LoginActivity;
import com.vomozsystems.apps.android.vomoznet.MyChurchActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Child;
import com.vomozsystems.apps.android.vomoznet.entity.ChildAttendance;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.fragment.ChangeDonationCenterDialogFrament;
import com.vomozsystems.apps.android.vomoznet.fragment.KidFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.KidFragment.OnKidListFragmentInteractionListener;
import com.vomozsystems.apps.android.vomoznet.fragment.UpdateChildDialogFragment;
import com.vomozsystems.apps.android.vomoznet.fragment.dummy.DummyContent.DummyItem;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.ServiceGenerator;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.exit;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link KidFragment.OnKidListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyKidRecyclerViewAdapter extends RecyclerView.Adapter<MyKidRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private final List<Child> mValues;
    private final KidFragment.OnKidListFragmentInteractionListener mListener;
    private KidFragment kidFragment;

    public MyKidRecyclerViewAdapter(Context context, List<Child> items, KidFragment.OnKidListFragmentInteractionListener listener, KidFragment kidFragment) {
        mValues = items;
        mListener = listener;
        this.context = context;
        this.kidFragment = kidFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_kid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SimpleDateFormat df = new SimpleDateFormat("MMM, dd yyyy");
        holder.mItem = mValues.get(position);
        holder.mKidName.setText(mValues.get(position).getFirstName().equalsIgnoreCase("0")?"Not Available": mValues.get(position).getFirstName());
        holder.mKidGender.setText(mValues.get(position).getGender().equalsIgnoreCase("0")?"Not Available": mValues.get(position).getGender());
        holder.mKidSchool.setText(mValues.get(position).getSchoolName().equalsIgnoreCase("0")?"Not Available": mValues.get(position).getSchoolName());
        holder.mKidGrade.setText(mValues.get(position).getGrade().equalsIgnoreCase("0")?"Not Available": mValues.get(position).getGrade());
        holder.mKidStatus.setText(holder.mItem.getCheckInStatus().equals("0")?"CHECKED_OUT":"CHECKED_IN");

        Realm realm = Realm.getDefaultInstance();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        User user = realm.where(User.class).findFirst();
        Config config = realm.where(Config.class).findFirst();
        holder.mItem.setDonationCenterCardId(donationCenter.getCardId());
        holder.mItem.setPassword(config.getPassword());
        holder.mItem.setCallerId(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
        holder.mItem.setParent1TexterCardId(donationCenter.getTexterCardId());
        holder.mKidDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(kidFragment.getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setContentText("Dou you want to delete this child information?")
                        .setTitleText(kidFragment.getActivity().getResources().getString(R.string.app_name))
                        .setConfirmText("Delete")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(final SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                                ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                                Call<BaseServiceResponse> call = apiInterface.deleteChild(holder.mItem, "", "");
                                call.enqueue(new Callback<BaseServiceResponse>() {
                                    @Override
                                    public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                                        if(response.isSuccessful()) {
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                            sweetAlertDialog.setConfirmText("OK");
                                            sweetAlertDialog.setContentText("DELETED");
                                            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    sDialog.dismiss();
                                                }
                                            });
                                            kidFragment.onResume();
                                            notifyDataSetChanged();
                                        }else {
                                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                            sweetAlertDialog.setConfirmText("OK");
                                            sweetAlertDialog.setContentText("Request was NOT completed successfully.");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setConfirmText("OK");
                                        sweetAlertDialog.setContentText("Request was NOT completed successfully.");
                                    }
                                });

                            }
                        }).show();
            }
        });

        holder.mKidCheckInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.mItem.getCheckInStatus().equals("0")) {
                    new SweetAlertDialog(kidFragment.getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Check in " + holder.mItem.getFirstName() + "?")
                            .setTitleText(kidFragment.getActivity().getString(R.string.app_name))
                            .setConfirmText("Yes")
                            .setCancelText("No")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    checkIn(holder, sweetAlertDialog);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                }else {
                    new SweetAlertDialog(kidFragment.getActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setContentText("Check out " + holder.mItem.getFirstName() + "?")
                            .setTitleText(kidFragment.getActivity().getString(R.string.app_name))
                            .setConfirmText("Yes")
                            .setCancelText("No")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    checkOut(holder, sweetAlertDialog);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            }
        });

        holder.mKidCheckInButton.setBackgroundColor(context.getResources().getColor(holder.mItem.getCheckInStatus().equals("0")?R.color.dark_gray:R.color.green));
        holder.mKidCheckInButton.setText(holder.mItem.getCheckInStatus().equals("0")?"CHECK IN":"CHECK OUT");
        holder.mKidPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kidFragment.selectImage(holder.mKidPic1, "childpic1");
            }
        });

        try {
            Timestamp stamp = new Timestamp(Long.valueOf(mValues.get(position).getBirthDate()));
            Date birthDate = new Date(stamp.getTime());
            holder.mKidAge.setText(getAge(birthDate)+"");
        }catch(Exception e){
            holder.mKidAge.setText("No Date");
        }
        try {
            String texterCardString = Long.toString(holder.mItem.getParent1TexterCardId());
            if (null != holder.mItem  && null != holder.mItem.getPic1() && !holder.mItem.getPic1().equals("0")) {
                String url = ServiceGenerator.PROFILE_PICS_BASE_URL + "/" + holder.mItem.getPic1().charAt(0) + "/" + texterCardString.charAt(0) + "/" + holder.mItem.getPic1();
                try {
                    Picasso.with(context)
                            .load(url)
                            .resize(100, 100)
                            .centerCrop().noPlaceholder()
                            .placeholder(R.mipmap.ic_profile)
                            .into(holder.mKidPic1);
                } catch (Exception e) {

                }
            }
        }catch (Exception e) {

        }

        holder.mKidUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateChildDialogFragment frament = UpdateChildDialogFragment.newInstance(holder.mItem, kidFragment);
                frament.show(kidFragment.getActivity().getSupportFragmentManager(), "");
            }
        });


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onKidListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    private void checkIn(final ViewHolder holder, final SweetAlertDialog sweetAlertDialog) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        Config config = realm.where(Config.class).findFirst();
        String phone = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone());
        String password = config.getPassword();
        ChildAttendance childAttendance = new ChildAttendance();
        childAttendance.setCallerId(phone);
        childAttendance.setPassword(password);
        childAttendance.setCheckInByTexterCardId(Long.valueOf(user.getTexterCardId()));
        childAttendance.setChildUniqueId(holder.mItem.getChildUniqueId());
        childAttendance.setDonationCenterCardId(donationCenter.getCardId());
        childAttendance.setMerchantIdCode(donationCenter.getMerchantIdCode());

        Call<BaseServiceResponse> call = apiInterface.checkInChild(childAttendance, "", "");
        call.enqueue(new Callback<BaseServiceResponse>() {
            @Override
            public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                if(response.isSuccessful()) {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setContentText("SUCCESS");
                    sweetAlertDialog.setConfirmText("OK");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
                    holder.mItem.setCheckInStatus("1");
                    holder.mKidStatus.setText(holder.mItem.getCheckInStatus().equals("0")?"CHECKED_OUT":"CHECKED_IN");
                    holder.mKidCheckInButton.setBackgroundColor(context.getResources().getColor(holder.mItem.getCheckInStatus().equals("0")?R.color.dark_gray:R.color.green));
                    holder.mKidCheckInButton.setText(holder.mItem.getCheckInStatus().equals("0")?"CHECK IN":"CHECK OUT");
                }else {
                    sweetAlertDialog.dismiss();
                    SweetAlertDialog sDialog = new SweetAlertDialog(kidFragment.getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                    sDialog.setContentText("SUCCESS");
                    sDialog.setCancelable(false);
                    sDialog.setCancelClickListener(null);
                    sDialog.setContentText("CheckIn was not completed successfully");
                    sDialog.setCancelable(false);
                    sDialog.setCancelClickListener(null);
                }
            }

            @Override
            public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setContentText("Network Failure - CheckIn was not completed successfully");
                sweetAlertDialog.setCancelable(false);
            }
        });
    }

    private void checkOut(final ViewHolder holder, final SweetAlertDialog sweetAlertDialog){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        Realm realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        Config config = realm.where(Config.class).findFirst();
        String phone = ApplicationUtils.cleanPhoneNumber(config.getMobilePhone());
        String password = config.getPassword();
        ChildAttendance childAttendance = new ChildAttendance();
        childAttendance.setCallerId(phone);
        childAttendance.setPassword(password);
        childAttendance.setCheckOutByTexterCardId(Long.valueOf(user.getTexterCardId()));
        childAttendance.setChildUniqueId(holder.mItem.getChildUniqueId());
        childAttendance.setDonationCenterCardId(donationCenter.getCardId());
        childAttendance.setMerchantIdCode(donationCenter.getMerchantIdCode());

        Call<BaseServiceResponse> call = apiInterface.checkOutChild(childAttendance, "", "");
        call.enqueue(new Callback<BaseServiceResponse>() {
            @Override
            public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                if(response.isSuccessful()) {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setContentText("SUCCESS");
                    sweetAlertDialog.setConfirmText("OK");
                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    });
                    holder.mItem.setCheckInStatus("0");
                    holder.mKidStatus.setText(holder.mItem.getCheckInStatus().equals("0")?"CHECKED_OUT":"CHECKED_IN");
                    holder.mKidCheckInButton.setBackgroundColor(context.getResources().getColor(holder.mItem.getCheckInStatus().equals("0")?R.color.dark_gray:R.color.green));
                    holder.mKidCheckInButton.setText(holder.mItem.getCheckInStatus().equals("0")?"CHECK IN":"CHECK OUT");
                }else {
                    sweetAlertDialog.dismiss();
                    SweetAlertDialog sDialog = new SweetAlertDialog(kidFragment.getActivity(), SweetAlertDialog.SUCCESS_TYPE);
                    sDialog.setContentText("SUCCESS");
                    sDialog.setCancelable(false);
                    sDialog.setCancelClickListener(null);
                    sDialog.setContentText("CheckOut was not completed successfully");
                    sDialog.setCancelable(false);
                    sDialog.setCancelClickListener(null);
                }
            }

            @Override
            public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                sweetAlertDialog.setContentText("Network Failure - CheckOut was not completed successfully");
                sweetAlertDialog.setCancelable(false);
            }
        });
    }

    public static int getAge(Date dateOfBirth) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);
        if (birthDate.after(today)) {
            throw new IllegalArgumentException("You don't exist yet");
        }
        int todayYear = today.get(Calendar.YEAR);
        int birthDateYear = birthDate.get(Calendar.YEAR);
        int todayDayOfYear = today.get(Calendar.DAY_OF_YEAR);
        int birthDateDayOfYear = birthDate.get(Calendar.DAY_OF_YEAR);
        int todayMonth = today.get(Calendar.MONTH);
        int birthDateMonth = birthDate.get(Calendar.MONTH);
        int todayDayOfMonth = today.get(Calendar.DAY_OF_MONTH);
        int birthDateDayOfMonth = birthDate.get(Calendar.DAY_OF_MONTH);
        int age = todayYear - birthDateYear;

        // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year
        if ((birthDateDayOfYear - todayDayOfYear > 3) || (birthDateMonth > todayMonth)){
            age--;

            // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
        } else if ((birthDateMonth == todayMonth) && (birthDateDayOfMonth > todayDayOfMonth)){
            age--;
        }
        return age;
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mKidName;
        public final TextView mKidAge;
        public final TextView mKidGender;
        public final TextView mKidSchool;
        public final TextView mKidGrade;
        public final TextView mKidStatus;
        public final ImageView mKidPic1;
        public final Button mKidCheckInButton;
        public final Button mKidUpdateButton;
        public final Button mKidDeleteButton;
        public Child mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mKidName = (TextView) view.findViewById(R.id.kid_name);
            mKidAge= (TextView) view.findViewById(R.id.kid_age);
            mKidGender = (TextView) view.findViewById(R.id.kid_gender);
            mKidGrade = (TextView) view.findViewById(R.id.kid_grade);
            mKidSchool = (TextView) view.findViewById(R.id.kid_school);
            mKidStatus = (TextView) view.findViewById(R.id.kid_status);
            mKidPic1 = (ImageView) view.findViewById(R.id.kid_pic_1);
            mKidCheckInButton = (Button) view.findViewById(R.id.kid_checkin_button);
            mKidUpdateButton = (Button) view.findViewById(R.id.kid_update_button);
            mKidDeleteButton = (Button) view.findViewById(R.id.kid_delete_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mKidName.getText() + "'";
        }
    }
}

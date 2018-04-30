package com.vomozsystems.apps.android.vomoznet.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Config;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.fragment.ChangeDonationCenterDialogFrament;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.ChangeDonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.MakeDonationInterface;
import com.vomozsystems.apps.android.vomoznet.service.MemberInfoRequest;
import com.vomozsystems.apps.android.vomoznet.service.UserLoginResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by leksrej on 3/19/18.
 */

public class MyOrganizationRecyclerViewAdapter extends RecyclerView.Adapter<MyOrganizationRecyclerViewAdapter.ViewHolder> {

    private List<DonationCenter> mValues;
    private Activity activity;
    private ChangeDonationCenterDialogFrament mListener;

    public List<DonationCenter> getmValues() {
        return mValues;
    }

    public void setmValues(List<DonationCenter> mValues) {
        this.mValues = mValues;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public MyOrganizationRecyclerViewAdapter(Activity activity, List<DonationCenter> items, ChangeDonationCenterDialogFrament mListener) {
        mValues = items;
        this.activity = activity;
        this.mListener = mListener;
    }

    @Override
    public MyOrganizationRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donation_center_row, parent, false);
        return new MyOrganizationRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyOrganizationRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(holder.mItem.getName());
        holder.mAddressView.setText(holder.mItem.getAddress());

        String donationCenterLogoUrl = ApiClient.BASE_IMAGE_URL + "/" + mValues.get(position).getLogoName();
        if (null != donationCenterLogoUrl && !mValues.get(position).getLogoName().equalsIgnoreCase("0")) {
           try {
               Picasso.with(activity)
                       .load(donationCenterLogoUrl)
                       .into(holder.mImageView, new com.squareup.picasso.Callback() {
                           @Override
                           public void onSuccess() {
                           }

                           @Override
                           public void onError() {
                           }
                       });
           }catch(Exception e){

           }
        }

        holder.mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrganization(holder.mView, position);
            }
        });

        holder.mAddressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrganization(holder.mView,position);
            }
        });

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrganization(holder.mView,position);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrganization(holder.mView,position);
            }
        });
    }

    private void changeOrganization(final View view, final int position) {
        final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getActivity().getResources().getString(R.string.app_name))
                .setContentText("Change default organization to " + mValues.get(position).getShortName() + "?")
                .setConfirmText("Change")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        changeOrganization(view, mValues.get(position), sDialog);

                    }
                })
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                });
        sweetAlertDialog.show();
    }
    @Override
    public int getItemCount() {
        if (mValues != null)
            return mValues.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mAddressView;
        public final ImageView mImageView;
        public DonationCenter mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.donation_center_name);
            mAddressView = (TextView) view.findViewById(R.id.donation_center_address);
            mImageView = (ImageView) view.findViewById(R.id.donation_center_logo);

        }
    }

    private void updateUserInfo(final DonationCenter donationCenter) {
        final Realm realm = Realm.getDefaultInstance();
        final Config config = realm.where(Config.class).findFirst();
        if (config != null) {
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<DonationCenterResponse> call = apiService.getMemberDonationCenters(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()), activity.getResources().getString(R.string.org_filter),"", ApplicationUtils.APP_ID);
            call.enqueue(new Callback<DonationCenterResponse>() {
                @Override
                public void onResponse(Call<DonationCenterResponse> call, Response<DonationCenterResponse> response) {
                    if (response.isSuccessful()) {
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(response.body().getResponseData());
                        List<DonationCenter> donationCenters = realm.where(DonationCenter.class).findAll();
                        for(DonationCenter donationCenter1: donationCenters) {
                            if(!donationCenter.getCardId().equals(donationCenter1.getCardId()))
                             donationCenter1.setHomeDonationCenter(false);
                            else
                                donationCenter1.setHomeDonationCenter(true);
                        }
                        realm.copyToRealmOrUpdate(donationCenters);
                        realm.commitTransaction();

                        DonationCenter center = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
                        if (center != null) {
                            MemberInfoRequest memberInfoRequest = new MemberInfoRequest();
                            memberInfoRequest.setPhoneNumber(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()));
                            memberInfoRequest.setPassword(config.getPassword());
                            memberInfoRequest.setCenterCardId(center.getCardId());
                            Call<UserLoginResponse> call2 = apiService.login(memberInfoRequest, getActivity().getResources().getString(R.string.org_filter), "", ApplicationUtils.APP_ID);
                            call2.enqueue(new Callback<UserLoginResponse>() {
                                @Override
                                public void onResponse(Call<UserLoginResponse> call, Response<UserLoginResponse> response) {
                                    if (response.isSuccessful()) {
                                        realm.beginTransaction();
                                        realm.delete(User.class);
                                        realm.copyToRealmOrUpdate(response.body().getResponseData());
                                        realm.commitTransaction();
                                    }
                                }

                                @Override
                                public void onFailure(Call<UserLoginResponse> call, Throwable t) {
                                    // network failure - cannot load user profile
                                }
                            });

                        }
                    }
                }

                @Override
                public void onFailure(Call<DonationCenterResponse> call, Throwable t) {
                    Log.i(getClass().getSimpleName(), "");

                }
            });
        }
    }
    public void changeOrganization(View view, final DonationCenter newDonationCenter, final SweetAlertDialog sweetAlertDialog) {
        final Realm realm = Realm.getDefaultInstance();
        Config config = realm.where(Config.class).findFirst();
        final Long newDonationCenterCardId = newDonationCenter.getCardId();
        final String newMerchantIdCode = newDonationCenter.getMerchantIdCode();
        final User user = realm.where(User.class).findFirst();
        final String universalAuthToken = user.getAuthToken();
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<DonationCenterResponse> call = apiInterface.getMemberDonationCenters(ApplicationUtils.cleanPhoneNumber(config.getMobilePhone()), activity.getResources().getString(R.string.org_filter), "", "");
        call.enqueue(new Callback<DonationCenterResponse>() {
            @Override
            public void onResponse(Call<DonationCenterResponse> call, Response<DonationCenterResponse> response) {
                if(response.isSuccessful()) {
                    DonationCenter donationCenter = null;
                    for(DonationCenter donationCenter1: response.body().getResponseData()) {
                        String txtId = Long.toString(donationCenter1.getTexterCardId());
                        if(user.getTexterCardId().equalsIgnoreCase(txtId)) {
                            donationCenter = donationCenter1;
                            break;
                        }
                    }
                    final String giverPhoneNumber = user.getMobilePhone();
                    final String giverLastName = user.getLastName();
                    final String giverFirstName = user.getFirstName();
                    final String giverEmail = user.getEmail();

                    MakeDonationInterface apiInterface = ApplicationUtils.getDonationInterface();
                    Call<ChangeDonationCenterResponse> call2 = apiInterface.changeOrganization(
                            universalAuthToken,donationCenter.getCardId(), donationCenter.getMerchantIdCode(),newDonationCenterCardId,newMerchantIdCode,
                            giverPhoneNumber,giverFirstName,giverLastName,giverEmail,"SwitchTheDefaultOrganizationOfThisUser");

                    call2.enqueue(new Callback<ChangeDonationCenterResponse>() {
                        @Override
                        public void onResponse(Call<ChangeDonationCenterResponse> call, Response<ChangeDonationCenterResponse> response) {

                            if(response.isSuccessful()) {
                                ChangeDonationCenterResponse changeDonationCenterResponse = response.body();
                                if(changeDonationCenterResponse.getStatus().equalsIgnoreCase("1")) {
                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    sweetAlertDialog.setContentText("SUCCESS : " + changeDonationCenterResponse.getOtherInformation().getReturnMessage());
                                    sweetAlertDialog.setConfirmText("OK");

                                    updateUserInfo(newDonationCenter);
                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            mListener.dismiss();
                                        }
                                    }).show();

                                } else {

                                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    sweetAlertDialog.setContentText("FAILURE - " + changeDonationCenterResponse.getFaultCode() +":"+changeDonationCenterResponse.getFaultString());
                                    sweetAlertDialog.setConfirmText("OK");
                                    sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                        }
                                    }).show();
                                }
                            }else {
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(activity.getResources().getString(R.string.app_name))
                                        .setContentText("Your organization was not changed successfully")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismiss();
                                            }
                                        }).show();

                                sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                sweetAlertDialog.setContentText("Your organization was not changed successfully");
                                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismiss();
                                    }
                                }).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ChangeDonationCenterResponse> call, Throwable t) {
                            sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            sweetAlertDialog.setContentText("Your organization was not changed successfully");
                            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            }).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<DonationCenterResponse> call, Throwable t) {

            }
        });

    }
}

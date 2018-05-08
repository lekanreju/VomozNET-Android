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
import com.vomozsystems.apps.android.vomoznet.MainActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by leksrej on 4/29/18.
 */

public class SetDefaultDonationCenterRecyclerViewAdapter extends RecyclerView.Adapter<SetDefaultDonationCenterRecyclerViewAdapter.ViewHolder> {

    private List<DonationCenter> mValues;
    private MainActivity activity;

    public List<DonationCenter> getmValues() {
        return mValues;
    }

    public void setmValues(List<DonationCenter> mValues) {
        this.mValues = mValues;
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public SetDefaultDonationCenterRecyclerViewAdapter(MainActivity activity, List<DonationCenter> items) {
        mValues = items;
        this.activity = activity;
    }

    @Override
    public SetDefaultDonationCenterRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.donation_center_row, parent, false);
        return new SetDefaultDonationCenterRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SetDefaultDonationCenterRecyclerViewAdapter.ViewHolder holder, final int position) {
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

//        holder.mNameView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            activity.signUpToADonationCenter(mValues.get(position));
//            }
//        });
//
//        holder.mAddressView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                activity.signUpToADonationCenter(mValues.get(position));
//            }
//        });
//
//        holder.mImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                activity.signUpToADonationCenter(mValues.get(position));
//            }
//        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
//                        .setTitleText(getActivity().getResources().getString(R.string.app_name))
//                        .setContentText("Change default organization to " + mValues.get(position).getShortName() + "?")
//                        .setConfirmText("Change")
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                activity.signUpToADonationCenter(mValues.get(position));
//                                sDialog.setContentText("Done");
//                                sDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                            }
//                        });
//
//                sweetAlertDialog.show();

                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getActivity().getResources().getString(R.string.app_name))
                        .setContentText("Change default organization to " + mValues.get(position).getShortName() + "?")
                        .setConfirmText("Change")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                activity.signUpToADonationCenter(mValues.get(position));
                                sDialog
                                        .setContentText("Done")
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

                            }
                        })
                        .setCancelText("No")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
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


}

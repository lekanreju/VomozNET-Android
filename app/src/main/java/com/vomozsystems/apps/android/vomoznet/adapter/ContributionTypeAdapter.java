package com.vomozsystems.apps.android.vomoznet.adapter;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.ContributionType;
import com.vomozsystems.apps.android.vomoznet.entity.DonationType;
import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;
import com.vomozsystems.apps.android.vomoznet.fragment.DonationAmountDialogFragment;

import java.text.NumberFormat;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;

/**
 * Created by leksrej on 7/12/17.
 */

public class ContributionTypeAdapter extends RecyclerView.Adapter<ContributionTypeAdapter.ViewHolder> {

    NumberFormat format;
    private List<ContributionType> mValues;
    private FragmentActivity activity;
    private PaymentInfo paymentInfo;
    private int selectedOption;
    private TextView totalAmountToContribute;
    double maxm = 10000D;
    double minm = 2D;

    public ContributionTypeAdapter(FragmentActivity activity, TextView totalAmountToContribute, PaymentInfo paymentInfo) {
        Realm realm = Realm.getDefaultInstance();
        mValues = realm.where(ContributionType.class).findAll();
        this.activity = activity;
        this.paymentInfo = paymentInfo;
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setGroupingUsed(true);
        this.totalAmountToContribute = totalAmountToContribute;
    }

    public List<ContributionType> getmValues() {
        return mValues;
    }

    public void setmValues(List<ContributionType> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ContributionTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contribution_type_row, parent, false);
        return new ContributionTypeAdapter.ViewHolder(view);
    }

    public void onBindViewHolder(final ContributionTypeAdapter.ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        String engineType = paymentInfo.getPaymentEngine().get("type");
        if(engineType.equalsIgnoreCase("PAYPAL")) {
            try {
                maxm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paypal_maximum_contribution"));
                minm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paypal_minimum_contribution"));
            }catch(Exception e) {

            }
        }
        else if(engineType.equalsIgnoreCase("PAYSTACK")) {
            try {
                maxm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paystack_maximum_contribution"));
                minm = Double.parseDouble(paymentInfo.getPaymentEngine().get("paystack_minimum_contribution"));
            }catch(Exception e) {

            }
        }
        else if(engineType.equalsIgnoreCase("RAVE")) {
            try {
                maxm = Double.parseDouble(paymentInfo.getPaymentEngine().get("rave_maximum_contribution"));
                minm = Double.parseDouble(paymentInfo.getPaymentEngine().get("rave_minimum_contribution"));
            }catch(Exception e) {

            }
        }

        if(null != holder.mItem.getAmount() && holder.mItem.getAmount()<=maxm && holder.mItem.getAmount()>=minm)
            holder.amountView.setText(format.format(holder.mItem.getAmount()));
        else
            holder.amountView.setText(format.format(0D));

        holder.typeView.setText(holder.mItem.getDescription());
        if (position == 0) {
            holder.deleteView.setVisibility(View.VISIBLE);
            holder.deleteImageView.setVisibility(View.GONE);
        } else {
            holder.deleteView.setVisibility(View.GONE);
            holder.deleteImageView.setVisibility(View.VISIBLE);
        }
        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(activity.getString(R.string.app_name))
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelText("No")
                        .setConfirmText("Delete")
                        .setContentText("Delete this contribution?.")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Realm realm = Realm.getDefaultInstance();
                                List<ContributionType> values = realm.copyFromRealm(mValues);
                                values.remove(position);
                                realm.beginTransaction();
                                realm.delete(ContributionType.class);
                                realm.copyToRealm(values);
                                realm.commitTransaction();
                                mValues = realm.where(ContributionType.class).findAll();
                                sweetAlertDialog.dismissWithAnimation();
                                notifyDataSetChanged();
                                List<ContributionType> contributionTypeList = realm.where(ContributionType.class).findAll();
                                Double total = 0D;
                                for (ContributionType type : contributionTypeList) {
                                    total += type.getAmount();
                                }
                                totalAmountToContribute.setText(format.format(total));
                            }
                        })
                        .show();

            }
        });
        holder.amountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = activity.getSupportFragmentManager();
                DonationAmountDialogFragment donationAmountDialogFragment = DonationAmountDialogFragment.newInstance(holder.amountView, mValues.get(position), totalAmountToContribute, paymentInfo);
                donationAmountDialogFragment.show(fm, "donate-frag");
            }
        });
        holder.typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setup the alert builder
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Choose Contribution Type");
                selectedOption = 0;
                final Realm realm = Realm.getDefaultInstance();
                final List<DonationType> donationTypes = realm.where(DonationType.class).equalTo("donationCenterCardId", paymentInfo.getColorWrapper().getCardId()).findAll();
                // add a radio button list
                String[] animals = new String[donationTypes.size()];
                int i = 0;
                if (null != donationTypes) {
                    for (DonationType donationType : donationTypes) {
                        animals[i] = donationType.getDescription();
                        i++;
                    }
                    int checkedItem = 0; // cow
                    builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user checked an item
                            selectedOption = which;
                        }
                    });

                    // add OK and Cancel buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // user clicked OK
                            try {
                                realm.beginTransaction();
                                mValues.get(position).setDescription(donationTypes.get(selectedOption).getDescription());
                                mValues.get(position).setTypeId(donationTypes.get(selectedOption).getAutoId());
                                realm.copyToRealmOrUpdate(mValues.get(position));
                                realm.commitTransaction();
                                holder.typeView.setText(donationTypes.get(selectedOption).getDescription());
                            }catch (Exception e) {
                                if(realm.isInTransaction()) {
                                    realm.cancelTransaction();
                                }
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", null);

                    // create and show the alert dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    private void hideKeyboard() {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView typeView;
        public final TextView amountView;
        public final ImageView deleteImageView;
        public final View deleteView;
        public ContributionType mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            typeView = (TextView) view.findViewById(R.id.contribution_type_view);
            amountView = (TextView) view.findViewById(R.id.contribution_amount_view);
            deleteView = view.findViewById(R.id.contribution_delete_view);
            deleteImageView = (ImageView) view.findViewById(R.id.contribution_delete_imageview);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "" + "'";
        }
    }
}
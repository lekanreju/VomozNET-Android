package com.vomozsystems.apps.android.vomoznet.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.DonationHistory;
import com.vomozsystems.apps.android.vomoznet.fragment.DonationHistoryFragment;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DonationHistory} and makes a call to the
 * specified {@link DonationHistoryFragment.OnDonationHistoryListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDonationHistoryRecyclerViewAdapter extends RecyclerView.Adapter<MyDonationHistoryRecyclerViewAdapter.ViewHolder> {

    private final DonationHistoryFragment.OnDonationHistoryListFragmentInteractionListener mListener;
    private List<DonationHistory> mValues;
    private Activity activity;

    public MyDonationHistoryRecyclerViewAdapter(Activity activity, List<DonationHistory> items, DonationHistoryFragment.OnDonationHistoryListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    public List<DonationHistory> getmValues() {
        return mValues;
    }

    public void setmValues(List<DonationHistory> mValues) {
        this.mValues = mValues;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_donationhistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setGroupingUsed(true);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd yyyy");
        Date d = mValues.get(position).getDate();
        try {
            holder.mDonationDateView.setText(sdf.format(d));
        } catch (Exception e) {

        }
        try {
            holder.mDonationAmountView.setText(numberFormat.format(mValues.get(position).getAmount()) + " " + mValues.get(position).getCurrency());
        } catch (Exception e) {
            holder.mDonationAmountView.setText(numberFormat.format(0D));
        }

        holder.mDonationTypeView.setText(mValues.get(position).getDonationTypeDescription());
        holder.mDonatedToView.setText(mValues.get(position).getDonationCenterName());
//        if ((position % 2) == 0) {
//            holder.mView.setBackgroundColor(activity.getResources().getColor(R.color.darker_gray));
//        } else {
//            holder.mView.setBackgroundColor(activity.getResources().getColor(R.color.colorPrimary));
//        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onDonationHistoryListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null != mValues)
            return mValues.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDonationAmountView;
        public final TextView mDonationDateView;
        public final TextView mDonationTypeView;
        public final TextView mDonatedToView;
        public DonationHistory mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDonationAmountView = (TextView) view.findViewById(R.id.donation_amount);
            mDonationDateView = (TextView) view.findViewById(R.id.donation_date);
            mDonationTypeView = (TextView) view.findViewById(R.id.donation_type);
            mDonatedToView = (TextView) view.findViewById(R.id.donated_to);
        }

        @Override
        public String toString() {
            return null;
        }
    }
}

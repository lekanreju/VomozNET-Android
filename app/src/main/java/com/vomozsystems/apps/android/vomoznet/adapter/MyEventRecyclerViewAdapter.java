package com.vomozsystems.apps.android.vomoznet.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Event;
import com.vomozsystems.apps.android.vomoznet.fragment.EventFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a and makes a call to the
 * specified {@link EventFragment.OnEventListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

    private final List<Event> mValues;
    private final EventFragment.OnEventListFragmentInteractionListener mListener;
    private Activity activity;

    public MyEventRecyclerViewAdapter(Activity activity, List<Event> items, EventFragment.OnEventListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_event, parent, false);
        return new ViewHolder(view);
    }

    private void showEventBanner(String bannerUrl) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.banner_dialog);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        ImageView imgView = (ImageView) dialog.findViewById(R.id.img_banner);
        ImageView imgClose = (ImageView) dialog.findViewById(R.id.img_banner_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //if (donationCenter != null) {
        Picasso.with(activity)
                .load(bannerUrl)
                .fit()
                .into(imgView);
        //}

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onBindViewHolder(final MyEventRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(holder.mItem.getTitle());
        holder.mDescriptionView.setText(holder.mItem.getDescription());

        DateFormat fmtDate = new SimpleDateFormat("E MMM d, yyyy");
        DateFormat fmtTime = new SimpleDateFormat("hh:mm a zz");
        Date startDate = new Date(mValues.get(position).getStartTime());
        Date endDate = new Date(mValues.get(position).getEndTime());

        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        if (null != startDate && null != endDate) {
            holder.mDateView.setText(fmtDate.format(mValues.get(position).getStartTime()));
            holder.mStartDateView.setText(fmtTime.format(startDate));
            holder.mEndDateView.setText(fmtTime.format(endDate));
        }

        final String url = mValues.get(position).getBannerUrl();
        try {
            Picasso.with(activity)
                    .load(url)
                    .resize(65, 65)
                    .centerCrop()
                    .placeholder(R.mipmap.ic_event)
                    .into(holder.mImageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } catch (Exception e) {

        }
        holder.mTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventBanner(url);
            }
        });

        holder.mDescriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEventBanner(url);
            }
        });

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEventBanner(url);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onEventListFragmentInteraction(holder.mItem);
                }
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
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final TextView mDateView;
        public final TextView mStartDateView;
        public final TextView mEndDateView;
        public final ImageView mImageView;
        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.event_title);
            mStartDateView = (TextView) view.findViewById(R.id.event_start);
            mDateView = (TextView) view.findViewById(R.id.event_date);
            mEndDateView = (TextView) view.findViewById(R.id.event_end);
            mImageView = (ImageView) view.findViewById(R.id.event_image);
            mDescriptionView = (TextView) view.findViewById(R.id.event_description);
        }
    }
}

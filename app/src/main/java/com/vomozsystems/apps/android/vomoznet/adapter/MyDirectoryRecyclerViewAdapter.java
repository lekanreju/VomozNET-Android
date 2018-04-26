package com.vomozsystems.apps.android.vomoznet.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.fragment.DirectoryFragment.OnListFragmentInteractionListener;
import com.vomozsystems.apps.android.vomoznet.fragment.dummy.DummyContent.DummyItem;
import com.vomozsystems.apps.android.vomoznet.service.ServiceGenerator;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyDirectoryRecyclerViewAdapter extends RecyclerView.Adapter<MyDirectoryRecyclerViewAdapter.ViewHolder> {

    private final List<Personal> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Activity activity;

    public MyDirectoryRecyclerViewAdapter(List<Personal> items, OnListFragmentInteractionListener listener, Activity activity) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_directory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        if(mValues.get(position).getShowFullName().equals("1")) {
            TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.directory_line_row, null);
            textView.setTextSize(17);
            textView.setText(mValues.get(position).getTitle() + " " + mValues.get(position).getFirstName() + " " + mValues.get(position).getLastName());
            holder.mLineLayout.addView(textView);
        }
        if(mValues.get(position).getShowPhone().equals("1")) {
            TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.directory_line_row, null);
            textView.setText(mValues.get(position).getMobilePhone());
            textView.setTextSize(13);
            holder.mLineLayout.addView(textView);
        }
        if(mValues.get(position).getShowEmail().equals("1")) {
            TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.directory_line_row, null);
            textView.setText(mValues.get(position).getPrimaryEmail());
            textView.setTextSize(13);
            holder.mLineLayout.addView(textView);
        }
        if(mValues.get(position).getShowDepartmentPosition().equals("1")) {
            TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.directory_line_row, null);
            textView.setText(mValues.get(position).getPosition());
            holder.mLineLayout.addView(textView);
            textView.setTextSize(13);
        }
        if(mValues.get(position).getShowDepartment().equals("1")) {
            TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.directory_line_row, null);
            textView.setText(mValues.get(position).getDepartment());
            textView.setTextSize(13);
            holder.mLineLayout.addView(textView);
        }
        try {
            Personal personal = mValues.get(position);
            String texterCardString = Long.toString(personal.getTexterCardId());
            if (null != personal && null != personal.getProfilePicture() && !personal.getProfilePicture().equals("0")) {
                String url = ServiceGenerator.PROFILE_PICS_BASE_URL + "/" + personal.getProfilePicture().charAt(0) + "/" + texterCardString.charAt(0) + "/" + personal.getProfilePicture();
                try {
                    Picasso.with(activity)
                            .load(url)
                            .resize(100, 100)
                            .centerCrop()
                            .placeholder(R.mipmap.ic_profile)
                            .into(holder.mImageView);
                } catch (Exception e) {

                }
            }
        }catch (Exception e) {

        }

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final LinearLayout mLineLayout;
        public Personal mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.directory_img);
            mLineLayout = (LinearLayout) view.findViewById(R.id.directory_layout);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + "'";
        }
    }
}

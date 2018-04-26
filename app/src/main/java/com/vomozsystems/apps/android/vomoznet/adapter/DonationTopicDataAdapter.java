package com.vomozsystems.apps.android.vomoznet.adapter;

/**
 * Created by leksrej on 8/29/16.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenterTopic;

import java.util.List;

import io.realm.Realm;

public class DonationTopicDataAdapter extends
        RecyclerView.Adapter<DonationTopicDataAdapter.ViewHolder> {

    private List<DonationCenterTopic> topics;
    private Realm realm;

    public List<DonationCenterTopic> getTopics() {
        return topics;
    }

    public void setTopics(List<DonationCenterTopic> topics) {
        this.topics = topics;
    }

    public DonationTopicDataAdapter(List<DonationCenterTopic> topics, Realm realm) {
        this.topics = topics;
        this.realm = realm;
    }

    // Create new views
    @Override
    public DonationTopicDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.topic_subscription_row_layout, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.tvName.setText(topics.get(position).getTopicName());
        viewHolder.tvDescription.setText(topics.get(position).getTopicDescription());
        viewHolder.chkSelected.setChecked(topics.get(position).getSelected());
        viewHolder.chkSelected.setTag(topics.get(position));
        viewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                DonationCenterTopic contact = (DonationCenterTopic) cb.getTag();

                contact.setSelected(cb.isChecked());
                topics.get(pos).setSelected(cb.isChecked());
                realm.copyToRealmOrUpdate(contact);
                realm.commitTransaction();
            }
        });

    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return topics.size();
    }

    // method to access in activity after updating selection
    public List<DonationCenterTopic> getDonationCenterTopics() {
        return topics;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvDescription;
        public CheckBox chkSelected;
        public DonationCenterTopic donationCenterTopic;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvName = (TextView) itemLayoutView.findViewById(R.id.topic_name);

            tvDescription = (TextView) itemLayoutView.findViewById(R.id.topic_description);
            chkSelected = (CheckBox) itemLayoutView
                    .findViewById(R.id.chkSelected);

        }

    }

}


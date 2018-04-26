package com.vomozsystems.apps.android.vomoznet.adapter;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;
import com.squareup.picasso.Picasso;
import com.vomozsystems.apps.android.vomoznet.GiveActivity;
import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.WizardCallbacks;
import com.vomozsystems.apps.android.vomoznet.entity.ColorWrapper;
import com.vomozsystems.apps.android.vomoznet.entity.PaymentInfo;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsListAdapter extends RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder> {

    private List<ColorWrapper> mDataSet = new ArrayList<>();
    private Context context;
    private PaymentInfo paymentInfo;
    private int mLastAnimatedItemPosition = -1;
    private WizardCallbacks wizardCallbacks;
    private OnItemClickListener mItemsOnClickListener;

    public interface OnItemClickListener{
        void onClick(ColorWrapper colorWrapper);
    }

    public void setWizardCallbacks(WizardCallbacks wizardCallbacks, PaymentInfo paymentInfo) {
        this.wizardCallbacks = wizardCallbacks;
        this.paymentInfo = paymentInfo;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReceiverName;
        public final TextView mReceiverPhone;
        public final TextView mReceiverEmail;
        public final TextView mReceiverAddress;
        public final ImageView mClientLogo;
        public final View mTextContainer;

        public ViewHolder(View view) {
            super(view);
            mReceiverName = (TextView) view.findViewById(R.id.receiver_name);
            mReceiverPhone = (TextView) view.findViewById(R.id.receiver_phone);
            mReceiverAddress = (TextView) view.findViewById(R.id.receiver_address);
            mReceiverEmail = (TextView) view.findViewById(R.id.receiver_email);
            mTextContainer = view.findViewById(R.id.text_container);
            mClientLogo = (ImageView) view.findViewById(R.id.img_receiver);

        }
    }

    public void swapData(List<ColorWrapper> mNewDataSet) {
        mDataSet = mNewDataSet;
        notifyDataSetChanged();
    }

    public void setItemsOnClickListener(OnItemClickListener onClickListener){
        this.mItemsOnClickListener = onClickListener;
    }

    @Override
    public SearchResultsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_list_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchResultsListAdapter.ViewHolder holder, final int position) {

        final ColorWrapper colorSuggestion = mDataSet.get(position);
        holder.mReceiverName.setText(colorSuggestion.getName());
        holder.mReceiverPhone.setText(colorSuggestion.getPhone());
        holder.mReceiverEmail.setText(colorSuggestion.getFullName());
        holder.mReceiverAddress.setText(colorSuggestion.getAddress());

        try {
            String donationCenterLogoUrl = ApiClient.BASE_IMAGE_URL + "/" + colorSuggestion.getLogoUrl();
            if (null != donationCenterLogoUrl && !colorSuggestion.getLogoUrl().equalsIgnoreCase("0")) {
                Picasso.with(context)
                        .load(donationCenterLogoUrl)
                        .into(holder.mClientLogo);
            }
        }catch(Exception e){

        }

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    paymentInfo.setColorWrapper(colorSuggestion);
                    wizardCallbacks.onNext(GiveActivity.RECEIVER_PAGE, paymentInfo);
                }
        });
    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }
}

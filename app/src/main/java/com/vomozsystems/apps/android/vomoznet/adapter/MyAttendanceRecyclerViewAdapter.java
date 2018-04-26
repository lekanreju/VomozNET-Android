package com.vomozsystems.apps.android.vomoznet.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Event;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.fragment.AttendanceFragment;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.AttendEventRequest;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;
import com.vomozsystems.apps.android.vomoznet.utility.GPSTracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * {@link RecyclerView.Adapter} that can display a {@link} and makes a call to the
 * specified {@link }.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyAttendanceRecyclerViewAdapter extends RecyclerView.Adapter<MyAttendanceRecyclerViewAdapter.ViewHolder> {

    private static final Float MAX_DISTANCE = 100F;
    private final AttendanceFragment.OnAttendanceListFragmentInteractionListener mListener;
    ViewHolder tHolder;
    private List<Event> mValues;
    private Activity activity;
    private GPSTracker gtracker;
    private User user;
    private DonationCenter donationCenter;
    private EventUpdater mUIUpdater;

    public MyAttendanceRecyclerViewAdapter(GPSTracker gtracker, List<Event> items, AttendanceFragment.OnAttendanceListFragmentInteractionListener listener, Activity activity, User user, DonationCenter donationCenter) {
        mValues = items;
        mListener = listener;
        this.activity = activity;
        this.user = user;
        this.donationCenter = donationCenter;
        this.gtracker = gtracker;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_attendance, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        tHolder = holder;
        DateFormat fmtDate = new SimpleDateFormat("E MMM d, yyyy");
        DateFormat fmtTime = new SimpleDateFormat("hh:mm a zz");
        Date startDate;
        Date endDate;
        startDate = new Date(mValues.get(position).getStartTime());
        endDate = new Date(mValues.get(position).getEndTime());
        holder.mItem = mValues.get(position);
        holder.mTitleView.setText(mValues.get(position).getTitle());
        if (null != startDate && null != endDate) {
            holder.mDateView.setText(fmtDate.format(mValues.get(position).getStartTime()));
            holder.mStartView.setText(fmtTime.format(startDate));
            holder.mEndView.setText(fmtTime.format(endDate));
        }

        if (gtracker.canGetLocation()) {
            Location dCenterLocation = new Location("DCenter");
            dCenterLocation.setLatitude(holder.mItem.getLatitude());
            dCenterLocation.setLongitude(holder.mItem.getLongitude());
            Location currentLocation = new Location("Current");
            currentLocation.setLatitude(gtracker.getLatitude());
            currentLocation.setLongitude(gtracker.getLongitude());
            float distance = dCenterLocation.distanceTo(currentLocation);
            Date currentTime = new Date();
            if (null != startDate && null != endDate) {
                if (currentTime.after(startDate) && currentTime.before(endDate)) {
                    if (mValues.get(position).isAttend()) {
                        holder.mAttendButton.setVisibility(View.GONE);
                        holder.mMessageView.setVisibility(View.VISIBLE);
                        holder.mMessageView.setTextColor(Color.WHITE);
                        holder.mMessageView.setText("Your have already marked your attendance for this event!");
                    } else if (distance <= MAX_DISTANCE) {
                        holder.mAttendButton.setVisibility(View.VISIBLE);
                        holder.mMessageView.setText("This event is currently ongoing!");
                        holder.mMessageView.setTextColor(Color.WHITE);
                        holder.mMessageView.setVisibility(View.VISIBLE);
                    } else {
                        holder.mAttendButton.setVisibility(View.GONE);
                        holder.mMessageView.setVisibility(View.VISIBLE);
                        holder.mMessageView.setTextColor(Color.WHITE);
                        holder.mMessageView.setText("This event is currently ongoing!\nBut you are not near the venue of the event");
                    }
                } else if (currentTime.after(endDate)) {
                    holder.mAttendButton.setVisibility(View.GONE);
                    holder.mMessageView.setVisibility(View.VISIBLE);
                    holder.mMessageView.setTextColor(Color.WHITE);
                    holder.mMessageView.setText("This event has ENDED!");
                } else {
                    holder.mAttendButton.setVisibility(View.GONE);
                    holder.mMessageView.setVisibility(View.VISIBLE);
                    holder.mMessageView.setTextColor(Color.WHITE);
                    holder.mMessageView.setText("This event has not yet started");
                }
            }
        } else {
            holder.mAttendButton.setVisibility(View.GONE);
            holder.mMessageView.setVisibility(View.VISIBLE);
            holder.mMessageView.setTextColor(Color.WHITE);
            holder.mMessageView.setText("Your location cannot be retrieved");
        }

        holder.mAttendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (null != user) {
                        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                        AttendEventRequest request = new AttendEventRequest();
                        request.setEvent(holder.mItem);
                        request.setAttended(true);
                        request.setTexterCardId(Long.valueOf(user.getTexterCardId()));
                        final SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE)
                                .setTitleText("VomozPay")
                                .setContentText("Processing...please wait");
                        dialog1.show();
                        Call<BaseServiceResponse> call = apiService.attendEvent(request, "", ApplicationUtils.APP_ID);
                        call.enqueue(new Callback<BaseServiceResponse>() {
                            @Override
                            public void onResponse(Call<BaseServiceResponse> call, Response<BaseServiceResponse> response) {
                                dialog1.dismiss();
                                if (response.isSuccessful()) {
                                    holder.mMessageView.setText("Your attendance has been processed");
                                    holder.mMessageView.setTextColor(Color.WHITE);
                                    holder.mAttendButton.setVisibility(View.GONE);
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    mValues.get(position).setAttend(true);
                                    realm.copyToRealmOrUpdate(mValues.get(position));
                                    realm.commitTransaction();
                                    SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("VomozPay")
                                            .setContentText("Your attendance has been processed successfully");
                                    dialog1.show();
                                } else {
                                    try {
                                        String json = response.errorBody().string();
                                        BaseServiceResponse baseServiceResponse = ApiClient.getError(json);
                                        SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("VomozPay")
                                                .setContentText(baseServiceResponse.getMessage().getDescription() + "\n" +
                                                        "TransactionID : " + baseServiceResponse.getTransactionId());
                                        dialog1.show();
                                    } catch (Exception e) {

                                }
                            }
                            }

                            @Override
                            public void onFailure(Call<BaseServiceResponse> call, Throwable t) {
                                dialog1.dismiss();
                                SweetAlertDialog dialog1 = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("VomozPay")
                                        .setContentText("Your request was not completed successfully");
                                dialog1.show();
                            }
                        });
                    }
                } catch (Exception e) {

            }
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAttendanceListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

//    class UpdateEventsTask extends AsyncTask<Void, Void, Void> {
//        ViewHolder holder;
//
//        public UpdateEventsTask(ViewHolder holder) {
//            this.holder = holder;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if (gtracker.canGetLocation()) {
//                        Location dCenterLocation = new Location("DCenter");
//                        dCenterLocation.setLatitude(donationCenter.getLatitude());
//                        dCenterLocation.setLongitude(donationCenter.getLongitude());
//                        Location currentLocation = new Location("Current");
//                        currentLocation.setLatitude(gtracker.getLatitude());
//                        currentLocation.setLongitude(gtracker.getLongitude());
//                        float distance = dCenterLocation.distanceTo(currentLocation);
//                        Date currentTime = new Date();
//                        if (null != startDate && null != endDate) {
//                            if (currentTime.after(startDate) && currentTime.before(endDate)) {
//                                if (distance <= MAX_DISTANCE) {
//                                    holder.mAttendButton.setVisibility(View.VISIBLE);
//                                    holder.mMessageView.setText("This event is currently ongoing!");
//                                    holder.mMessageView.setTextColor(Color.parseColor("#009900"));
//                                    holder.mMessageView.setVisibility(View.VISIBLE);
//                                } else {
//                                    holder.mAttendButton.setVisibility(View.GONE);
//                                    holder.mMessageView.setVisibility(View.VISIBLE);
//                                    holder.mMessageView.setTextColor(Color.RED);
//                                    holder.mMessageView.setText("This event is currently ongoing!\nBut you are not at the venue of the event");
//                                }
//                            } else if (currentTime.after(endDate)) {
//                                holder.mAttendButton.setVisibility(View.GONE);
//                                holder.mMessageView.setTextColor(Color.RED);
//                                holder.mMessageView.setVisibility(View.VISIBLE);
//                                holder.mMessageView.setText("This event has ENDED!");
//                            } else {
//                                holder.mAttendButton.setVisibility(View.GONE);
//                                holder.mMessageView.setVisibility(View.VISIBLE);
//                                holder.mMessageView.setTextColor(Color.RED);
//                                holder.mMessageView.setText("This event has not yet started");
//                            }
//                        }
//                    } else {
//                        holder.mAttendButton.setVisibility(View.GONE);
//                        holder.mMessageView.setVisibility(View.VISIBLE);
//                        holder.mMessageView.setTextColor(Color.RED);
//                        holder.mMessageView.setText("Your location cannot be retrieved");
//                    }
//                }
//            });
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTitleView;
        public final TextView mStartView;
        public final TextView mDateView;
        public final TextView mEndView;
        public final Button mAttendButton;
        public final TextView mMessageView;
        public Event mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = (TextView) view.findViewById(R.id.event_title);
            mDateView = (TextView) view.findViewById(R.id.event_date);
            mStartView = (TextView) view.findViewById(R.id.event_start);
            mEndView = (TextView) view.findViewById(R.id.event_end);
            mAttendButton = (Button) view.findViewById(R.id.btn_attend);
            mMessageView = (TextView) view.findViewById(R.id.attend_message);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}

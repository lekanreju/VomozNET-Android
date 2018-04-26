package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyAttendanceRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Event;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.DonationCenterResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationCenterEventsResponse;
import com.vomozsystems.apps.android.vomoznet.utility.ApplicationUtils;
import com.vomozsystems.apps.android.vomoznet.utility.GPSTracker;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnAttendanceListFragmentInteractionListener}
 * interface.
 */
public class AttendanceFragment extends Fragment implements Refreshable{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnAttendanceListFragmentInteractionListener mListener;
    private Realm realm;
    private RecyclerView recyclerView;
    private Float MAX_DISTANCE = 100F;
    private FloatingActionButton fab;
    private List<Event> events;
    private TextView emptyView;
    private GPSTracker gtracker;
    private View view;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AttendanceFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AttendanceFragment newInstance() {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        if(gtracker!=null)
            gtracker.stopUsingGPS();
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance_list, container, false);
        // Set the adapter
        Context context = view.getContext();
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    private void getEvents(final DonationCenter donationCenter, final User user, ApiInterface apiService) {
        if (null != donationCenter && null != user && null != recyclerView) {
            Call<GetDonationCenterEventsResponse> call = apiService.getAttendableDonationCenterEvents(donationCenter.getCardId(), Long.valueOf(user.getTexterCardId()), user.getAuthToken(), ApplicationUtils.APP_ID);
//            final SweetAlertDialog d1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE)
//                    .setTitleText(getResources().getString(R.string.app_name))
//                    .setContentText("Fetching events...Please wait");
//            d1.show();
            call.enqueue(new Callback<GetDonationCenterEventsResponse>() {
                @Override
                public void onResponse(Call<GetDonationCenterEventsResponse> call, Response<GetDonationCenterEventsResponse> response) {
                    //d1.dismiss();
                    if (response.isSuccessful()) {
                        events = response.body().getResponseData();
                        if (null != events && events.size() > 0) {
                            realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            realm.delete(Event.class);
                            realm.copyToRealmOrUpdate(events);
                            realm.commitTransaction();
                            final MyAttendanceRecyclerViewAdapter adapter = new MyAttendanceRecyclerViewAdapter(gtracker, events, mListener, getActivity(), user, realm.copyFromRealm(donationCenter));
                            recyclerView.setAdapter(adapter);

                            if (null != events && events.size() > 0) {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(GONE);
                            } else {
                                recyclerView.setVisibility(GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }
//                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
//                                    .setTitleText(getResources().getString(R.string.app_name))
//                                    .setContentText(events.size() + " events found");
//                            dialog1.show();

                            final Handler handler = new Handler();
                            Timer timer = new Timer();
                            TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            Log.d(getClass().getSimpleName(), "Running >>>>>");
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                            };
                            timer.schedule(task, 0, 30000);

                        } else {
                            recyclerView.setVisibility(GONE);
                            emptyView.setVisibility(View.VISIBLE);

                        }

                        //GPSTracker gtracker = new GPSTracker(getActivity());
                        if (!gtracker.canGetLocation()) {
                            // cannot get location
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                gtracker.showSettingsAlert();
                                SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText(getResources().getString(R.string.app_name))
                                        .setContentText("Location services is off\n\nYour location cannot be determined");
                                dialog1.show();
                            }
                        }

                    } else {
                        try {
                            String json = response.errorBody().string();
                            BaseServiceResponse baseServiceResponse = ApiClient.getError(json);
                            SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText(getResources().getString(R.string.app_name))
                                    .setContentText(baseServiceResponse.getMessage().getDescription() + "\n" +
                                            "TransactionID : " + baseServiceResponse.getTransactionId());
                            dialog1.show();
                        } catch (Exception e) {

                        }
                    }
                }

                @Override
                public void onFailure(Call<GetDonationCenterEventsResponse> call, Throwable t) {
                    //d1.dismiss();
                    emptyView = (TextView) view.findViewById(R.id.empty_view);
                    recyclerView.setVisibility(GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("Events cannot be retrieved");
                }
            });
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAttendanceListFragmentInteractionListener) {
            mListener = (OnAttendanceListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAttendanceListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        realm.close();
        if(gtracker!=null)
            gtracker.stopUsingGPS();
        super.onStop();
    }

    @Override
    public void refreshView() {
        if(view != null) {
            realm = Realm.getDefaultInstance();
            gtracker = new GPSTracker(getActivity());
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            final DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            final User user = realm.where(User.class).findFirst();

            emptyView = (TextView) view.findViewById(R.id.empty_view);
            recyclerView.setVisibility(GONE);
            emptyView.setVisibility(View.VISIBLE);

            if (null != donationCenter) {
                if (donationCenter.getLatitude() != 0 && donationCenter.getLongitude() != 0) {
                    getEvents(donationCenter, user, apiService);
                } else {
                    Call<DonationCenterResponse> call = apiService.setMemberDonationCenterCoords(donationCenter.getCardId(), "", ApplicationUtils.APP_ID);
                    call.enqueue(new Callback<DonationCenterResponse>() {
                        @Override
                        public void onResponse(Call<DonationCenterResponse> call, Response<DonationCenterResponse> response) {
                            if (null != response && null != response.body() && null != response.body().getResponseData() && response.body().getResponseData().size() > 0) {
                                realm = Realm.getDefaultInstance();
                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(response.body().getResponseData().get(0));
                                realm.commitTransaction();
                                getEvents(response.body().getResponseData().get(0), user, apiService);
                            } else {
                                emptyView = (TextView) view.findViewById(R.id.empty_view);
                                recyclerView.setVisibility(GONE);
                                emptyView.setVisibility(View.VISIBLE);
                                emptyView.setText("Church location cannot be retrieved");
                            }

                        }

                        @Override
                        public void onFailure(Call<DonationCenterResponse> call, Throwable t) {
                            emptyView = (TextView) view.findViewById(R.id.empty_view);
                            recyclerView.setVisibility(GONE);
                            emptyView.setVisibility(View.VISIBLE);
                            emptyView.setText("Events cannot be fetched");
                        }
                    });
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnAttendanceListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onAttendanceListFragmentInteraction(Event item);
    }
}

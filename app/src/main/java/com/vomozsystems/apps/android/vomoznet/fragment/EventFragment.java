package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyEventRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Event;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.GetDonationCenterEventsResponse;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.view.View.GONE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnEventListFragmentInteractionListener}
 * interface.
 */
public class EventFragment extends Fragment implements Refreshable{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnEventListFragmentInteractionListener mListener;
    private View view;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EventFragment newInstance(int columnCount) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        refreshView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event_list, container, false);
        mColumnCount = 1;
        // Set the adapter
        final TextView emptyView = view.findViewById(R.id.empty_view);
        final RecyclerView recyclerView = view.findViewById(R.id.list);
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEventListFragmentInteractionListener) {
            mListener = (OnEventListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnEventListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void refreshView() {
        if(view != null) {
            final TextView emptyView = view.findViewById(R.id.empty_view);
            final RecyclerView recyclerView = view.findViewById(R.id.list);

            Realm realm = Realm.getDefaultInstance();
            DonationCenter homeDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            User user = realm.where(User.class).findFirst();
            if (homeDonationCenter != null && null != user) {
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                //FORM, MEDIA, LINK, DIRECTORY_SERVICE

                Call<GetDonationCenterEventsResponse> call = apiService.getAllDonationCenterEvents(homeDonationCenter.getCardId(), Long.valueOf(user.getTexterCardId()), "", "");
                call.enqueue(new Callback<GetDonationCenterEventsResponse>() {
                    @Override
                    public void onResponse(Call<GetDonationCenterEventsResponse> call, Response<GetDonationCenterEventsResponse> response) {
                        if (response.isSuccessful() && null != response.body().getResponseData() && response.body().getResponseData().size() > 0) {
                            final List<Event> events = response.body().getResponseData();
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(GONE);
                            recyclerView.setAdapter(new MyEventRecyclerViewAdapter(getActivity(), events, mListener));

                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetDonationCenterEventsResponse> call, Throwable t) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText(getResources().getString(R.string.app_name))
                                .setContentText("Events for this organization cannot be retrieved")
                                .show();
                    }
                });

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
    public interface OnEventListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onEventListFragmentInteraction(Event event);
    }
}

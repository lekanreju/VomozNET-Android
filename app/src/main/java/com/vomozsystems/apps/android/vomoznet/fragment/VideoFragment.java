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

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyItemRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.adapter.MyVideoEntryRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Item;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.GetItemResponse;
import com.vomozsystems.apps.android.vomoznet.youtube.VideoEntry;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnVideoListFragmentInteractionListener}
 * interface.
 */
public class VideoFragment extends Fragment implements Refreshable{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 3;
    private OnVideoListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private View view;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VideoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static VideoFragment newInstance(int columnCount) {
        VideoFragment fragment = new VideoFragment();
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
    public void onResume() {
        super.onResume();

        Realm realm = Realm.getDefaultInstance();
        DonationCenter homeDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        User user = realm.where(User.class).findFirst();
        if (homeDonationCenter != null && null != user) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<GetItemResponse> call = apiService.getDonationCenterItems(homeDonationCenter.getCardId(), "MEDIA");
            call.enqueue(new Callback<GetItemResponse>() {
                @Override
                public void onResponse(Call<GetItemResponse> call, Response<GetItemResponse> response) {
                    if (response.isSuccessful() && null != response.body().getResponseData() && response.body().getResponseData().size() > 0) {
                        final List<Item> items = response.body().getResponseData();
                        Context context = view.getContext();
                        if (mColumnCount <= 1) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        } else {
                            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                        }
                        List<VideoEntry> list = new ArrayList<VideoEntry>();
                        for(Item item: items) {
                            list.add(new VideoEntry(item.getTitle(), item.getUrl()));
                        }
                        recyclerView.setAdapter(new MyVideoEntryRecyclerViewAdapter(getActivity(), list, mListener));
                    }
                }

                @Override
                public void onFailure(Call<GetItemResponse> call, Throwable t) {

                }
            });
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_videoentry_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {

            recyclerView = (RecyclerView) view;
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnVideoListFragmentInteractionListener) {
            mListener = (OnVideoListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnVideoListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void refreshView() {

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
    public interface OnVideoListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onVideoListFragmentInteraction(VideoEntry item);
    }
}

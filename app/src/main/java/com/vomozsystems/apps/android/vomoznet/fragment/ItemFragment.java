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
import com.vomozsystems.apps.android.vomoznet.adapter.MyItemRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Item;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.GetItemResponse;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnItemFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements Refreshable{

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 3;
    private OnItemFragmentInteractionListener mListener;
    private String type;
    private View view;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount, String type) {
        ItemFragment fragment = new ItemFragment();
        fragment.type = type;
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_item_list, container, false);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemFragmentInteractionListener) {
            mListener = (OnItemFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
            final RecyclerView recyclerView = view.findViewById(R.id.list);
            final TextView emptyView = view.findViewById(R.id.empty_view);
            // Set the adapter
            Context context = view.getContext();

            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            Realm realm = Realm.getDefaultInstance();
            DonationCenter homeDonationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
            User user = realm.where(User.class).findFirst();
            if (homeDonationCenter != null && null != user) {
                ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

                Call<GetItemResponse> call = apiService.getDonationCenterItems(homeDonationCenter.getCardId(), type);
                call.enqueue(new Callback<GetItemResponse>() {
                    @Override
                    public void onResponse(Call<GetItemResponse> call, Response<GetItemResponse> response) {
                        if (response.isSuccessful() && null != response.body().getResponseData() && response.body().getResponseData().size() > 0) {
                            final List<Item> items = response.body().getResponseData();
                            int defaultImage = 0;
                            if (type.equalsIgnoreCase("FORM")) {
                                defaultImage = R.mipmap.ic_document;
                            } else if (type.equalsIgnoreCase("MEDIA")) {
                                defaultImage = R.mipmap.ic_media;
                            } else if (type.equalsIgnoreCase("DIRECTORY_SERVICE")) {
                                defaultImage = R.mipmap.ic_profile;
                            }
                            emptyView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(getActivity(), items, mListener, defaultImage));
                        } else {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(GONE);
                            emptyView.setText("No " + type + " Found");
                        }
                    }

                    @Override
                    public void onFailure(Call<GetItemResponse> call, Throwable t) {
                        emptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(GONE);
                        emptyView.setText("No " + type + " Found");

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
    public interface OnItemFragmentInteractionListener {
        // TODO: Update argument type and name
        void onItemFragmentInteraction(Item item);
    }
}

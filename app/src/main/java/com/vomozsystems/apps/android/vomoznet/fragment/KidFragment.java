package com.vomozsystems.apps.android.vomoznet.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.vomozsystems.apps.android.vomoznet.R;
import com.vomozsystems.apps.android.vomoznet.adapter.MyKidRecyclerViewAdapter;
import com.vomozsystems.apps.android.vomoznet.entity.Child;
import com.vomozsystems.apps.android.vomoznet.entity.DonationCenter;
import com.vomozsystems.apps.android.vomoznet.entity.Personal;
import com.vomozsystems.apps.android.vomoznet.entity.User;
import com.vomozsystems.apps.android.vomoznet.fragment.dummy.DummyContent;
import com.vomozsystems.apps.android.vomoznet.fragment.dummy.DummyContent.DummyItem;
import com.vomozsystems.apps.android.vomoznet.service.ApiClient;
import com.vomozsystems.apps.android.vomoznet.service.ApiInterface;
import com.vomozsystems.apps.android.vomoznet.service.BaseServiceResponse;
import com.vomozsystems.apps.android.vomoznet.service.GetMemberChildrenResponse;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnKidListFragmentInteractionListener}
 * interface.
 */
public class KidFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 4;
    private OnKidListFragmentInteractionListener mListener;
    private View view;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public KidFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static KidFragment newInstance(int columnCount) {
        KidFragment fragment = new KidFragment();
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
    public void finish() {
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();

        Context context = view.getContext();
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        Button addChildButton = view.findViewById(R.id.add_child);
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateChildDialogFragment fragment = UpdateChildDialogFragment.newInstance(null, KidFragment.this);
                fragment.show(getActivity().getSupportFragmentManager(), "addChild");
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Realm realm = Realm.getDefaultInstance();
        DonationCenter donationCenter = realm.where(DonationCenter.class).equalTo("homeDonationCenter", true).findFirst();
        User user = realm.where(User.class).findFirst();
        Personal personal = new Personal();
        personal.setCenterCardId(donationCenter.getCardId());
        personal.setTexterCardId(Long.valueOf(user.getTexterCardId()));
        Call<GetMemberChildrenResponse> call = apiInterface.getMemberChildren(personal, "", "");
        call.enqueue(new Callback<GetMemberChildrenResponse>() {
            @Override
            public void onResponse(Call<GetMemberChildrenResponse> call, Response<GetMemberChildrenResponse> response) {
                if(response.isSuccessful()) {
                    recyclerView.setAdapter(new MyKidRecyclerViewAdapter(getActivity(), response.body().getResponseData(), mListener, KidFragment.this));
                }
            }

            @Override
            public void onFailure(Call<GetMemberChildrenResponse> call, Throwable t) {
                Log.d(getClass().getSimpleName(), "");
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kid_list, container, false);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnKidListFragmentInteractionListener) {
            mListener = (OnKidListFragmentInteractionListener) context;
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
    public interface OnKidListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onKidListFragmentInteraction(Child item);
    }
}

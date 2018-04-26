package com.vomozsystems.apps.android.vomoznet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vomozsystems.apps.android.vomoznet.entity.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * A fragment representing a single Message detail screen.
 * This fragment is either contained in a {@link MessageListActivity}
 * in two-pane mode (on tablets) or a {@link MessageDetailActivity}
 * on handsets.
 */
public class MessageDetailFragment extends Fragment {
    public static final String ARG_ITEM_ID = "item_id";
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private RealmConfiguration config;
    private Realm realm;

    /**
     * The dummy content this fragment is presenting.
     */
    private Message mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageDetailFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = realm.where(Message.class).equalTo("id", getArguments().getString(ARG_ITEM_ID)).findFirst();
            if (null != mItem) {
                realm.beginTransaction();
                mItem.setNewMessage(false);
                realm.commitTransaction();
//                Activity activity = this.getActivity();

            }
        }

        long badgeCount = realm.where(Message.class).equalTo("newMessage", true).count();
        if(badgeCount > 0)
            ShortcutBadger.applyCount(this.getActivity(), (int)badgeCount);
        else
            ShortcutBadger.removeCount(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.message_detail, container, false);

//        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
//        if (appBarLayout != null) {
//            appBarLayout.setTitle(mItem.getTitle());
//        }
        DateFormat fmt = new SimpleDateFormat("MMM d yyyy H:mm a");
        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.message_body)).setText(mItem.getBody());
            ((TextView) rootView.findViewById(R.id.message_date)).setText(mItem.getDate().toString());
            ((TextView) rootView.findViewById(R.id.message_from)).setText(mItem.getFrom());
            ((TextView) rootView.findViewById(R.id.message_title)).setText(mItem.getTitle());
        }

        return rootView;
    }
}

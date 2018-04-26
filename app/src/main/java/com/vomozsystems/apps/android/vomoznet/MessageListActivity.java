package com.vomozsystems.apps.android.vomoznet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.splunk.mint.Mint;
import com.vomozsystems.apps.android.vomoznet.entity.Message;
import com.vomozsystems.apps.android.vomoznet.entity.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * An activity representing a list of Messages. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MessageDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MessageListActivity extends AppCompatActivity{

    public static final String MSG_FROM = "msg.form";
    public static final String MSG_TITLE = "msg.title";
    public static final String MSG_BODY = "msg.body";
    public static final String MSG_DATE = "msg.date";
    public static final String MSG_ID = "msg.id";
    public static final String NEW_MSG = "msg.new";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RealmConfiguration config;
    private Realm realm;

    @Override
    public void onDestroy() {
        if (null != realm && !realm.isClosed())
            realm.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        Mint.initAndStartSession(this, "22ce5546");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (toolbar != null) {
            toolbar.setTitle(getTitle() + " - Messages");
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        realm = Realm.getDefaultInstance();

        com.pkmmte.view.CircularImageView imgPerson = (com.pkmmte.view.CircularImageView) findViewById(R.id.img_user_profile);

        if (null != imgPerson)
            imgPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        Bundle bundle = getIntent().getExtras();
        if (null != bundle && bundle.containsKey(NEW_MSG)) {
            String from = getIntent().getExtras().getString(MSG_FROM);
            String title = getIntent().getExtras().getString(MSG_TITLE);
            long time = getIntent().getExtras().getLong(MSG_DATE);
            String body = getIntent().getExtras().getString(MSG_BODY);
            String id = getIntent().getExtras().getString(MSG_ID);

            if (null != from)
                from = from.replace("/topics/", "Topic: ");
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Message message = new Message();
            message.setBody(body);
            message.setDate(new Date(time));
            message.setId(id);
            message.setTitle(title);
            message.setFrom(from);
            message.setNewMessage(true);
            realm.copyToRealmOrUpdate(message);
            realm.commitTransaction();
        }

        long badgeCount = realm.where(Message.class).equalTo("newMessage", true).count();
        if(badgeCount > 0)
            ShortcutBadger.applyCount(this, (int)badgeCount);
        else
            ShortcutBadger.removeCount(this);

        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        View recyclerView = findViewById(R.id.message_list);
        assert recyclerView != null;
        realm = Realm.getDefaultInstance();
        List<Message> allMessages = realm.where(Message.class).findAll().sort("date", Sort.DESCENDING);

        setupRecyclerView((RecyclerView) recyclerView, allMessages);
        List<Message> newMessages = realm.where(Message.class).equalTo("newMessage", true).findAll().sort("date", Sort.DESCENDING);
        if (null != newMessages && newMessages.size() > 0) {
            SweetAlertDialog dialog1 = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText("There are new messages");
            dialog1.show();
        } else {
            SweetAlertDialog dialog1 = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getResources().getString(R.string.app_name))
                    .setContentText("There are NO new messages");
            dialog1.show();
        }

        if (null != allMessages && allMessages.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        if (findViewById(R.id.message_detail_container) != null) {
            mTwoPane = true;
        }

        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();

        imgPerson = (com.pkmmte.view.CircularImageView) findViewById(R.id.img_user_profile);
        if (null != imgPerson)
            imgPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        if (null != user && user.getUserId() == null) {
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialog.setTitleText(getString(R.string.app_name));
            sweetAlertDialog.setContentText("Your profile cannot be loaded");
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    finish();
                }
            });
            sweetAlertDialog.setConfirmText("OK");
            sweetAlertDialog.show();
        }
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Message> messages) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(messages));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Message> mValues;

        public SimpleItemRecyclerViewAdapter(List<Message> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mTitleView.setText(mValues.get(position).getTitle());
            if (null != mValues.get(position).getNewMessage()) {
                if (mValues.get(position).getNewMessage()) {
                    holder.mTitleView.setTypeface(null, Typeface.BOLD);
                } else {
                    holder.mTitleView.setTypeface(null, Typeface.NORMAL);
                }
            }

            DateFormat fmt = new SimpleDateFormat("MMM dd, yyyy H:mm a");
            String date = fmt.format(mValues.get(position).getDate());
            holder.mDateView.setText(date);
            holder.mFromView.setText(mValues.get(position).getFrom());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        MessageDetailFragment fragment = new MessageDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.message_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MessageDetailActivity.class);
                        intent.putExtra(MessageDetailFragment.ARG_ITEM_ID, holder.mItem.getId());
                        context.startActivity(intent);
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
            public final TextView mFromView;
            public final TextView mDateView;
            public final TextView mTitleView;
            public Message mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mFromView = (TextView) view.findViewById(R.id.message_from);
                mDateView = (TextView) view.findViewById(R.id.message_date);
                mTitleView = (TextView) view.findViewById(R.id.message_title);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTitleView.getText() + "'";
            }
        }
    }
}

package com.vomozsystems.apps.android.vomoznet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.splunk.mint.Mint;
import com.vomozsystems.apps.android.vomoznet.entity.Message;
import com.vomozsystems.apps.android.vomoznet.entity.User;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;

/**
 * An activity representing a single Message detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MessageListActivity}.
 */
public class MessageDetailActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume reached");
        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        TextView name = (TextView) findViewById(R.id.text_name);
        TextView totalContribution = (TextView) findViewById(R.id.text_user_total_contrib);
        TextView totalContributionLabel = (TextView) findViewById(R.id.text_user_total_contrib_label);

        if (null != name && null != totalContribution && null != user) {
            totalContributionLabel.setText(new SimpleDateFormat("yyyy").format(new Date()) + " Contributions: ");
            String nameString = user.getFirstName() + " " + user.getLastName();
            name.setText(nameString);
            NumberFormat fmt = DecimalFormat.getCurrencyInstance();
            if (null != user.getCurrentYearTotalContribution())
                totalContribution.setText(fmt.format(user.getCurrentYearTotalContribution()));
            else
                totalContribution.setText(fmt.format(0D));
        }
    }

    @Override
    public void onDestroy() {
        if (null != realm && !realm.isClosed())
            realm.close();
        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        Mint.initAndStartSession(this, "22ce5546");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        com.pkmmte.view.CircularImageView imgPerson = (com.pkmmte.view.CircularImageView) findViewById(R.id.img_user_profile);

        if (null != imgPerson)
            imgPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        Realm realm = Realm.getDefaultInstance();
        Message mItem = realm.where(Message.class).equalTo("id", getIntent().getStringExtra(MessageDetailFragment.ARG_ITEM_ID)).findFirst();
        //toolbar.setTitle(mItem.getTitle());
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(MessageDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(MessageDetailFragment.ARG_ITEM_ID));
            MessageDetailFragment fragment = new MessageDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.message_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MessageListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

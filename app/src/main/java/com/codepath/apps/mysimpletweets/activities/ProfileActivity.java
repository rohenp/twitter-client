package com.codepath.apps.mysimpletweets.activities;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.CustomAlertDialogFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONObject;

public class ProfileActivity extends ActionBarActivity implements CustomAlertDialogFragment.CustomAlertListener {
    private TwitterClient client;
    private boolean dialogShowable = true;
    private long uid;
    private TextView tvUsername;
    private TextView tvName;
    private TextView tvTweets;
    private TextView tvFollowers;
    private TextView tvFollowing;
    private TextView tvTagline;
    private ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        client = TwitterApplication.getRestClient();

        uid = getIntent().getLongExtra("uid", 0);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvName = (TextView) findViewById(R.id.tvName);
        tvTweets = (TextView) findViewById(R.id.tvTweets);
        tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageResource(android.R.color.transparent);
        tvTagline = (TextView) findViewById(R.id.tvTagline);

        if (savedInstanceState == null) {
            UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance(uid);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, userTimelineFragment);
            ft.commit();
        }

        User user = User.byUid(uid);

        if (user != null) {
            populateUser(user);
        }

        // Refresh the user anyway, but do it after so the network call doesn't cause any lag
        getUser();
    }

    private void getUser() {
        client.getUser(uid, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user = User.fromJSON(response);

                populateUser(user);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                showAlertDialog("Unable to view user", "A problem occurred while trying to view the specified user.");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void showAlertDialog(String title, String message) {
        if (!dialogShowable) {
            return;
        }
        dialogShowable = false;

        FragmentManager fm = getSupportFragmentManager();
        CustomAlertDialogFragment alertDialog = CustomAlertDialogFragment.newInstance(title, message);
        alertDialog.show(fm, "fragment_alert");
    }

    private void populateUser(User user) {
        getSupportActionBar().setTitle(user.getScreenName());
        tvUsername.setText("@" + user.getScreenName());
        tvName.setText(user.getName());
        tvTweets.setText(formatNumber(user.getTweetsCount()));
        tvFollowers.setText(formatNumber(user.getFollowersCount()));
        tvFollowing.setText(formatNumber(user.getFollowingCount()));
        tvTagline.setText(user.getDescription());
        Picasso.with(getApplicationContext()).load(user.getProfileImageUrl()).into(ivProfileImage);
    }

    private String formatNumber(long number) {
        String numberString = String.valueOf(number);

        if (numberString.length() >= 4) {
            numberString = numberString.substring(0, numberString.length() - 2);

            int newLength = numberString.length();
            numberString = numberString.substring(0, newLength - 1) + "." + numberString.substring(newLength - 1, newLength) + "K";
        }

        return numberString;
    }

    @Override
    public void onOKButton() {
        dialogShowable = true;
    }
}

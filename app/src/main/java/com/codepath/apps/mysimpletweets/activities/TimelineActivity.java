package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.ComposeDialog;
import com.codepath.apps.mysimpletweets.fragments.CustomAlertDialogFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;


public class TimelineActivity extends ActionBarActivity implements ComposeDialog.ComposeDialogListener, CustomAlertDialogFragment.CustomAlertListener {
    private TwitterClient client;
    private User currentUser = null;
    private boolean dialogShowable = true;
    private TweetsPagerAdapter viewPagerAdapter;

    @Override
    public void onFinishComposeDialog(String body) {
        postStatus(body);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_bird);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        client = TwitterApplication.getRestClient();
        setCurrentUser();
    }

    private void setCurrentUser() {
        client.getCurrentUser(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                currentUser = User.fromJSON(response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            }
        });
    }

    private void postStatus(String body) {
        if (!isNetworkAvailable()) {
            showAlertDialog("No Internet connection", "Internet access is required to post a new tweet.");

            return;
        }

        if (body.length() > 140) {
            showAlertDialog("Unable to post tweet", "Tweet is greater than 140 characters.");
            return;
        }

        client.updateStatus(body, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                HomeTimelineFragment homeTimelineFragment = (HomeTimelineFragment) viewPagerAdapter.getRegisteredFragment(0);
                homeTimelineFragment.insertTweet(Tweet.fromJSON(response), 0);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                showAlertDialog("Unable to post tweet", "A problem was encountered while trying to post your tweet.");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.miCompose:
                composeMessage();
                return true;
            case R.id.miProfile:
                profileView(currentUser);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void profileView(User user) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("uid", user.getUid());
        startActivity(i);
    }

    private void composeMessage() {
        if (!isNetworkAvailable()) {
            showAlertDialog("No Internet connection", "Internet access is required to post a new tweet.");

            return;
        }

        if (currentUser == null) {
            setCurrentUser();
        }

        FragmentManager fm = getSupportFragmentManager();
        ComposeDialog composeDialog = ComposeDialog.newInstance(currentUser);
        composeDialog.show(fm, "fragment_compose");
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

    public class TweetsPagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = { "Home", "Mentions" };
        SparseArray<Fragment> registeredFragments = new SparseArray<>();

        public TweetsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new HomeTimelineFragment();
            } else if (position == 1) {
                return new MentionsTimelineFragment();
            }

            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    @Override
    public void onOKButton() {
        dialogShowable = true;
    }
}
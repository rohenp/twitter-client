package com.codepath.apps.mysimpletweets.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.activities.DetailActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.listeners.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment implements CustomAlertDialogFragment.CustomAlertListener {
    private boolean dialogShowable = true;
    private ListView lvTweets;

    public ArrayList<Tweet> tweets;
    public TweetsArrayAdapter aTweets;
    public long highestUid = 0;
    public SwipeRefreshLayout swipeContainer;
    public TwitterClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        setupViews(view);
        setupSwipeRefresh(view);

        lvTweets.setAdapter(aTweets);
        populateTimeline();

        return view;
    }

    private void setupSwipeRefresh(View view) {
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                resetTimeline();
                populateTimeline();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void setupViews(View view) {
        lvTweets = (ListView) view.findViewById(R.id.lvTweets);

        lvTweets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("tweet", tweets.get(position));
                startActivity(i);
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                populateTimeline();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        client = TwitterApplication.getRestClient();
        client = TwitterApplication.getRestClient();
    }

    @SuppressWarnings("unchecked")
    public void populateFromDB() {
        List dbTweets = Tweet.getItems(highestUid, 25);
        aTweets.addAll(dbTweets);
        if (tweets.size() > 1) {
            highestUid = tweets.get(tweets.size() - 1).getUid() - 1;
        }
    }

    public void resetTimeline() {
        highestUid = 0;
        tweets.clear();
    }

    public void insertTweet(Tweet tweet, int index) {
        aTweets.insert(tweet, index);
    }

    public void populateTimeline() {
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void showAlertDialog(String title, String message) {
        if (!dialogShowable) {
            return;
        }
        dialogShowable = false;

        FragmentManager fm = getActivity().getSupportFragmentManager();
        CustomAlertDialogFragment alertDialog = CustomAlertDialogFragment.newInstance(title, message);
        alertDialog.show(fm, "fragment_alert");
    }

    @Override
    public void onOKButton() {
        dialogShowable = true;
    }

    public void addAll(List<Tweet> newTweets) {
        aTweets.addAll(newTweets);
        highestUid = tweets.get(tweets.size() - 1).getUid() - 1;
        swipeContainer.setRefreshing(false);
    }
}

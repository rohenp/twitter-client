package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class HomeTimelineFragment extends TweetsListFragment {

    @Override
    public void populateTimeline() {
        if (!isNetworkAvailable()) {
            populateFromDB();
            swipeContainer.setRefreshing(false);

            showAlertDialog("No Internet connection", "Internet access is required to update the timeline.");
        }

        client.getHomeTimeline(highestUid, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addAll(Tweet.fromJSONArray(response));
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                showAlertDialog("Unable to update timeline", "A problem was encountered while trying to update the timeline.");
            }
        });
    }


}

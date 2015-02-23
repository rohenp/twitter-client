package com.codepath.apps.mysimpletweets.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends ActionBarActivity {
    Tweet tweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Date parsedDate;
        String formattedTime = null;
        String tagRetweetString;
        String tagFavoriteString;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_bird);

        tweet = getIntent().getParcelableExtra("tweet");

        TextView tvBody = (TextView) findViewById(R.id.tvBody);
        TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
        TextView tvTime = (TextView) findViewById(R.id.tvTime);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvFavorites = (TextView) findViewById(R.id.tvFavorites);
        TextView tvRetweets = (TextView) findViewById(R.id.tvRetweets);
        ImageView ivRetweetTop = (ImageView) findViewById(R.id.ivRetweetTop);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvRetweeter = (TextView) findViewById(R.id.tvRetweeter);

        tvBody.setText(tweet.getBody());
        tvUsername.setText("@" + tweet.getUser().getScreenName());

        tvName.setText(tweet.getUser().getName());

        Picasso.with(getApplicationContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
        SimpleDateFormat outFormat = new SimpleDateFormat("hh:mm a \u00B7 dd MMM yy", Locale.US);
        dateFormat.setLenient(true);

        try {
            parsedDate = dateFormat.parse(tweet.getCreatedAt());
            formattedTime = outFormat.format(parsedDate, new StringBuffer(), new FieldPosition(0)).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (formattedTime != null) {
            tvTime.setText(formattedTime);
        }

        if (tweet.getRetweetingUser() != null) {
            tvRetweeter.setText(tweet.getRetweetingUser().getName() + " retweeted");
            tvRetweeter.setVisibility(View.VISIBLE);
            ivRetweetTop.setVisibility(View.VISIBLE);
        } else {
            tvRetweeter.setVisibility(View.GONE);
            ivRetweetTop.setVisibility(View.GONE);
        }

        if (tweet.getRetweetCount() > 0) {
            if (tweet.getRetweetCount() > 1) {
                tagRetweetString = "RETWEETS";
            } else {
                tagRetweetString = "RETWEET";
            }

            String retweetString = "<b>" + String.valueOf(tweet.getRetweetCount()) + "</b> " + "<font color='#8D9EAA'>" + tagRetweetString + "</font>";
            tvRetweets.setText(Html.fromHtml(retweetString));
            tvRetweets.setVisibility(View.VISIBLE);
        } else {
            tvRetweets.setVisibility(View.GONE);
        }

        if (tweet.getFavoritesCount() > 0) {
            if (tweet.getFavoritesCount() > 1) {
                tagFavoriteString = "FAVORITES";
            } else {
                tagFavoriteString = "FAVORITE";
            }

            String favoriteString = "<b>" + String.valueOf(tweet.getFavoritesCount()) + "</b> " + "<font color='#8D9EAA'>" + tagFavoriteString + "</font>";
            tvFavorites.setText(Html.fromHtml(favoriteString));
            tvFavorites.setVisibility(View.VISIBLE);
        } else {
            tvFavorites.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}

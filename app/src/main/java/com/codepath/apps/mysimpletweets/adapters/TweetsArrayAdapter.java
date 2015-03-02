package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{
    private final Long hour = 1000L * 60L * 60L;
    private final Long day = 1000L * 60L * 60L * 24L;
    private final long minute = 1000L * 60L;

    private static class ViewHolder {
        TextView tvBody;
        TextView tvUsername;
        TextView tvTime;
        TextView tvName;
        TextView tvRetweeter;
        TextView tvRetweets;
        TextView tvFavorites;
        ImageView ivRetweetTop;
        ImageView ivProfileImage;
    }

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1, tweets);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        Long resolution = null;
        Long timeDiff;
        Timestamp timestamp = null;
        Long now = System.currentTimeMillis();
        String relativeTimeString;
        String timeValue;
        String resolutionString;
        Date parsedDate = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvRetweeter = (TextView) convertView.findViewById(R.id.tvRetweeter);
            viewHolder.tvFavorites = (TextView) convertView.findViewById(R.id.tvFavorites);
            viewHolder.tvRetweets = (TextView) convertView.findViewById(R.id.tvRetweets);
            viewHolder.ivRetweetTop = (ImageView) convertView.findViewById(R.id.ivRetweetTop);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US);
        dateFormat.setLenient(true);

        try {
            parsedDate = dateFormat.parse(tweet.getCreatedAt());
            timestamp = new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (parsedDate != null) {
            timeDiff = now - timestamp.getTime();

            if (timeDiff < minute) {
                resolution = DateUtils.SECOND_IN_MILLIS;
                resolutionString = "s";
            } else if (timeDiff < hour) {
                resolution = DateUtils.MINUTE_IN_MILLIS;
                resolutionString = "m";
            } else if (timeDiff < day) {
                resolution = DateUtils.HOUR_IN_MILLIS;
                resolutionString = "h";
            } else if (timeDiff < 7 * day) {
                resolution = DateUtils.DAY_IN_MILLIS;
                resolutionString = "d";
            } else {
                resolutionString = "date";
            }

            if (resolutionString.equals("date")) {
                SimpleDateFormat outFormat = new SimpleDateFormat("M/d/yy", Locale.US);
                String formattedTime = outFormat.format(parsedDate, new StringBuffer(), new FieldPosition(0)).toString();

                viewHolder.tvTime.setText(formattedTime);
            } else if (resolution != null) {
                relativeTimeString = DateUtils.getRelativeTimeSpanString(timestamp.getTime(), now, resolution).toString();

                if (relativeTimeString.equals("Yesterday")) {
                    timeValue = "1";
                } else if (relativeTimeString.equals("in 0 seconds")) {
                    timeValue = "0";
                } else {
                    timeValue = relativeTimeString.split(" ")[0];
                }

                viewHolder.tvTime.setText(timeValue + resolutionString);
            }
        } else {
            viewHolder.tvTime.setText("");
        }

        if (tweet.getRetweetingUser() != null) {
            viewHolder.tvRetweeter.setText(tweet.getRetweetingUser().getName() + " retweeted");
            viewHolder.tvRetweeter.setVisibility(View.VISIBLE);
            viewHolder.ivRetweetTop.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvRetweeter.setVisibility(View.GONE);
            viewHolder.ivRetweetTop.setVisibility(View.GONE);
        }
        viewHolder.tvBody.setText(Html.fromHtml(tweet.getBody()));
        viewHolder.tvName.setText(tweet.getUser().getName());
        viewHolder.tvUsername.setText("@" + tweet.getUser().getScreenName());
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), ProfileActivity.class);
                i.putExtra("uid", tweet.getUser().getUid());
                getContext().startActivity(i);
            }
        });

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);

        if (tweet.getRetweetCount() > 0) {
            viewHolder.tvRetweets.setText(String.valueOf(tweet.getRetweetCount()));
            viewHolder.tvRetweets.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvRetweets.setVisibility(View.GONE);
        }

        if (tweet.getFavoritesCount() > 0) {
            viewHolder.tvFavorites.setText(String.valueOf(tweet.getFavoritesCount()));
            viewHolder.tvFavorites.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvFavorites.setVisibility(View.GONE);
        }

        return convertView;
    }
}

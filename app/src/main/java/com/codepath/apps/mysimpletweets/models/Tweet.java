package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Table(name = "tweets")
public class Tweet extends Model implements Parcelable {

    @Column(name = "favorites_count")
    private int favoritesCount;

    @Column(name = "retweets_count")
    private int retweetCount;

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;

    @Column(name = "body")
    private String body;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "userId")
    private long userId;

    @Column(name = "retweetingUser")
    private long retweetingUserId = -1;

    private User user = null;
    private User retweetingUser = null;

    public User getRetweetingUser() {

        if (retweetingUser == null && retweetingUserId > 0) {
            retweetingUser = User.byUid(retweetingUserId);
        }

        return retweetingUser;
    }

    public int getFavoritesCount() {
        return favoritesCount;
    }

    public int getRetweetCount() {
        return retweetCount;
    }

    public long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        if (user == null) {
            user = User.byUid(userId);
        }

        return user;
    }

    public String getBody() {
        return body;
    }

    public static Tweet fromJSON(JSONObject jsonObject) {
        Tweet tweet = new Tweet();

        try {
            tweet.uid = jsonObject.getLong("id");

            if (jsonObject.has("retweeted_status")) {
                tweet.retweetingUserId = User.fromJSON(jsonObject.getJSONObject("user")).getUid();
                jsonObject = jsonObject.getJSONObject("retweeted_status");
            }

            tweet.body = jsonObject.getString("text");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.userId = User.fromJSON(jsonObject.getJSONObject("user")).getUid();
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoritesCount = jsonObject.getInt("favorite_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray (JSONArray jsonArray) {
        ArrayList <Tweet> tweets = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);

                if (tweet != null) {
                    tweet.save();
                    tweets.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return tweets;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(favoritesCount);
        out.writeInt(retweetCount);
        out.writeLong(uid);
        out.writeLong(userId);
        out.writeLong(retweetingUserId);
        out.writeString(body);
        out.writeString(createdAt);
    }

    public static final Parcelable.Creator<Tweet> CREATOR
            = new Parcelable.Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    private Tweet(Parcel in) {
        favoritesCount = in.readInt();
        retweetCount = in.readInt();
        uid = in.readLong();
        userId = in.readLong();
        retweetingUserId = in.readLong();
        body = in.readString();
        createdAt = in.readString();

        user = User.byUid(userId);

        if (retweetingUserId > 0) {
            retweetingUser = User.byUid(retweetingUserId);
        } else {
            retweetingUser = null;
        }
    }

    public Tweet() {
        super();
    }

    public static List<Tweet> getItems(long highest_uid, long limit) {
        if (highest_uid <= 0) {
            highest_uid = Long.MAX_VALUE;
        }

        return new Select().from(Tweet.class).where("uid <= ?", highest_uid).orderBy("uid DESC").limit(String.valueOf(limit)).execute();
    }
}

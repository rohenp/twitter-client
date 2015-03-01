package com.codepath.apps.mysimpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;


@Table(name = "users")
public class User extends Model implements Parcelable {

    @Column(name = "uid", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long uid;

    @Column(name = "name")
    private String name;

    @Column(name = "screen_name")
    private String screenName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "following_count")
    private long followingCount;

    @Column(name = "followers_count")
    private long followersCount;

    @Column(name = "tweets_count")
    private long tweetsCount;

    @Column(name = "description")
    private String description;

    public String getProfileImageUrl() {
        if (profileImageUrl.length() > 0) {
            String filetype = profileImageUrl.substring(profileImageUrl.lastIndexOf('.') + 1);
            return profileImageUrl.substring(0, profileImageUrl.lastIndexOf('.')) + "_bigger." + filetype;
        }

        return profileImageUrl;
    }

    public long getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public long getFollowingCount() {
        return followingCount;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public long getTweetsCount() {
        return tweetsCount;
    }

    public static User fromJSON(JSONObject jsonObject) {
        User user = new User();

        try {
            user.uid = jsonObject.getLong("id");
            user.screenName = jsonObject.getString("screen_name");
            user.profileImageUrl = jsonObject.getString("profile_image_url");
            user.name = jsonObject.getString("name");
            user.followersCount = jsonObject.getLong("followers_count");
            user.tweetsCount = jsonObject.getLong("statuses_count");
            user.followingCount = jsonObject.getLong("friends_count");
            user.description = jsonObject.getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        user.save();

        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(uid);
        out.writeString(name);
        out.writeString(screenName);
        out.writeString(profileImageUrl);
        out.writeLong(followersCount);
        out.writeLong(followingCount);
        out.writeLong(tweetsCount);
        out.writeString(description);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    private User(Parcel in) {
        uid = in.readLong();
        name = in.readString();
        screenName = in.readString();
        profileImageUrl = in.readString();
        followersCount = in.readLong();
        followingCount = in.readLong();
        tweetsCount = in.readLong();
        description = in.readString();
    }

    public User() {
        super();
    }

    public static User byUid(long uid) {
        return new Select().from(User.class).where("uid = ?", uid).executeSingle();
    }

}

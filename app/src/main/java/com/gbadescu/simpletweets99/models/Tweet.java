package com.gbadescu.simpletweets99.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import io.realm.RealmObject;


@Parcel
public class Tweet extends RealmObject {
    // Define database columns and associated fields

    User user;

    String created_at;

    String text;

    // Make sure to always define this constructor with no arguments
    public Tweet() {
        super();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public Tweet(JSONObject object){
        super();

        try {
            User user = new User();
            this.setUser(user);
            this.getUser().setId_str(object.getJSONObject("user").getString("id"));
            this.getUser().setName(object.getJSONObject("user").getString("name"));
            this.getUser().setScreen_name(object.getJSONObject("user").getString("screen_name"));
            this.getUser().setProfile_image_url(object.getJSONObject("user").getString("profile_image_url"));
            this.setCreated_at(object.getString("created_at"));
            this.setText(object.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        final ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {

            try {
                final JSONObject tweetJson = jsonArray.getJSONObject(i);

                final Tweet tweet = new Tweet(tweetJson);

                tweets.add(tweet);

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }


        }

        return tweets;
    }
}

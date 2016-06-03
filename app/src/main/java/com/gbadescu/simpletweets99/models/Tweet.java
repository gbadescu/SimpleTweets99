package com.gbadescu.simpletweets99.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Table(name = "Tweets")
public class Tweet extends Model {
    // Define database columns and associated fields
    @Column(name = "userId")
    String userId;
    @Column(name = "userHandle")
    String userHandle;
    @Column(name = "timestamp")
    String timestamp;
    @Column(name = "body")
    String body;

    // Make sure to always define this constructor with no arguments
    public Tweet() {
        super();
    }


    // Add a constructor that creates an object from the JSON response
    public Tweet(JSONObject object){
        super();

        try {
            this.userId = object.getJSONObject("user").getString("id");
            this.userHandle = object.getJSONObject("user").getString("name");
            this.timestamp = object.getString("created_at");
            this.body = object.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Tweet> fromJson(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<Tweet>(jsonArray.length());

        for (int i=0; i < jsonArray.length(); i++) {
            JSONObject tweetJson = null;
            try {
                tweetJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Tweet tweet = new Tweet(tweetJson);
            tweet.save();
            tweets.add(tweet);
        }

        return tweets;
    }
}
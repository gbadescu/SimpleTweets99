package com.gbadescu.simpletweets99.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.RealmObject;



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

    public Tweet(JSONObject object){
        super();

        try {
            User user = new User();
            this.setUser(user);
            this.getUser().setId_str(object.getJSONObject("user").getString("id"));
            this.getUser().setName(object.getJSONObject("user").getString("name"));
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
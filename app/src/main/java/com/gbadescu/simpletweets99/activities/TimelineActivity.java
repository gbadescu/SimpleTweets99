package com.gbadescu.simpletweets99.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.gbadescu.simpletweets99.models.Tweet;
import com.gbadescu.simpletweets99.net.RestApplication;
import com.gbadescu.simpletweets99.net.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        RestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(1, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                Log.d("DEBUG", "timeline: " + jsonArray.toString());
                // Load json array into model classes
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }
}

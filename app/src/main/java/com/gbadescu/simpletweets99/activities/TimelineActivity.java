package com.gbadescu.simpletweets99.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.gbadescu.simpletweets99.adapters.TimelineAdapter;
import com.gbadescu.simpletweets99.application.SimpleTweets99Application;
import com.gbadescu.simpletweets99.models.Tweet;
import com.gbadescu.simpletweets99.net.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;

public class TimelineActivity extends AppCompatActivity {

    private TimelineAdapter timelineAdapter;
    private ListView tweetListView;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        realm = Realm.getDefaultInstance();


        RestClient client = SimpleTweets99Application.getRestClient();
        client.getHomeTimeline(1, new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                Log.d("DEBUG", "timeline: " + jsonArray.toString());
                // Load json array into model classes
                ArrayList<Tweet> tweets = Tweet.fromJson(jsonArray);

                for (int i=0; i<tweets.size();i++) {

                    final Tweet tweet = tweets.get(i);

                    realm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.copyToRealm(tweet);
                        }
                    });
                }

                timelineAdapter = new TimelineAdapter(getApplicationContext(),tweets);

                tweetListView = (ListView) findViewById(R.id.tweetListView);

                tweetListView.setAdapter(timelineAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }
}

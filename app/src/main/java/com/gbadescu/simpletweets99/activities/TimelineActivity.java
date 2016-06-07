package com.gbadescu.simpletweets99.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gbadescu.simpletweets99.adapters.TimelineAdapter;
import com.gbadescu.simpletweets99.application.SimpleTweets99Application;
import com.gbadescu.simpletweets99.models.Tweet;
import com.gbadescu.simpletweets99.net.NetworkStateReceiver;
import com.gbadescu.simpletweets99.net.RestClient;
import com.gbadescu.simpletweets99.utilities.EndlessScrollListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;

public class TimelineActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener{

    private TimelineAdapter timelineAdapter;
    private ListView tweetListView;
    private Realm realm;
    ArrayList<Tweet> tweets;
    SwipeRefreshLayout swipeContainer;
    NetworkStateReceiver networkStateReceiver;
    Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        realm = Realm.getDefaultInstance();

        tweets = new ArrayList<Tweet>();

        timelineAdapter = new TimelineAdapter(getApplicationContext(),tweets);

        tweetListView = (ListView) findViewById(R.id.tweetListView);

        tweetListView.setAdapter(timelineAdapter);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.ic_action_twitter);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                tweets.clear();

                fetchTweets(0);
            }
        });


        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

       fetchTweets(0);

        tweetListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });



        setupListViewListener();

    }

    private boolean setupListViewListener()
    {

        tweetListView.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                        Intent detailIntent = new Intent(TimelineActivity.this,TweetDetail.class);

                        detailIntent.putExtra("tweet", Parcels.wrap(tweets.get(pos)));
                        startActivity(detailIntent);

                        startActivityForResult(detailIntent,200);

                    }
                }
        );

        return true;
    }

    // Append more data into the adapter
    public void customLoadMoreDataFromApi(int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        fetchTweets(offset);
    }


    public void fetchTweets(int page)
    {
        if (isNetworkAvailable()) {
            RestClient client = SimpleTweets99Application.getRestClient();
            client.getHomeTimeline(page, new JsonHttpResponseHandler() {

                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray jsonArray) {
                    Log.d("DEBUG", "timeline: " + jsonArray.toString());
                    // Load json array into model classes
                    tweets = Tweet.fromJson(jsonArray);

                    for (int i = 0; i < tweets.size(); i++) {

                        final Tweet tweet = tweets.get(i);

                        timelineAdapter.add(tweet);

                        realm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {

                                realm.copyToRealm(tweet);
                            }
                        });
                    }

                    timelineAdapter.notifyDataSetChanged();



                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });


        }
        else
        {
            //get tweets from DB
            connectivityMessage("Network unavailable - retrieving saved Timeline");

            RealmResults<Tweet> tweets = realm.where(Tweet.class).findAll();

            timelineAdapter.clear();

            for (int i = 0; i < tweets.size(); i++) {

                final Tweet tweet = tweets.get(i);

                timelineAdapter.add(tweet);

            }

            timelineAdapter.notifyDataSetChanged();




        }

        swipeContainer.setRefreshing(false);
    }

    public void networkAvailable() {

    }


    public void networkUnavailable() {

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
    }

    private boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        optionsMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_timeline, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_compose_tweet) {
            Intent settingsIntent = new Intent(TimelineActivity.this, ComposeTweetActivity.class);
            TimelineActivity.this.startActivity(settingsIntent);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    public void connectivityMessage(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(msg);
        toast.show();
    }
}

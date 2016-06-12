package com.gbadescu.simpletweets99.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.gbadescu.simpletweets99.activities.R;
import com.gbadescu.simpletweets99.activities.TweetDetail;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TimeLine.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TimeLine#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Mention extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Mention() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeLine.
     */
    // TODO: Rename and change types and number of parameters
    public static Mention newInstance(String param1, String param2) {
        Mention fragment = new Mention();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TimelineAdapter timelineAdapter;
    private ListView tweetListView;
    private Realm realm;
    ArrayList<Tweet> tweets;
    SwipeRefreshLayout swipeContainer;
    NetworkStateReceiver networkStateReceiver;
    Menu optionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        realm = Realm.getDefaultInstance();

        tweets = new ArrayList<Tweet>();

        timelineAdapter = new TimelineAdapter(this.getContext(),tweets);

    }

    private boolean setupListViewListener()
    {

        tweetListView.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                        Intent detailIntent = new Intent(getActivity(),TweetDetail.class);

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
            client.getMentionsTimeline(page, new JsonHttpResponseHandler() {

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

                    swipeContainer.setRefreshing(false);

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

            swipeContainer.setRefreshing(false);


        }
    }

    public void networkAvailable() {

    }


    public void networkUnavailable() {

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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




    public void connectivityMessage(String msg){
        Context context = this.getContext();
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(msg);
        toast.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_line, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tweetListView = (ListView) view.findViewById(R.id.tweetListView);

        tweetListView.setAdapter(timelineAdapter);

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
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

    /*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

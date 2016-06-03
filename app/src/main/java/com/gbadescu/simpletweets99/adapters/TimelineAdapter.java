package com.gbadescu.simpletweets99.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gbadescu.simpletweets99.activities.R;
import com.gbadescu.simpletweets99.models.Tweet;

import java.util.ArrayList;

/**
 * Created by gabrielbadescu on 6/2/16.
 */
public class TimelineAdapter extends ArrayAdapter<Tweet> {

    ArrayList<Tweet> tweetList;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Tweet tweet =  tweetList.get(position);

        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_tweet, parent, false);

        TextView tvTweetText = (TextView) convertView.findViewById(R.id.tvTweetText);

        tvTweetText.setText(tweet.getText());

        return convertView;
    }

    @Override
    public Tweet getItem(int position) {
        return tweetList.get(position);
    }


    public TimelineAdapter(Context context, ArrayList<Tweet> tweets) {
        super(context, R.layout.item_tweet,tweets);

        tweetList = tweets;


    }
}

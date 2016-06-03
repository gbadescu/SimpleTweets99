package com.gbadescu.simpletweets99.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.gbadescu.simpletweets99.activities.R;
import com.gbadescu.simpletweets99.models.Tweet;

import java.util.ArrayList;

/**
 * Created by gabrielbadescu on 6/2/16.
 */
public class TimelineAdapter extends ArrayAdapter<Tweet> {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    public TimelineAdapter(Context context, ArrayList<Tweet> tweets) {
        super(context, R.layout.item_tweet,tweets);
    }
}

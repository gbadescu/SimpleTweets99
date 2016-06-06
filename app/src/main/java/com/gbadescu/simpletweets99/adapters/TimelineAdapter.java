package com.gbadescu.simpletweets99.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gbadescu.simpletweets99.activities.R;
import com.gbadescu.simpletweets99.models.Tweet;

import java.util.ArrayList;

/**
 * Created by gabrielbadescu on 6/2/16.
 */
public class TimelineAdapter extends ArrayAdapter<Tweet> {

    ArrayList<Tweet> tweetList;

    private static class ViewHolder {
        public ImageView ivProfileImg;
        public TextView tvTweetText;
        public TextView tvName;
        public TextView tvUsername;
        public TextView tvRelativeDate;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Tweet tweet =  tweetList.get(position);

        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);

            viewHolder.tvTweetText = (TextView)convertView.findViewById(R.id.tvTweetText);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.tvUsername = (TextView)convertView.findViewById(R.id.tvUsername);
            viewHolder.tvRelativeDate = (TextView)convertView.findViewById(R.id.tvRelativeDate);
            viewHolder.ivProfileImg = (ImageView) convertView.findViewById(R.id.ivProfileImg);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvName.setText(tweet.getUser().getName().toString());
        viewHolder.tvUsername.setText("@"+tweet.getUser().getScreen_name().toString());
        viewHolder.tvTweetText.setText(tweet.getText());
        viewHolder.tvRelativeDate.setText(tweet.getRelativeTimeAgo(tweet.getCreated_at()));

        Glide.with(getContext().getApplicationContext())
                .load(Uri.parse(tweet.getUser().getProfile_image_url()))
                .into(viewHolder.ivProfileImg);


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

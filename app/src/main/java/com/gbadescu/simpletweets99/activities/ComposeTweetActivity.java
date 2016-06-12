package com.gbadescu.simpletweets99.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gbadescu.simpletweets99.application.SimpleTweets99Application;
import com.gbadescu.simpletweets99.net.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ComposeTweetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_tweet);

        TextView tweetBody = (TextView) findViewById(R.id.tvTweetText);

        tweetBody.addTextChangedListener(mTextEditorWatcher);

        RestClient client = SimpleTweets99Application.getRestClient();

        client.getVerifyCredentials(new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "posted successfully");


                try {
                    TextView nameView = (TextView) findViewById(R.id.tvName);
                    nameView.setText(jsonObject.getString("name"));

                    TextView usernameView = (TextView) findViewById(R.id.tvUserName);
                    usernameView.setText("@"+jsonObject.getString("screen_name"));

                    ImageView profileImageView = (ImageView) findViewById(R.id.imageView);
                    Picasso.with(getApplicationContext())
                            .load(Uri.parse(jsonObject.getString("profile_image_url")))
                            .resize(150,150).transform(new RoundedCornersTransformation(10, 10)).into(profileImageView);

                }
                catch(JSONException e)
                    {
                        e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                Log.d("DEBUG", "failed");
            }

        });



    }


    public void onTweet(View view)
    {

        TextView tweetBody = (TextView) findViewById(R.id.tvTweetText);

        tweetBody.addTextChangedListener(mTextEditorWatcher);

        RestClient client = SimpleTweets99Application.getRestClient();
        client.postTweet(tweetBody.getText().toString(), new JsonHttpResponseHandler() {

            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                Log.d("DEBUG", "posted successfully");
                Intent refresh = new Intent(getApplicationContext(), TimelineActivity.class);
                startActivity(refresh);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,  Throwable throwable, JSONObject jsonObject) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                connectivityMessage("Duplicate tweet");
                //finish();
            }
        });

    }

    public void connectivityMessage(String msg){
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setText(msg);
        toast.show();
    }





    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            TextView mTextView = (TextView) findViewById(R.id.charsLeft);
            mTextView.setText(String.valueOf(140 - s.length()));
        }

        public void afterTextChanged(Editable s) {
        }
    };

}

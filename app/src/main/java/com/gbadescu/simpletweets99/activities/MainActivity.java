package com.gbadescu.simpletweets99.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gbadescu.simpletweets99.adapters.SmartFragmentStatePagerAdapter;
import com.gbadescu.simpletweets99.application.SimpleTweets99Application;
import com.gbadescu.simpletweets99.fragments.Mention;
import com.gbadescu.simpletweets99.fragments.TimeLine;
import com.gbadescu.simpletweets99.net.RestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {


    private SmartFragmentStatePagerAdapter adapterViewPager;
    private Menu optionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_timeline);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Reached:", "onActivityResult: ");
    }

    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
        private static int NUM_ITEMS = 2;


        private static int[] imageResId = {
                R.drawable.ic_home_black_24dp,
                R.drawable.ic_home_black_24dp

        };

        private static String[] tabTitles = {
                "HOME",
                "MENTIONS"
        };

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return TimeLine.newInstance("0", "Page # 1");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return Mention.newInstance("1", "Page # 2");
                default:
                    return null;
            }
        }


        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            Drawable image = ContextCompat.getDrawable(SimpleTweets99Application.getContext(), imageResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString sb = new SpannableString(tabTitles[position]);
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return sb;
        }

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
            Intent composeIntent = new Intent(MainActivity.this, ComposeTweetActivity.class);
            MainActivity.this.startActivity(composeIntent);
            return false;
        }

        if (id == R.id.action_profile) {
            final Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);

            RestClient client = SimpleTweets99Application.getRestClient();

            client.getVerifyCredentials(new JsonHttpResponseHandler() {

                public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject jsonObject) {
                    Log.d("DEBUG", "posted successfully");


                    try {

                        profileIntent.putExtra("screen_name",jsonObject.getString("screen_name"));

                        MainActivity.this.startActivity(profileIntent);

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


            return false;
        }

        return super.onOptionsItemSelected(item);
    }
}

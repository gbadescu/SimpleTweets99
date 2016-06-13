package com.gbadescu.simpletweets99.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.gbadescu.simpletweets99.fragments.Profile;
import com.gbadescu.simpletweets99.fragments.UserTimeLine;

public class ProfileActivity extends AppCompatActivity {

    Fragment profileFragment;
    Fragment userTimelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String screenName = getIntent().getStringExtra("screen_name");

        profileFragment = Profile.newInstance(screenName,null);

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.flContainer,profileFragment);

        //adds it to execution stack so android can go back to it. activities get added to this backstack by default - fragments do not. thats why if you dont have anything there it just closes the app
        //good to give it a name so you can actually say go to name when a user clicks the back button
        //ft.addToBackStack("one");


        ft.commit();

        userTimelineFragment = UserTimeLine.newInstance(screenName,null);

        FragmentTransaction ft1 = fm.beginTransaction();

        ft1.replace(R.id.userTimelineContainer,userTimelineFragment);

        //adds it to execution stack so android can go back to it. activities get added to this backstack by default - fragments do not. thats why if you dont have anything there it just closes the app
        //good to give it a name so you can actually say go to name when a user clicks the back button
        //ft1.addToBackStack("two");


        ft1.commit();
    }
}

package com.gbadescu.simpletweets99.models;

import org.parceler.Parcel;

import io.realm.RealmObject;

/**
 * Created by gabrielbadescu on 6/3/16.
 */
@Parcel
public class User extends RealmObject {

    private String name;
    private String id_str;
    private String profile_image_url;
    private String screen_name;

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public User()
    {
       super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId_str() {
        return id_str;
    }

    public void setId_str(String id_str) {
        this.id_str = id_str;
    }
}

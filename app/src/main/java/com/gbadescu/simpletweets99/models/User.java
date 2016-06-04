package com.gbadescu.simpletweets99.models;

import io.realm.RealmObject;

/**
 * Created by gabrielbadescu on 6/3/16.
 */
public class User extends RealmObject {

    private String name;
    private String id_str;

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

package com.gbadescu.simpletweets99.application;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.gbadescu.simpletweets99.net.RestClient;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by gabrielbadescu on 6/3/16.
 */
public class SimpleTweets99Application extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        //Realm.deleteRealm(realmConfig); // Delete Realm between app restarts.
        Realm.setDefaultConfiguration(realmConfig);

        SimpleTweets99Application.context = this;

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    public static RestClient getRestClient() {
        return (RestClient) RestClient.getInstance(RestClient.class, SimpleTweets99Application.context);
    }
}

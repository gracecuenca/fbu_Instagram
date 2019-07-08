package com.example.fbu_instagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initializing Parse
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-instagram")
                .clientKey("fbuisawesome")
                .server("http://gracecuenca-fbu-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}

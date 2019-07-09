package com.example.fbu_instagram;

import android.app.Application;

import com.example.fbu_instagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initializing Parse

        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-instagram")
                .clientKey("fbuisawesome")
                .server("http://gracecuenca-fbu-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}

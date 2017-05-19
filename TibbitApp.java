package com.example.theopsyphertxt.tibbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by TheoPsyphertxt on 21/03/2017.
 */

public class TibbitApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize (new Parse.Configuration.Builder (this)
                .applicationId ("sparkling-wildflower-2700")
                .server ("https://evansdomina.me/parse/")
                .build ()
        );
       /* ParseObject tryObj = new ParseObject ("TryNewObject");
        tryObj.put("Me","Myself");
        tryObj.saveInBackground (); */ //creates a new test object
    }
}

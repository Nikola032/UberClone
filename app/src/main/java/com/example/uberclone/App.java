package com.example.uberclone;

import com.parse.Parse;
import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("tVZTWOwHXtOYIO9CV9BJKpHf91huyrEFh4kh506a")
                // if defined
                .clientKey("uJcJP60AM4dGEYFhRxf9bBjiaUUyRWpITM01bV7m")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
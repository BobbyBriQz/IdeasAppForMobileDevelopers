package com.appsbygreatness.ideasappformobiledevelopers.Application;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        //Check for saved preferences for Night mode
        SharedPreferences preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        boolean isNightModeEnabled = preferences.getBoolean("NIGHT MODE", false);

        if(isNightModeEnabled){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }

}

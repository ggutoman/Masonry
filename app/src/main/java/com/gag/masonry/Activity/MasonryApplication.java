package com.gag.masonry.Activity;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

public class MasonryApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);
    }
}

package com.euswag.eu;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by ASUS on 2017/3/9.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}

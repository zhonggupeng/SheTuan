package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.example.asus.shetuan.BR;

/**
 * Created by ASUS on 2017/4/27.
 */

public class PressAnnounce extends BaseObservable {
    private Activity activity;
    public PressAnnounce(Activity activity){
        this.activity = activity;
    }

    @Bindable
    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
        notifyPropertyChanged(BR.announcement);
    }

    private String announcement;

    public void backclick(View view){
        activity.onBackPressed();
    }

}

package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.example.asus.shetuan.BR;

/**
 * Created by ASUS on 2017/4/27.
 */

public class ChangeShetuan extends BaseObservable {
    private Activity activity;
    public ChangeShetuan(Activity activity){
        this.activity = activity;
    }
    private String stannouncement;

    @Bindable
    public String getStannouncement() {
        return stannouncement;
    }

    public void setStannouncement(String stannouncement) {
        this.stannouncement = stannouncement;
        notifyPropertyChanged(BR.stannouncement);
    }

    @Bindable
    public String getStintroduction() {
        return stintroduction;
    }

    public void setStintroduction(String stintroduction) {
        this.stintroduction = stintroduction;
        notifyPropertyChanged(BR.stintroduction);
    }

    private String stintroduction;

    public void backclick(View view){
        activity.onBackPressed();
    }
}

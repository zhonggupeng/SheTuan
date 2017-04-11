package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.view.View;

/**
 * Created by ASUS on 2017/2/18.
 */

public class MoreActivity extends BaseObservable {
    private Activity activity;
    public MoreActivity(Activity activity){
        this.activity = activity;
    }
    public void backclick(View view){
        activity.onBackPressed();
    }
}

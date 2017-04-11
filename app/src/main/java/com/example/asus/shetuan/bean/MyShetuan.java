package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

/**
 * Created by ASUS on 2017/2/24.
 */

public class MyShetuan extends BaseObservable {

    private Activity activity;
    public MyShetuan(Activity activity){
        this.activity = activity;
    }
    public void backclick(View view){
        activity.onBackPressed();
    }
}

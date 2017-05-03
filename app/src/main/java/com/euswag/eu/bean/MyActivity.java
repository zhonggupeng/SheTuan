package com.euswag.eu.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.view.View;

/**
 * Created by ASUS on 2017/2/24.
 */

public class MyActivity extends BaseObservable {

    private Activity activity;
    public MyActivity(Activity activity){
        this.activity = activity ;
    }
    public void backclick(View view){
        activity.onBackPressed();
    }
}

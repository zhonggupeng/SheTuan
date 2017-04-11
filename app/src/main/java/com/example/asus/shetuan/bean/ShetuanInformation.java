package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.view.View;

/**
 * Created by ASUS on 2017/4/4.
 */

public class ShetuanInformation extends BaseObservable {
    private Activity activity;
    public ShetuanInformation(Activity activity){
        this.activity = activity;
    }
    public void backclick(View view){
        activity.onBackPressed();
    }
}

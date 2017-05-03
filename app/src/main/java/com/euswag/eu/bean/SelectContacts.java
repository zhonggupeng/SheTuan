package com.euswag.eu.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.view.View;

/**
 * Created by ASUS on 2017/3/6.
 */

public class SelectContacts extends BaseObservable {
    private Activity activity;
    public SelectContacts(Activity activity){
        this.activity = activity;
    }
    public void backclick(View view){
        activity.onBackPressed();
    }
}

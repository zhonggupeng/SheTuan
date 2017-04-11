package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.view.View;

import com.acker.simplezxing.activity.CaptureActivity;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.funct.PressActivityActivity;
import com.example.asus.shetuan.activity.funct.WriteNoticeActivity;

/**
 * Created by ASUS on 2017/2/27.
 */

public class Function extends BaseObservable {

    private Activity activity;
    public Function(Activity activity){
        this.activity = activity;
    }
    public void noticeclick(View view){
        Intent intent = new Intent(activity, WriteNoticeActivity.class);
        activity.startActivity(intent);
    }
    public void pressactivityclick(View view){
        Intent intent = new Intent(activity, PressActivityActivity.class);
        activity.startActivity(intent);
    }
    public void sweepsweepclick(View view){
        Intent intent = new Intent(activity, CaptureActivity.class);
        activity.startActivityForResult(intent,CaptureActivity.REQ_CODE);
     //   Intent intent = new Intent(activity, CaptureActivity.class);
      //  activity.startActivity(intent);
    }
    public void cloclick(View view){
        activity.onBackPressed();
    }
}

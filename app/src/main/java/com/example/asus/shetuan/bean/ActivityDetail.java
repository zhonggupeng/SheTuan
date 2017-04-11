package com.example.asus.shetuan.bean;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.example.asus.shetuan.BR;

/**
 * Created by ASUS on 2017/4/3.
 */

public class ActivityDetail extends BaseObservable {

    private String phonestring = "15061884797";

    private Activity activity;

    public ActivityDetail(Activity activity) {
        this.activity = activity;
    }

    public void backclick(View view) {
        activity.onBackPressed();
    }

    public void shareclick(View view) {
        //分享到QQ、微信等
    }

    public void callclick(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phonestring.trim()));
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        activity.startActivity(intent);
    }
}

package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.view.View;
import android.widget.Toast;
import com.example.asus.shetuan.activity.MainTabActivity;
import com.example.asus.shetuan.activity.MoreActivityActivity;
import com.example.asus.shetuan.activity.MyActivityActivity;
import com.example.asus.shetuan.activity.MyShetuanActivity;
import com.example.asus.shetuan.activity.SearchActivity;
import com.xys.libzxing.zxing.activity.CaptureActivity;

/**
 * Created by ASUS on 2017/2/18.
 */

public class Homepage extends BaseObservable{
    private Context context;
    public Homepage(Context context){
        this.context = context;
    }
    public void myactivityclick(View view){
        Intent intent = new Intent(context, MyActivityActivity.class);
        context.startActivity(intent);
    }
    public void myshetuanclick(View view){
        Intent intent = new Intent(context, MyShetuanActivity.class);
        context.startActivity(intent);
    }
    public void searchviewclick(View view){
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }
    public void scanimageclick(View view){
        MainTabActivity activity = (MainTabActivity) context ;
        Intent intent = new Intent(activity,CaptureActivity.class);
        activity.startActivityForResult(intent, 0);
    }
}

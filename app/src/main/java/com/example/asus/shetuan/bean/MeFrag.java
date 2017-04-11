package com.example.asus.shetuan.bean;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.view.View;

import com.example.asus.shetuan.activity.ActivityDetailActivity;
import com.example.asus.shetuan.activity.MyActivityActivity;
import com.example.asus.shetuan.activity.MyShetuanActivity;

/**
 * Created by ASUS on 2017/2/14.
 */

public class MeFrag extends BaseObservable{
    private Context context;

    public MeFrag(Context context){
        this.context = context ;
    }
    public void MyNicknameclick(View view){

    }
    public void MyShetuanclick(View view){
        Intent intent = new Intent(context, MyShetuanActivity.class);
        context.startActivity(intent);
    }
    public void MyActivityclick(View view){
        Intent intent = new Intent(context, MyActivityActivity.class);
        context.startActivity(intent);
    }
    public void MyCollectionclick(View view){

    }
    public void SystemServiceclick(View view){

    }
    public void Setupclick(View view){

    }
    public void Aboutusclick(View view){

    }
    public void Shareusclick(View view){
        Intent intent = new Intent(context, ActivityDetailActivity.class);
        context.startActivity(intent);
    }
}

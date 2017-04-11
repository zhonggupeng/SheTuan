package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;
import android.widget.Toast;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.activity.login.InputPINActivity;
import com.example.asus.shetuan.activity.login.LoginActivity;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ASUS on 2016/12/8.
 */

public class Phone extends BaseObservable{

    private String phonenumber;
    private Activity activity;

    public String getIsregister() {
        return isregister;
    }

    public void setIsregister(String isregister) {
        this.isregister = isregister;
    }

    // 0 表示用户想进行注册，1 表示用户忘记密码转入此页面
    private String isregister ;

    public Phone(Activity activity){
        this.activity = activity;
        isregister = "0";
    }

    @Bindable
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
        notifyPropertyChanged(BR.phonenumber);
    }
    public void tendtoLogin(View view){
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
//        view.getContext().startActivity(intent);
    }

}

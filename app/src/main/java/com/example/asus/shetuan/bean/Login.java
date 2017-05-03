package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;
import android.widget.Toast;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.activity.MainTabActivity;
import com.example.asus.shetuan.activity.login.InputPINActivity;
import com.example.asus.shetuan.activity.login.InputPhoneActivity;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by ASUS on 2017/1/24.
 */

public class Login extends BaseObservable {

    @Bindable
    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
        notifyPropertyChanged(BR.accountnumber);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    private String accountnumber;
    private String password;

    private Activity activity;
    public Login(Activity activity){
        this.activity = activity;
    }
    public void backclick(View view){
        activity.onBackPressed();
    }
    public void register(View view){
        //立即注册处理
        Intent intent = new Intent(activity, InputPhoneActivity.class);
        activity.startActivity(intent);
    }

}

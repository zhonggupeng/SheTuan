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

    public Phone(Activity activity){
        this.activity = activity;
    }

    @Bindable
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
        notifyPropertyChanged(BR.phonenumber);
    }

}

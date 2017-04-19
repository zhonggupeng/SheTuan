package com.example.asus.shetuan.bean;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.app.AlertDialog;
import android.view.View;


import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.activity.login.SetPasswordActivity;

/**
 * Created by ASUS on 2017/1/14.
 */

public class InformationFill extends BaseObservable{

    @Bindable
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
        notifyPropertyChanged(BR.phonenumber);
    }

    @Bindable
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        notifyPropertyChanged(BR.nickname);
    }

    @Bindable
    public String getAcademe() {
        return academe;
    }

    public void setAcademe(String academe) {
        this.academe = academe;
        notifyPropertyChanged(BR.academe);
    }

    @Bindable
    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
        notifyPropertyChanged(BR.studentid);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPersonalexplaintion() {
        return personalexplaintion;
    }

    public void setPersonalexplaintion(String personalexplaintion) {
        this.personalexplaintion = personalexplaintion;
        notifyPropertyChanged(BR.personalexplaintion);
    }

    private String phonenumber;
    private String nickname;
    private String academe;
    private String studentid;
    private String name;
    private String personalexplaintion;

    private Activity activity;

    public InformationFill(Activity activity){
        this.activity = activity;
    }

    public void backimageclick(View view){
        activity.onBackPressed();
    }

}

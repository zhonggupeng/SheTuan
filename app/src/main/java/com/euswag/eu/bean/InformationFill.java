package com.euswag.eu.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.euswag.eu.BR;

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
    public String getEntryyear() {
        return entryyear;
    }

    public void setEntryyear(String entryyear) {
        this.entryyear = entryyear;
        notifyPropertyChanged(BR.entryyear);
    }


    private String phonenumber;
    private String nickname;
    private String academe;
    private String studentid;
    private String name;
    private String entryyear;

    private Activity activity;

    public InformationFill(Activity activity){
        this.activity = activity;
    }

    public void backimageclick(View view){
        activity.onBackPressed();
    }

}

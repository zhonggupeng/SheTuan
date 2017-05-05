package com.euswag.eu.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.euswag.eu.BR;


/**
 * Created by ASUS on 2017/4/28.
 */

public class ShetuanContacts extends BaseObservable {
    private String name;
    private String avatar;
    private int position;
    private long uid;
    private int grade;
    private int gender;         //男0 女1 保密2
    private String studentid;
    private String academe;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
        notifyPropertyChanged(BR.avatar);
    }

    @Bindable
    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyPropertyChanged(BR.position);
    }

    @Bindable
    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
        notifyPropertyChanged(BR.uid);
    }

    @Bindable
    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
        notifyPropertyChanged(BR.grade);
    }

    @Bindable
    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
        notifyPropertyChanged(BR.gender);
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
    public String getAcademe() {
        return academe;
    }

    public void setAcademe(String academe) {
        this.academe = academe;
        notifyPropertyChanged(BR.academe);
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    private boolean isCheck;

}

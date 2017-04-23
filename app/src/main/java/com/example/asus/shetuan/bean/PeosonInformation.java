package com.example.asus.shetuan.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.asus.shetuan.BR;

/**
 * Created by ASUS on 2017/4/15.
 */

public class PeosonInformation extends BaseObservable {
    private long uid;
    private String headimage;
    private String nickname;
    private String gender;
    private String academe;
    private String studentid;
    private String name;
    private String personalexplaintion;
    private String entryyear;

    @Bindable
    public String getEntryyear() {
        return entryyear;
    }

    public void setEntryyear(String entryyear) {
        this.entryyear = entryyear;
        notifyPropertyChanged(BR.entryyear);
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }

    @Bindable
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        notifyPropertyChanged(BR.nickname);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getPersonalexplaintion() {
        return personalexplaintion;
    }

    public void setPersonalexplaintion(String personalexplaintion) {
        this.personalexplaintion = personalexplaintion;
    }

    public String getReputation() {
        return reputation;
    }

    public void setReputation(String reputation) {
        this.reputation = reputation;
    }

    private String reputation;

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }

    private int verified;
}

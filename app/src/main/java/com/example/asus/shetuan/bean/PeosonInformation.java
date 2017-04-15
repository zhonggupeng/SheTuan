package com.example.asus.shetuan.bean;

import android.databinding.BaseObservable;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAcademe() {
        return academe;
    }

    public void setAcademe(String academe) {
        this.academe = academe;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonalexplaintion() {
        return personalexplaintion;
    }

    public void setPersonalexplaintion(String personalexplaintion) {
        this.personalexplaintion = personalexplaintion;
    }

}

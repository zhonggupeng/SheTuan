package com.euswag.eu.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.euswag.eu.BR;

/**
 * Created by ASUS on 2017/5/6.
 */

public class ShetuanNotification extends BaseObservable{
    private int cmid;
    private long uid;
    private String msg;
    private String sendtime;
    private String sender;

    public int getCmid() {
        return cmid;
    }

    public void setCmid(int cmid) {
        this.cmid = cmid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    @Bindable
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
        notifyPropertyChanged(BR.msg);
    }

    @Bindable
    public String getSendtime() {
        return sendtime;
    }

    public void setSendtime(String sendtime) {
        this.sendtime = sendtime;
        notifyPropertyChanged(BR.sendtime);
    }

    @Bindable
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
        notifyPropertyChanged(BR.sender);
    }

}

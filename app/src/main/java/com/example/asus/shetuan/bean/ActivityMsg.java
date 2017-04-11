package com.example.asus.shetuan.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.widget.ImageView;

import com.example.asus.shetuan.BR;

/**
 * Created by ASUS on 2017/2/16.
 */

public class ActivityMsg extends BaseObservable{
    private int actid;
    private String acttitle;

    @Bindable
    public int getActid() {
        return actid;
    }

    public void setActid(int actid) {
        this.actid = actid;
        notifyPropertyChanged(BR.actid);
    }

    @Bindable
    public String getActendtime() {
        return actendtime;
    }

    public void setActendtime(String actendtime) {
        this.actendtime = actendtime;
        notifyPropertyChanged(BR.actendtime);
    }

    @Bindable
    public double getActprice() {
        return actprice;
    }

    public void setActprice(double actprice) {
        this.actprice = actprice;
        notifyPropertyChanged(BR.actprice);
    }

    @Bindable
    public String getActdetail() {
        return actdetail;
    }

    public void setActdetail(String actdetail) {
        this.actdetail = actdetail;
        notifyPropertyChanged(BR.actdetail);
    }

    @Bindable
    public int getActexpectnum() {
        return actexpectnum;
    }

    public void setActexpectnum(int actexpectnum) {
        this.actexpectnum = actexpectnum;
        notifyPropertyChanged(BR.actexpectnum);
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
    public int getActstate() {
        return actstate;
    }

    public void setActstate(int actstate) {
        this.actstate = actstate;
        notifyPropertyChanged(BR.actstate);
    }
    @Bindable
    public int getActregister() {
        return actregister;
    }

    public void setActregister(int actregister) {
        this.actregister = actregister;
        notifyPropertyChanged(BR.actregister);
    }

    @Bindable
    public String getActenrolldeadline() {
        return actenrolldeadline;
    }

    public void setActenrolldeadline(String actenrolldeadline) {
        this.actenrolldeadline = actenrolldeadline;
        notifyPropertyChanged(BR.actenrolldeadline);
    }

    private String actendtime;
    private double actprice;
    private String actdetail;
    private int actexpectnum;
    private long uid;
    private int actstate;
    private int actregister;
    private String actenrolldeadline;
    private String actaddress;
    private String acttime;
    private String imageurl;

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public ActivityMsg(String acttitle,String actaddress,String acttime,String imageurl){
        this.acttitle = acttitle;
        this.actaddress = actaddress;
        this.acttime = acttime;
        this.imageurl = imageurl;
    }

    @Bindable
    public String getActtitle() {
        return acttitle;
    }

    public void setActtitle(String acttitle) {
        this.acttitle = acttitle;
        notifyPropertyChanged(BR.acttitle);
    }

    @Bindable
    public String getActaddress() {
        return actaddress;
    }

    public void setActaddress(String actaddress) {
        this.actaddress = actaddress;
        notifyPropertyChanged(BR.actaddress);
    }

    @Bindable
    public String getActtime() {
        return acttime;
    }

    public void setActtime(String acttime) {
        this.acttime = acttime;
        notifyPropertyChanged(BR.acttime);
    }
    //是否是自己创建的活动，是为1，不是为0
    private int isbuild;
    public void setIsbuild(int isbuild){
        this.isbuild = isbuild;
    }
    public int getIsbuild(){
        return isbuild;
    }
    private String activityDetailJsonString;
    public void setActivityDetailJsonString(String string){
        this.activityDetailJsonString = string;
    }
    public String getActivityDetailJsonString(){
        return activityDetailJsonString;
    }
}

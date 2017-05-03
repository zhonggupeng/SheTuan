package com.euswag.eu.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.euswag.eu.BR;
import com.euswag.eu.activity.funct.PressActivityActivity;
import com.euswag.eu.model.DateUtils;

/**
 * Created by ASUS on 2017/2/27.
 */

public class PressActivity extends BaseObservable {

    private PressActivityActivity activity;
    public PressActivity(PressActivityActivity activity){
        this.activity = activity;
    }
    public void backclick(View view){
        activity.onBackPressed();
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
        notifyPropertyChanged(BR.starttime);
    }

    @Bindable
    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
        notifyPropertyChanged(BR.endtime);
    }

    @Bindable
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
        notifyPropertyChanged(BR.place);
    }

    @Bindable
    public String getEnrolldeadline() {
        return enrolldeadline;
    }

    public void setEnrolldeadline(String enrolldeadline) {
        this.enrolldeadline = enrolldeadline;
        notifyPropertyChanged(BR.enrolldeadline);
    }

    @Bindable
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getExpectnum() {
        return expectnum;
    }

    public void setExpectnum(String expectnum) {
        this.expectnum = expectnum;
        notifyPropertyChanged(BR.expectnum);
    }

    @Bindable
    public String getRegister() {
        return register;
    }

    @Override
    public String toString() {
        return "PressActivity{" +
                "title='" + title + '\'' +
                ", starttime='" +  DateUtils.data(starttime) + '\'' +
                ", endtime='" + DateUtils.data(endtime) + '\'' +
                ", place='" + place + '\'' +
                ", enrolldeadline='" + DateUtils.data(enrolldeadline) + '\'' +
                ", price='" + price + '\'' +
                ", expectnum='" + expectnum + '\'' +
                ", register='" + register + '\'' +
                ", logo='" + logo + '\'' +
                ", detail='" + detail + '\'' +
                ", avid=" + avid +
                '}';
    }

    public void setRegister(String register) {
        this.register = register;
        notifyPropertyChanged(BR.register);
    }

    @Bindable
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
        notifyPropertyChanged(BR.logo);
    }

    @Bindable
    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
        notifyPropertyChanged(BR.detail);
    }

    private String title;
    private String starttime;
    private String endtime;
    private String place;
    private String enrolldeadline;
    private String price;
    private String expectnum;
    private String register;
    private String logo;
    private String detail;

    public int getAvid() {
        return avid;
    }

    public void setAvid(int avid) {
        this.avid = avid;
    }

    private int avid;

}

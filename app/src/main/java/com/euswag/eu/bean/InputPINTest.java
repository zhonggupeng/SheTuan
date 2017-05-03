package com.euswag.eu.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.euswag.eu.BR;

/**
 * Created by ASUS on 2017/1/20.
 */

public class InputPINTest extends BaseObservable{

    private Activity activity;

    private String edit1;
    private String edit2;
    private String edit3;
    private String edit4;

    private String countdown;

    public InputPINTest(Activity activity){
        this.activity = activity;

    }

    @Bindable
    public String getEdit1() {
        return edit1;
    }

    public void setEdit1(String edit1) {
        this.edit1 = edit1;
        notifyPropertyChanged(BR.edit1);
    }

    @Bindable
    public String getEdit2() {
        return edit2;
    }

    public void setEdit2(String edit2) {
        this.edit2 = edit2;
        notifyPropertyChanged(BR.edit2);
    }

    @Bindable
    public String getEdit3() {
        return edit3;
    }

    public void setEdit3(String edit3) {
        this.edit3 = edit3;
        notifyPropertyChanged(BR.edit3);
    }

    @Bindable
    public String getEdit4() {
        return edit4;
    }

    public void setEdit4(String edit4) {
        this.edit4 = edit4;
        notifyPropertyChanged(BR.edit4);
    }

    @Bindable
    public String getCountdown() {
        return countdown;
    }

    public void setCountdown(String countdown) {
        this.countdown = countdown;
        notifyPropertyChanged(BR.countdown);
    }

    public void backimage(View view){
        activity.onBackPressed();
    }
}

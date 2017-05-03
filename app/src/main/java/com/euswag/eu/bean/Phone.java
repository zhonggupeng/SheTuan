package com.euswag.eu.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.euswag.eu.BR;

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

package com.euswag.eu.bean;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.euswag.eu.BR;

/**
 * Created by ASUS on 2017/1/25.
 */

public class SetPassword extends BaseObservable {

    @Bindable
    public String getConfirmpassword() {
        return confirmpassword;
    }

    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
        notifyPropertyChanged(BR.confirmpassword);
    }

    @Bindable
    public String getSetpassword() {
        return setpassword;
    }

    public void setSetpassword(String setpassword) {
        this.setpassword = setpassword;
        notifyPropertyChanged(BR.setpassword);
    }
    private String setpassword;
    private String confirmpassword;

    private Activity activity;
    public SetPassword(Activity activity){
        this.activity = activity;
    }

    public void backclick(View view){
        activity.onBackPressed();
    }

}

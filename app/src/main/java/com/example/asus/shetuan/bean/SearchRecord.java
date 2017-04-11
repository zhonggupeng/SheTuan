package com.example.asus.shetuan.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.asus.shetuan.BR;

/**
 * Created by ASUS on 2017/3/22.
 */

public class SearchRecord extends BaseObservable {
    @Bindable
    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
        notifyPropertyChanged(BR.record);
    }

    private String record;
    public SearchRecord(String record){
        this.record = record;
    }
}

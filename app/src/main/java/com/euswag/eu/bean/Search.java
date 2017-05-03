package com.euswag.eu.bean;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.BR;
import com.euswag.eu.activity.act.MoreActivityActivity;
import com.euswag.eu.model.SearchDataBase;

/**
 * Created by ASUS on 2017/3/25.
 */

public class Search extends BaseObservable {
    private Activity activity;
    public Search(Activity activity){
        this.activity = activity;
    }

    @Bindable
    public String getSearchtext() {
        return searchtext;
    }

    public void setSearchtext(String searchtext) {
        this.searchtext = searchtext;
        notifyPropertyChanged(BR.searchtext);
    }

    @Bindable
    public int getTotaldata() {
        return totaldata;
    }

    public void setTotaldata(int totaldata) {
        this.totaldata = totaldata;
        notifyPropertyChanged(BR.totaldata);
    }

    private int totaldata;
    private String searchtext;

    public void backclick(View view){
        activity.onBackPressed();
    }
    private SearchDataBase searchDataBase ;
    public void searchclick(View view){
        //将search里面的数据写入本地数据库
        if(searchtext!=null) {
            searchDataBase = new SearchDataBase(activity);
            if (!searchDataBase.hasData(searchtext)) {
                if (searchDataBase.totalData() > 4)
                    searchDataBase.deleteFirstData();
                searchDataBase.insertData(searchtext);
            }
            //启动搜索结果页面

            Intent intent = new Intent(activity, MoreActivityActivity.class);
            activity.startActivity(intent);
            activity.finish();
            //从下一个页面返回时，重新启动activity
        }
        else {
            Toast.makeText(activity,"搜索内容不能为空！",Toast.LENGTH_SHORT).show();
        }
    }
    public void clearrecordclick(View view){
        searchDataBase = new SearchDataBase(activity);
        searchDataBase.deleteData();
        Intent intent = activity.getIntent();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }
}

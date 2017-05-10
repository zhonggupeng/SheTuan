package com.euswag.eu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.databinding.ActivityShowSearchBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ShowSearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ActivityShowSearchBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent getintent;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String activityurl = "/activity/search";
    private RequestBody activitybody;

    private String shetuanurl = "";
    private RequestBody shetuanbody;

    private String peopleurl = "";
    private RequestBody peoplebody;

    private int page = 1;

    private final int ACTIVITYS = 110;
    private final int SHETUANS = 100;
    private final int PEOPLES = 101;
    private final int REFRESH = 111;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_show_search);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        getintent = getIntent();
        click();
    }
    private void click(){
        binding.showSearchBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSearchActivity.this.onBackPressed();
            }
        });
        binding.showSearchSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getintent.getStringExtra("type").equals("activity")){
                    if (binding.showSearchSearchcontent.getText()==null||binding.showSearchSearchcontent.getText().length()==0){
                        Toast.makeText(ShowSearchActivity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
                    }else {
                        activitybody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("keyword",binding.showSearchSearchcontent.getText().toString())
                                .build();
                        new Thread(new SearchActivityRunnable()).start();
                    }
                }else if (getintent.getStringExtra("type").equals("shetuan")){

                }else {

                }
            }
        });
    }

    @Override
    public void onRefresh() {

    }

    private class SearchActivityRunnable implements Runnable{

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(activityurl,activitybody);
                Message message = handler.obtainMessage();
                message.what = ACTIVITYS;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class SearchShetuanRunnable implements Runnable{

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(shetuanurl,shetuanbody);
                Message message = handler.obtainMessage();
                message.what = SHETUANS;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class SearchPeopleRunnable implements Runnable{

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(peopleurl,peoplebody);
                Message message = handler.obtainMessage();
                message.what = PEOPLES;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH:
                    break;
                case ACTIVITYS:
                    String activityresult = (String) msg.obj;
                    if (activityresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(activityresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                //搜索结果为活动数组
                            }else {
                                Toast.makeText(ShowSearchActivity.this,"搜索失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ShowSearchActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SHETUANS:

                    break;
                case PEOPLES:
                    String peopleresult = (String) msg.obj;
                    if (peopleresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(peopleresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                //搜索结果为信息数组
                            }else {
                                Toast.makeText(ShowSearchActivity.this,"搜索失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ShowSearchActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

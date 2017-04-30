package com.example.asus.shetuan.activity.shetuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.RecruitSelectAdapter;
import com.example.asus.shetuan.bean.PeosonInformation;
import com.example.asus.shetuan.bean.ShetuanContacts;
import com.example.asus.shetuan.databinding.ActivityRecruitCheckManageBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RecruitCheckManageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ActivityRecruitCheckManageBinding binding;
    private SharedPreferences sharedPreferences;
    private RecruitSelectAdapter adapter;
    private ArrayList<ShetuanContacts> mData = new ArrayList<>();

    private Intent getintent;

    private OKHttpConnect okHttpConnect;
    private String getcontactsurl = "https://euswag.com/eu/community/contacts";
    private RequestBody getcontactsbody;

    private final int REFRESH = 110;
    private final int GET_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_recruit_check_manage);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new RecruitSelectAdapter(this);
        getintent = getIntent();
        binding.recruitCheckManageRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.recruitCheckManageRefresh.setOnRefreshListener(this);
        binding.recruitCheckManageRefresh.setDistanceToTriggerSync(300);
        binding.recruitCheckManageRefresh.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    private class GetContactsRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getcontactsurl,getcontactsbody);
                Message message = handler.obtainMessage();
                message.what = GET_CONTACTS;
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
                    getcontactsbody = new FormBody.Builder()
                            .add("cmid",String.valueOf(getintent.getIntExtra("cmid",-1)))
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .build();
                    new Thread(new GetContactsRunnable()).start();
                    binding.recruitCheckManageRefresh.setRefreshing(false);
                    break;
                case GET_CONTACTS:
                    String getcontactsresult = (String) msg.obj;
                    if (getcontactsresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getcontactsresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String getcontactsdata = jsonObject.getString("data");
                                JSONTokener jsonTokener = new JSONTokener(getcontactsdata);
                                JSONArray jsonArray = (JSONArray) jsonTokener.nextValue();
                                mData.clear();
                                for (int i=0;i<jsonArray.length();i++){
                                    if (jsonArray.getJSONObject(i).getInt("position")==getintent.getIntExtra("position",-8)) {
                                        ShetuanContacts shetuanContacts = new ShetuanContacts();
                                        shetuanContacts.setUid(jsonArray.getJSONObject(i).getLong("uid"));
                                        shetuanContacts.setAcademe(jsonArray.getJSONObject(i).getString("professionclass"));
                                        shetuanContacts.setAvatar(jsonArray.getJSONObject(i).getString("avatar"));
                                        shetuanContacts.setGender(jsonArray.getJSONObject(i).getInt("gender"));
                                        shetuanContacts.setGrade(jsonArray.getJSONObject(i).getInt("grade"));
                                        shetuanContacts.setName(jsonArray.getJSONObject(i).getString("name"));
                                        shetuanContacts.setPosition(jsonArray.getJSONObject(i).getInt("position"));
                                        shetuanContacts.setStudentid(jsonArray.getJSONObject(i).getString("studentid"));
                                        mData.add(shetuanContacts);
                                    }
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            }else {
                                Toast.makeText(RecruitCheckManageActivity.this,"信息请求失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(RecruitCheckManageActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.recruitCheckManageRecyclerview.removeAllViews();
                    binding.recruitCheckManageRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
}
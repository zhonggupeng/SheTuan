package com.example.asus.shetuan.activity.shetuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.CancelManagerAdapter;
import com.example.asus.shetuan.bean.PeosonInformation;
import com.example.asus.shetuan.bean.ShetuanContacts;
import com.example.asus.shetuan.databinding.ActivityCancelManagerBinding;
import com.example.asus.shetuan.model.OKHttpConnect;
import com.example.asus.shetuan.model.OnItemActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CancelManagerActivity extends AppCompatActivity {
    private ActivityCancelManagerBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent getintent;
    private ArrayList<ShetuanContacts> mData = new ArrayList<>();
    private ArrayList<PeosonInformation> showData = new ArrayList<>();
    private CancelManagerAdapter adapter;
    private int itemposition;

    private OKHttpConnect okHttpConnect;
    private String cancelmanagerurl = "https://euswag.com/eu/community/managecm";
    private RequestBody cancelmanagerbody;

    private String getcontactsurl = "https://euswag.com/eu/community/contacts";
    private RequestBody getcontactsbody;

    private String headimageloadurl = "https://euswag.com/picture/user/";

    private final int CANCEL_MANAGER = 110;
    private final int GET_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_cancel_manager);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new CancelManagerAdapter(this);
        getintent = getIntent();
        binding.cancelManagerRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        getcontactsbody = new FormBody.Builder()
                .add("cmid",String.valueOf(getintent.getIntExtra("cmid",-1)))
                .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                .build();
        new Thread(new GetContactsRunnable()).start();

        click();
    }
    private void click(){
        binding.cancelManagerBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelManagerActivity.this.onBackPressed();
            }
        });
        binding.cancelManagerRecyclerview.setOnItemActionListener(new OnItemActionListener() {
            @Override
            public void OnItemClick(int position) {

            }

            @Override
            public void OnItemDelete(int position) {
                itemposition = position;
                cancelmanagerbody = new FormBody.Builder()
                        .add("uid",String.valueOf(mData.get(position).getUid()))
                        .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                        .add("cmid",String.valueOf(getintent.getIntExtra("cmid",-1)))
                        .add("position","1")
                        .build();
                new Thread(new CancelManagerRunnable()).start();
            }
        });
    }
    private class CancelManagerRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(cancelmanagerurl,cancelmanagerbody);
                Message message = handler.obtainMessage();
                message.what = CANCEL_MANAGER;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
                case GET_CONTACTS:
                    String getcontactsresult = (String) msg.obj;
                    System.out.println("getcontactsresult"+getcontactsresult);
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
                                for (int i=0;i<jsonArray.length();i++){
                                    if (jsonArray.getJSONObject(i).getInt("position")==2) {
                                        ShetuanContacts shetuanContacts = new ShetuanContacts();
                                        shetuanContacts.setUid(jsonArray.getJSONObject(i).getLong("uid"));
                                        shetuanContacts.setAcademe(jsonArray.getJSONObject(i).getString("professionclass"));
                                        shetuanContacts.setAvatar(headimageloadurl+jsonArray.getJSONObject(i).getString("avatar")+".jpg");
                                        shetuanContacts.setGender(jsonArray.getJSONObject(i).getInt("gender"));
                                        shetuanContacts.setGrade(jsonArray.getJSONObject(i).getInt("grade"));
                                        shetuanContacts.setName(jsonArray.getJSONObject(i).getString("name"));
                                        shetuanContacts.setPosition(jsonArray.getJSONObject(i).getInt("position"));
                                        shetuanContacts.setStudentid(jsonArray.getJSONObject(i).getString("studentid"));
                                        mData.add(shetuanContacts);
                                        PeosonInformation peosonInformation = new PeosonInformation();
                                        peosonInformation.setName(shetuanContacts.getName());
                                        peosonInformation.setReputation("管理员");
                                        peosonInformation.setHeadimage(shetuanContacts.getAvatar());
                                        showData.add(peosonInformation);
                                    }
                                }
                                adapter.setmData(showData);
                            }else {
                                Toast.makeText(CancelManagerActivity.this,"信息请求失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CancelManagerActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.cancelManagerRecyclerview.setAdapter(adapter);
                    break;
                case CANCEL_MANAGER:
                    String cancelmanagerresult = (String) msg.obj;
                    if (cancelmanagerresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(cancelmanagerresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                showData.remove(itemposition);
                                mData.remove(itemposition);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(CancelManagerActivity.this,"成功取消该成员的管理员",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(CancelManagerActivity.this,"取消管理员失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CancelManagerActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

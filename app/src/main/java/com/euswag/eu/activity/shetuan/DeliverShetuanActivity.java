package com.euswag.eu.activity.shetuan;

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

import com.euswag.eu.R;
import com.euswag.eu.adapter.DeliverShetuanAdapter;
import com.euswag.eu.bean.PeosonInformation;
import com.euswag.eu.bean.ShetuanContacts;
import com.euswag.eu.databinding.ActivityDeliverShetuanBinding;
import com.euswag.eu.model.OKHttpConnect;
import com.euswag.eu.model.OnItemActionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class DeliverShetuanActivity extends AppCompatActivity {
    private ActivityDeliverShetuanBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent getintent;
    private ArrayList<ShetuanContacts> mData = new ArrayList<>();
    private ArrayList<PeosonInformation> showData = new ArrayList<>();
    private DeliverShetuanAdapter adapter;

    private OKHttpConnect okHttpConnect;
    private String delivershetuanurl = "/community/delivercm";
    private RequestBody delivershetuanbody;

    private String getcontactsurl = "/community/contacts";
    private RequestBody getcontactsbody;

    private String headimageloadurl = "https://eu-1251935523.file.myqcloud.com/user/user";

    private final int DELIVER = 110;
    private final int GET_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deliver_shetuan);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new DeliverShetuanAdapter(this);
        getintent = getIntent();
        binding.deliverShetuanRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
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
        binding.deliverShetuanBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliverShetuanActivity.this.onBackPressed();
            }
        });
        binding.deliverShetuanRecyclerview.setOnItemActionListener(new OnItemActionListener() {
            @Override
            public void OnItemClick(int position) {

            }

            @Override
            public void OnItemDelete(int position) {
                delivershetuanbody = new FormBody.Builder()
                        .add("cmid",String.valueOf(getintent.getIntExtra("cmid",-1)))
                        .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                        .add("oldboss",String.valueOf(getintent.getLongExtra("boss",0)))
                        .add("newboss",String.valueOf(mData.get(position).getUid()))
                        .build();
                new Thread(new DeliverShetuanRunnable()).start();
            }
        });
    }

    private class DeliverShetuanRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(delivershetuanurl,delivershetuanbody);
                Message message = handler.obtainMessage();
                message.what = DELIVER;
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
                                    if (jsonArray.getJSONObject(i).getInt("position")==2||jsonArray.getJSONObject(i).getInt("position")==1) {
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
                                        if (shetuanContacts.getPosition()==2) {
                                            peosonInformation.setReputation("管理员");
                                        }else {
                                            peosonInformation.setReputation("普通成员");
                                        }
                                        peosonInformation.setHeadimage(shetuanContacts.getAvatar());
                                        showData.add(peosonInformation);
                                    }
                                }
                                adapter.setmData(showData);
                            }else {
                                Toast.makeText(DeliverShetuanActivity.this,"信息请求失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(DeliverShetuanActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.deliverShetuanRecyclerview.setAdapter(adapter);
                    break;
                case DELIVER:
                    String deliverresult = (String) msg.obj;
                    if (deliverresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(deliverresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(DeliverShetuanActivity.this,"转交成功",Toast.LENGTH_SHORT).show();
                                DeliverShetuanActivity.this.finish();
                            }else {
                                Toast.makeText(DeliverShetuanActivity.this,"转交社团失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(DeliverShetuanActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

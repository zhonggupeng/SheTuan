package com.example.asus.shetuan.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.ParticipateMemberAdapter;
import com.example.asus.shetuan.bean.PeosonInformation;
import com.example.asus.shetuan.databinding.ActivityRegisterDetailBinding;
import com.example.asus.shetuan.model.NetWorkState;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class RegisterDetailActivity extends AppCompatActivity {
    private ActivityRegisterDetailBinding binding;
    private SharedPreferences sharedPreferences;

    private String registercode;
    private int number;
    private int actid;

    private OKHttpConnect okHttpConnect;
    private String participatenumberurl = "https://euswag.com/eu/activity/memberinfolist";
    private RequestBody participatenumberbody;

    private String closeactivityurl = "https://euswag.com/eu/activity/closeav";
    private RequestBody closeactivitybody;

    private String headimageloadurl = "https://euswag.com/picture/user/";

    private final int GETNUMBER = 110;
    private final int CLOSE_ACTIVITY = 100;

    private ArrayList<PeosonInformation> peosonData = new ArrayList<PeosonInformation>();
    private ParticipateMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register_detail);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new ParticipateMemberAdapter(this);
        Intent intent = getIntent();
        registercode = intent.getStringExtra("registercode");
        number = intent.getIntExtra("number",0);
        actid = intent.getIntExtra("actid",0);
        binding.registerDetailCodedata.setText(registercode);
        binding.registerDetailRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        participatenumberbody = new FormBody.Builder()
                .add("avid",String.valueOf(actid))
                .add("accesstoken",sharedPreferences.getString("accesstoken","00"))
                .add("choice","-1")
                .build();
        if (NetWorkState.checkNetWorkState(RegisterDetailActivity.this)) {
            new Thread(new ParticipateNumberRunnable()).start();
        }
        click();
    }
    private void click(){
        binding.registerDetailBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterDetailActivity.this.onBackPressed();
            }
        });
        binding.registerDetailQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterDetailActivity.this,DetailsQRcodeActivity.class);
                intent.putExtra("title","签到二维码");
                intent.putExtra("type","register");
                intent.putExtra("id",actid);
                intent.putExtra("registercode",registercode);
                RegisterDetailActivity.this.startActivity(intent);
            }
        });
        binding.registerDetailFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RegisterDetailActivity.this).setTitle("提示").setMessage("你确定要结束活动吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeactivitybody = new FormBody.Builder()
                                .add("avid",String.valueOf(actid))
                                .add("accesstoken",sharedPreferences.getString("accesstoken","00"))
                                .build();
                        if (NetWorkState.checkNetWorkState(RegisterDetailActivity.this)) {
                            new Thread(new CloseActivityRunnable()).start();
                        }
                    }
                }).setNegativeButton("取消",null).show();

            }
        });
    }

    private class ParticipateNumberRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(participatenumberurl,participatenumberbody);
                Message message = handler.obtainMessage();
                message.what = GETNUMBER;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class CloseActivityRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(closeactivityurl,closeactivitybody);
                Message message = handler.obtainMessage();
                message.what = CLOSE_ACTIVITY;
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
                case GETNUMBER:
                    String getnumberresult = (String) msg.obj;
                    if (getnumberresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getnumberresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String getnumberdata = jsonObject.getString("data");
                                int nuregisternumber;
                                if (getnumberdata.equals("null")){
                                    nuregisternumber = 0;
                                }else {
                                    JSONTokener memberjsonTokener = new JSONTokener(getnumberdata);
                                    JSONArray memberjsonArray = (JSONArray) memberjsonTokener.nextValue();
                                    nuregisternumber = memberjsonArray.length();
                                    for (int i = 0; i < memberjsonArray.length(); i++) {
                                        PeosonInformation peosonInformation = new PeosonInformation();
                                        peosonInformation.setName(memberjsonArray.getJSONObject(i).getString("nickname"));
                                        peosonInformation.setHeadimage(headimageloadurl + memberjsonArray.getJSONObject(i).getString("avatar") + ".jpg");
                                        peosonInformation.setReputation("节操值 " + memberjsonArray.getJSONObject(i).getInt("reputation"));
                                        peosonInformation.setVerified(memberjsonArray.getJSONObject(i).getInt("verified"));
                                        peosonData.add(peosonInformation);
                                    }
                                    adapter.setmData(peosonData);
                                }
                                binding.registerDetailRegisterd.setText("已签到："+(number-nuregisternumber));
                                binding.registerDetailUnregister.setText("未签到："+nuregisternumber);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(RegisterDetailActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.registerDetailRecyclerview.setAdapter(adapter);
                    break;
                case CLOSE_ACTIVITY:
                    String closeactivityresult = (String) msg.obj;
                    if (closeactivityresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(closeactivityresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(RegisterDetailActivity.this,"活动已关闭",Toast.LENGTH_SHORT).show();
                                RegisterDetailActivity.this.finish();
                            }else {
                                Toast.makeText(RegisterDetailActivity.this,"关闭活动失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(RegisterDetailActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

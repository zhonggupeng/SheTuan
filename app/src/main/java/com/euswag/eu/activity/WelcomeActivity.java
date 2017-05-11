package com.euswag.eu.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.login.InputPhoneActivity;
import com.euswag.eu.databinding.ActivityWelcomeBinding;
import com.euswag.eu.model.NetWorkState;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class WelcomeActivity extends AppCompatActivity {
    private OKHttpConnect okHttpConnect;
    private String requesturl = "/info/introinfo";
    private RequestBody requestBody;

    private final int REQUEST = 110;

    private SharedPreferences sharedPreferences;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        intent = new Intent();
//        binding.welcomeImage.setImageURI("res://drawable/"+R.mipmap.loading);
        //设置延迟，可以在此新建另一个线程，进行初始化工作，如判断SD，网络状态等
        handler.postDelayed(new SplashHandler(),1000);
//        handler.post(new SplashHandler());
    }
    private class SplashHandler implements Runnable{
        @Override
        public void run() {
            if (sharedPreferences.getString("phonenumber", "").equals("")){
                intent.setClass(WelcomeActivity.this, InputPhoneActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }else {
                JPushInterface.setAlias(getApplication(),sharedPreferences.getString("phonenumber", ""),null);
                requestBody = new FormBody.Builder()
                        .add("uid", sharedPreferences.getString("phonenumber", ""))
                        .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                        .build();
                if (NetWorkState.checkNetWorkState(WelcomeActivity.this)) {
                    new Thread(new RequestRunnable()).start();
                }else {
                    intent.setClass(WelcomeActivity.this, InputPhoneActivity.class);
                    WelcomeActivity.this.startActivity(intent);
                    WelcomeActivity.this.finish();
                }
            }

        }
    }
    private class RequestRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(requesturl,requestBody);
                Message message = handler.obtainMessage();
                message.what = REQUEST;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                intent.setClass(WelcomeActivity.this, InputPhoneActivity.class);
                WelcomeActivity.this.startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REQUEST:
                    String requeststring = (String) msg.obj;
                    if (requeststring.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(requeststring);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                intent.setClass(WelcomeActivity.this,MainTabActivity.class);
                                WelcomeActivity.this.startActivity(intent);
                                WelcomeActivity.this.finish();
                            }else {
                                intent.setClass(WelcomeActivity.this, InputPhoneActivity.class);
                                WelcomeActivity.this.startActivity(intent);
                                WelcomeActivity.this.finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            intent.setClass(WelcomeActivity.this, InputPhoneActivity.class);
                            WelcomeActivity.this.startActivity(intent);
                            WelcomeActivity.this.finish();
                        }
                    }else {
                        Toast.makeText(WelcomeActivity.this,"网络异常",Toast.LENGTH_LONG).show();
                        intent.setClass(WelcomeActivity.this, InputPhoneActivity.class);
                        WelcomeActivity.this.startActivity(intent);
                        WelcomeActivity.this.finish();
                    }
                    break;
                default:break;
            }
        }
    };
}

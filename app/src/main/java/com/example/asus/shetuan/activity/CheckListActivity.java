package com.example.asus.shetuan.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.databinding.ActivityCheckListBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class CheckListActivity extends AppCompatActivity {

    private ActivityCheckListBinding binding;
    private SharedPreferences sharedPreferences;

    private OKHttpConnect okHttpConnect;
    private String requestmemberurl = "https://euswag.com/eu/activity/memberinfolist";
    private String requestmemberparam1;
    private String requestmemberparam2;
    private String requestmemberparam3 = "&choice=0";

    private final int REQUEST_PHONE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_check_list);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        requestmemberparam1 = "?avid="+intent.getIntExtra("actid",-1);
        System.out.println("requestphoneparam1: "+requestmemberparam1);
        requestmemberparam2 = "&accesstoken="+sharedPreferences.getString("accesstoken","00");
        new Thread(new RequestMemberRunnable()).start();
    }

    private class RequestMemberRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(requestmemberurl +requestmemberparam1+requestmemberparam2+requestmemberparam3);
                Message message = handler.obtainMessage();
                message.what = REQUEST_PHONE;
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
                case REQUEST_PHONE:
                    String requestphoneresult = (String) msg.obj;
                    if (requestphoneresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(requestphoneresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){

                            }else {
                                Toast.makeText(CheckListActivity.this,"参加活动成员的数据请求失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CheckListActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

package com.euswag.eu.activity.notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.databinding.ActivityPressNotificationBinding;
import com.euswag.eu.model.OKHttpConnect;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class PressNotificationActivity extends AppCompatActivity {
    private ActivityPressNotificationBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent getintent;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String pressnotificationurl = "/push/activitytagpush";
    private RequestBody pressnotificationbody;

    private String shetuannotificationurl = "/push/communitytagpush";
    private RequestBody shetuannotificationbody;

    private final int PRESS = 110;
    private final int SHETUAN_NOTIFICATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_press_notification);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        getintent = getIntent();
        click();
    }
    private void click(){
        binding.pressNotificationBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PressNotificationActivity.this.onBackPressed();
            }
        });
        binding.pressNotificationPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.pressNotificationContent.getText()==null||binding.pressNotificationContent.getText().length()==0){
                    Toast.makeText(PressNotificationActivity.this,"请填写需要发送的内容",Toast.LENGTH_SHORT).show();
                }else {
                    if (getintent.getStringExtra("type").equals("av")) {
                        pressnotificationbody = new FormBody.Builder()
                                .add("alert", binding.pressNotificationContent.getText().toString())
                                .add("avid", String.valueOf(getintent.getIntExtra("id", 0)))
                                .add("avname", getintent.getStringExtra("name"))
                                .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                                .build();
                        new Thread(new PressNotificationRunnable()).start();
                    }else if(getintent.getStringExtra("type").equals("cm")){
                        shetuannotificationbody = new FormBody.Builder()
                                .add("alert", binding.pressNotificationContent.getText().toString())
                                .add("cmid", String.valueOf(getintent.getIntExtra("id", 0)))
                                .add("cmname", getintent.getStringExtra("name"))
                                .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                                .build();
                        new Thread(new ShetuanNotificationRunnable()).start();
                    }
                }
            }
        });
    }

    private class PressNotificationRunnable implements Runnable{

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(pressnotificationurl,pressnotificationbody);
                Message message = handler.obtainMessage();
                message.what = PRESS;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ShetuanNotificationRunnable implements Runnable{

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(shetuannotificationurl,shetuannotificationbody);
                Message message = handler.obtainMessage();
                message.what = SHETUAN_NOTIFICATION;
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
                case PRESS:
                    String pressresult = (String) msg.obj;
                    System.out.println("tuisong fanhui:  "+pressresult);
                    //TODO
                    break;
                case SHETUAN_NOTIFICATION:
                    //TODO
                    break;
                default:break;
            }
        }
    };
}

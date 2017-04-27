package com.example.asus.shetuan.activity.shetuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.funct.PressActivityActivity;
import com.example.asus.shetuan.bean.PressAnnounce;
import com.example.asus.shetuan.databinding.ActivityPressAnnounceBinding;
import com.example.asus.shetuan.model.NetWorkState;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class PressAnnounceActivity extends AppCompatActivity {
    private ActivityPressAnnounceBinding binding;
    private SharedPreferences sharedPreferences;
    private PressAnnounce pressAnnounce;
    private Intent getintent;

    private OKHttpConnect okHttpConnect;
    private String pressannounceurl = "https://euswag.com/eu/community/changecm";
    private RequestBody pressannoucebody;

    private final int PRESS_ANNOUNCE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_press_announce);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        pressAnnounce = new PressAnnounce(this);
        getintent = getIntent();
        click();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void click(){
        binding.pressAnnouncePress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pressAnnounce.getAnnouncement()==null||pressAnnounce.getAnnouncement().length()==0){
                    Toast.makeText(PressAnnounceActivity.this,"请填写公告",Toast.LENGTH_SHORT).show();
                }else {
                    pressannoucebody = new FormBody.Builder()
                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                            .add("cmid", String.valueOf(getintent.getIntExtra("cmid", 0)))
                            .add("cmAnnouncement", pressAnnounce.getAnnouncement())
                            .build();
                    new Thread(new PressAnnounceRunnable()).start();
                }
            }
        });
    }

    private class PressAnnounceRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(pressannounceurl,pressannoucebody);
                Message message = handler.obtainMessage();
                message.what = PRESS_ANNOUNCE;
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
                case PRESS_ANNOUNCE:
                    String pressannounceresult = (String) msg.obj;
                    if (pressannounceresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(pressannounceresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(PressAnnounceActivity.this,"公告发布成功",Toast.LENGTH_SHORT).show();
                                PressAnnounceActivity.this.finish();
                            }else {
                                Toast.makeText(PressAnnounceActivity.this,"公告发布失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(PressAnnounceActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

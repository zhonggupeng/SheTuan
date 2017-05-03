package com.euswag.eu.activity.shetuan;

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
import com.euswag.eu.databinding.ActivityShetuanRecruitBinding;
import com.euswag.eu.model.OKHttpConnect;
import com.suke.widget.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ShetuanRecruitActivity extends AppCompatActivity {
    private ActivityShetuanRecruitBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent getintent;

    private OKHttpConnect okHttpConnect;
    private String cheangerecruiturl = "https://euswag.com/eu/community/changerecruit";
    private RequestBody changerecruitbody;

    private final int CHANGE_RECRUIT = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shetuan_recruit);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        getintent = getIntent();
        if (getintent.getIntExtra("recruit",-1)==0){
            binding.shetuanRecruitSwitchbutton.setChecked(false);
            binding.shetuanRecruitCheckmanage.setVisibility(View.GONE);
        }else {
            binding.shetuanRecruitSwitchbutton.setChecked(true);
            binding.shetuanRecruitCheckmanage.setVisibility(View.VISIBLE);
            click2();
        }
        click();
    }
    private void click(){
        binding.shetuanRecruitBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShetuanRecruitActivity.this.onBackPressed();
            }
        });
        binding.shetuanRecruitSwitchbutton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked){
                    changerecruitbody = new FormBody.Builder()
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .add("cmid",String.valueOf(getintent.getIntExtra("cmid",-1)))
                            .add("recruit","1")
                            .build();
                    new Thread(new ChangeRecruitRunnable()).start();
                }else {
                    changerecruitbody = new FormBody.Builder()
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .add("cmid",String.valueOf(getintent.getIntExtra("cmid",-1)))
                            .add("recruit","0")
                            .build();
                    new Thread(new ChangeRecruitRunnable()).start();
                }
            }
        });
    }
    private void click2(){
        //待审核
        binding.shetuanRecruitCheckwait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShetuanRecruitActivity.this,RecruitCheckManageActivity.class);
                intent.putExtra("cmid",getintent.getIntExtra("cmid",-1));
                intent.putExtra("position",-3);
                ShetuanRecruitActivity.this.startActivity(intent);
            }
        });
        //待笔试
        binding.shetuanRecruitWritten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShetuanRecruitActivity.this,RecruitCheckManageActivity.class);
                intent.putExtra("cmid",getintent.getIntExtra("cmid",-1));
                intent.putExtra("position",-2);
                ShetuanRecruitActivity.this.startActivity(intent);
            }
        });
        //待面试
        binding.shetuanRecruitInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShetuanRecruitActivity.this,RecruitCheckManageActivity.class);
                intent.putExtra("cmid",getintent.getIntExtra("cmid",-1));
                intent.putExtra("position",-1);
                ShetuanRecruitActivity.this.startActivity(intent);
            }
        });
    }

    private class ChangeRecruitRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(cheangerecruiturl,changerecruitbody);
                Message message = handler.obtainMessage();
                message.what = CHANGE_RECRUIT;
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
                case CHANGE_RECRUIT:
                    String changerecruitresult = (String) msg.obj;
                    if (changerecruitresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(changerecruitresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                if (binding.shetuanRecruitSwitchbutton.isChecked()){
                                    binding.shetuanRecruitCheckmanage.setVisibility(View.VISIBLE);
                                    click2();
                                }else {
                                    binding.shetuanRecruitCheckmanage.setVisibility(View.GONE);
                                }
                            }else {
                                if (binding.shetuanRecruitSwitchbutton.isChecked()){
                                    Toast.makeText(ShetuanRecruitActivity.this,"开启招新失败，请重试",Toast.LENGTH_SHORT).show();
                                    binding.shetuanRecruitSwitchbutton.setChecked(false);
                                }else {
                                    Toast.makeText(ShetuanRecruitActivity.this,"关闭招新失败，请重试",Toast.LENGTH_SHORT).show();
                                    binding.shetuanRecruitSwitchbutton.setChecked(true);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ShetuanRecruitActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

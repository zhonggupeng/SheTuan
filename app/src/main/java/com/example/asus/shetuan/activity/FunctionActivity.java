package com.example.asus.shetuan.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.funct.PressActivityActivity;
import com.example.asus.shetuan.databinding.ActivityFunctionBinding;
import com.example.asus.shetuan.model.NetWorkState;
import com.example.asus.shetuan.model.OKHttpConnect;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class FunctionActivity extends AppCompatActivity {

    private ActivityFunctionBinding binding;
    private Handler handler = new Handler();

    private OKHttpConnect okHttpConnect;
    private String getactivityurl = "https://euswag.com/eu/activity/getactivity";
    private RequestBody getactivitybody;

    private String registerfinishurl = "https://euswag.com/eu/activity/participateregister";
    private RequestBody registerfinishbody;

    private final int GETACTIVITY = 110;
    private final int REGISTER = 100;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_function);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        binding.functionViewPressactivity.setVisibility(View.INVISIBLE);
        binding.functionViewWritenotice.setVisibility(View.INVISIBLE);
        binding.functionViewScanning.setVisibility(View.INVISIBLE);
        //菜单按钮动画
        binding.functionViewCloseimage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_rotate_right));
        //选项动画
        binding.functionViewPressactivity.setVisibility(View.VISIBLE);
        binding.functionViewPressactivity.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_push_bottom_in));
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewWritenotice.setVisibility(View.VISIBLE);
                binding.functionViewWritenotice.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_in));
            }
        }, 100);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewScanning.setVisibility(View.VISIBLE);
                binding.functionViewScanning.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_in));
            }
        }, 200);
        click();

    }
    private void click(){
        binding.mainPublishDialogLlBtnMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.functionViewBacklayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.functionViewPressactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, PressActivityActivity.class);
                intent.putExtra("presstype",0);
                FunctionActivity.this.startActivity(intent);
            }
        });
        binding.functionViewWritenotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this,MyActivityActivity.class);
                intent.putExtra("page",1);
                FunctionActivity.this.startActivity(intent);
            }
        });
        binding.functionViewScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this,CaptureActivity.class);
                FunctionActivity.this.startActivityForResult(intent,0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        binding.functionViewCloseimage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_rotate_left));
        binding.functionViewPressactivity.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_push_bottom_out));
        binding.functionViewPressactivity.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewWritenotice.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_out));
                binding.functionViewWritenotice.setVisibility(View.INVISIBLE);
            }
        }, 50);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewScanning.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_out));
                binding.functionViewScanning.setVisibility(View.INVISIBLE);
            }
        }, 100);
        super.onBackPressed();
        overridePendingTransition(0,R.anim.mainactivity_fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            String resultstring = bundle.getString("result");
            if (resultstring.indexOf("www.euswag.com?")==0) {
                String[] resultarray = resultstring.split("\\?|=");
                if (resultarray.length==3) {
                    if (resultarray[1].equals("avid")) {
                        getactivitybody = new FormBody.Builder()
                                .add("avid",resultarray[2])
                                .add("accesstoken",sharedPreferences.getString("accesstoken", "00"))
                                .build();
                        if (NetWorkState.checkNetWorkState(FunctionActivity.this)) {
                            new Thread(new GetAcitivityRunnable()).start();
                        }
                    }else {
                        //
                        //转到社团
                    }
                } else {
                    registerfinishbody = new FormBody.Builder()
                            .add("uid",sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken",sharedPreferences.getString("accesstoken", "00"))
                            .add("avid",resultarray[2])
                            .build();
                    if (NetWorkState.checkNetWorkState(FunctionActivity.this)) {
                        new Thread(new RegisterFinishRunnable()).start();
                    }
                }
            }else {
                Toast.makeText(this, bundle.getString("result"), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class GetAcitivityRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getactivityurl,getactivitybody);
                Message message = nethandler.obtainMessage();
                message.what = GETACTIVITY;
                message.obj = resultstring;
                nethandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class RegisterFinishRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(registerfinishurl,registerfinishbody);
                Message message = nethandler.obtainMessage();
                message.what = REGISTER;
                message.obj = resultstring;
                nethandler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler nethandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GETACTIVITY:
                    String getactivityresult = (String) msg.obj;
                    if (getactivityresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getactivityresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Intent intent = new Intent(FunctionActivity.this,ActivityDetailActivity.class);
                                intent.putExtra("datajson1",jsonObject.getString("data"));
                                intent.putExtra("isparticipate","0");
                                FunctionActivity.this.startActivity(intent);
                            }else {
                                Toast.makeText(FunctionActivity.this,"请求活动详情失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(FunctionActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REGISTER:
                    String registerresult = (String) msg.obj;
                    if (registerresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(registerresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(FunctionActivity.this,"签到成功",Toast.LENGTH_SHORT).show();
                            }else if (result == 400){
                                Toast.makeText(FunctionActivity.this,"你未参加该活动或该签到二维码已失效",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(FunctionActivity.this,"签到失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(FunctionActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

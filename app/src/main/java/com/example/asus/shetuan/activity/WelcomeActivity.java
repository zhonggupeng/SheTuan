package com.example.asus.shetuan.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.login.InputPhoneActivity;
import com.example.asus.shetuan.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

//    private SharedPreferences sharedPreferences;
//    private Boolean isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWelcomeBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_welcome);
        binding.welcomeImage.setImageURI("res://drawable/"+R.drawable.welcome);
        Handler handler = new Handler();
        //设置延迟，可以在此新建另一个线程，进行初始化工作，如判断SD，网络状态等
        handler.postDelayed(new SplashHandler(),2000);
    }
    private class SplashHandler implements Runnable{
        @Override
        public void run() {
//            sharedPreferences = getSharedPreferences("LoginControl", Context.MODE_PRIVATE);
//            isLogin = sharedPreferences.getBoolean("isLogin",false);
            Intent intent = new Intent();
//            if(isLogin){
//                intent.setClass(WelcomeActivity.this,MainTabActivity.class);
//            }else {
                intent.setClass(WelcomeActivity.this, InputPhoneActivity.class);
//            }
            startActivity(intent);
            finish();
        }
    }
}

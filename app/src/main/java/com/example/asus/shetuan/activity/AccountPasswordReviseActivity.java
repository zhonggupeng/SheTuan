package com.example.asus.shetuan.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.databinding.ActivityAccountPasswordReviseBinding;

public class AccountPasswordReviseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAccountPasswordReviseBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_account_password_revise);
    }
}

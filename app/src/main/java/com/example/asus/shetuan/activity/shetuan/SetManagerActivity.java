package com.example.asus.shetuan.activity.shetuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.databinding.ActivitySetManagerBinding;

public class SetManagerActivity extends AppCompatActivity {
    private ActivitySetManagerBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent getintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_set_manager);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        //sharedPreferences.getString("accesstoken", "")
        getintent = getIntent();
    }
    private void click(){
    }
}

package com.example.asus.shetuan.activity.act;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.fragment.ActivityFragment1;
import com.example.asus.shetuan.activity.fragment.ActivityFragment2;
import com.example.asus.shetuan.adapter.SlidingtabAdapter;
import com.example.asus.shetuan.bean.MyActivity;
import com.example.asus.shetuan.databinding.ActivityMyActivityBinding;

public class MyActivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyActivityBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_my_activity);
        binding.setMyactivity(new MyActivity(this));

        Intent intent = getIntent();
        int page = intent.getIntExtra("page",0);
        Fragment[] fragments = {new ActivityFragment1(),new ActivityFragment2()};
        String[] titles = {"我参加的","我创建的"};
        SlidingtabAdapter slidingtabAdapter = new SlidingtabAdapter(getSupportFragmentManager(),fragments,titles);
        binding.myActivityViewpager.setAdapter(slidingtabAdapter);
        binding.myActivitySlidingtab.setViewPager(binding.myActivityViewpager);
        binding.myActivityViewpager.setCurrentItem(page);
    }
}

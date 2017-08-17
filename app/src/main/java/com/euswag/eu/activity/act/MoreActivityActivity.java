package com.euswag.eu.activity.act;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.euswag.eu.R;
import com.euswag.eu.activity.search.SearchActivity;
import com.euswag.eu.adapter.FirstAdapter;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.bean.MoreActivity;
import com.euswag.eu.databinding.ActivityMoreActivityBinding;

import java.util.ArrayList;

public class MoreActivityActivity extends AppCompatActivity {

    private ArrayList<ActivityMsg> mData = new ArrayList<ActivityMsg>(){
        {
            for (int i = 0; i < 10; i++) add(new ActivityMsg("环蠡湖骑行", "江南大学北门", "2017年01月24日9：00", "http://pic.58pic.com/58pic/13/02/50/57N58PICQIC.jpg"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMoreActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_more_activity);
        binding.moreActivityRecycleView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.moreActivityRecycleView.setAdapter(new FirstAdapter(mData));
        binding.moreActivityScrollview.smoothScrollTo(0,40);
        binding.setMoreActivity(new MoreActivity(this));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,SearchActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}

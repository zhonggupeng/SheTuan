package com.example.asus.shetuan.activity.act;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.ParticipateMemberAdapter;
import com.example.asus.shetuan.bean.PeosonInformation;
import com.example.asus.shetuan.databinding.ActivityParticipateMemberBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;

public class ParticipateMemberActivity extends AppCompatActivity {
    private ActivityParticipateMemberBinding binding;

    private ArrayList<PeosonInformation> peosonData = new ArrayList<PeosonInformation>();
    private ParticipateMemberAdapter adapter;

    private String headimageloadurl = "https://euswag.com/picture/user/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_participate_member);
        adapter = new ParticipateMemberAdapter(this);
        binding.participateMemberRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        Intent intent = getIntent();
        String memberjson = intent.getStringExtra("memberjson");
        //这个是数组解析
        if (memberjson.equals("null")){

        }else {
            JSONTokener memberjsonTokener = new JSONTokener(memberjson);
            JSONArray memberjsonArray = null;
            try {
                memberjsonArray = (JSONArray) memberjsonTokener.nextValue();
                for (int i = 0; i < memberjsonArray.length(); i++) {
                    PeosonInformation peosonInformation = new PeosonInformation();
                    peosonInformation.setName(memberjsonArray.getJSONObject(i).getString("nickname"));
                    peosonInformation.setHeadimage(headimageloadurl + memberjsonArray.getJSONObject(i).getString("avatar") + ".jpg");
                    peosonInformation.setReputation("节操值 " + memberjsonArray.getJSONObject(i).getInt("reputation"));
                    peosonInformation.setVerified(memberjsonArray.getJSONObject(i).getInt("verified"));
                    peosonData.add(peosonInformation);
                }
                adapter.setmData(peosonData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        binding.participateMemberRecyclerview.setAdapter(adapter);
        click();
    }
    private void click(){
        binding.participateMemberBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParticipateMemberActivity.this.onBackPressed();
            }
        });
    }
}

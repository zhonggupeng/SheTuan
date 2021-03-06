package com.euswag.eu.activity.shetuan;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.euswag.eu.R;
import com.euswag.eu.adapter.ParticipateMemberAdapter;
import com.euswag.eu.bean.PeosonInformation;
import com.euswag.eu.databinding.ActivityShetuanMemberBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.util.ArrayList;

public class ShetuanMemberActivity extends AppCompatActivity {
    private ActivityShetuanMemberBinding binding;

    private ArrayList<PeosonInformation> peosonData = new ArrayList<PeosonInformation>();
    private ParticipateMemberAdapter adapter;

    private String headimageloadurl = "https://eu-1251935523.file.myqcloud.com/user/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shetuan_member);
        adapter = new ParticipateMemberAdapter(this);
        binding.shetuanMemberRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
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
                    peosonInformation.setName(memberjsonArray.getJSONObject(i).getString("name"));
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
        binding.shetuanMemberRecyclerview.setAdapter(adapter);
        click();
    }
    private void click(){
        binding.shetuanMemberBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShetuanMemberActivity.this.onBackPressed();
            }
        });
    }
}

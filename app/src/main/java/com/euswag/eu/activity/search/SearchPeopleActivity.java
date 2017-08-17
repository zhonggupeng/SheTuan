package com.euswag.eu.activity.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.adapter.ParticipateMemberAdapter;
import com.euswag.eu.bean.PeosonInformation;
import com.euswag.eu.databinding.ActivitySearchPeopleBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SearchPeopleActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivitySearchPeopleBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent intent;

    private ArrayList<PeosonInformation> peosonData = new ArrayList<PeosonInformation>();
    private ParticipateMemberAdapter adapter;

    private OKHttpConnect okHttpConnect;
    private String loadpeopleurl = "/info/search";
    private RequestBody loadpeoplebody;

    private String headimageloadurl = "https://eu-1251935523.file.myqcloud.com/user/user";

    private final int REFRESH = 110;
    private final int LOADPEOPLE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_people);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new ParticipateMemberAdapter(this);
        intent = getIntent();
        onRefresh();
        binding.activitySearchPeopleRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.activitySearchPeopleRefresh.setOnRefreshListener(this);
        binding.activitySearchPeopleRefresh.setDistanceToTriggerSync(300);
        binding.activitySearchPeopleRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        click();
    }

    private void click(){
        binding.activitySearchPeopleBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPeopleActivity.this.onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    private class LoadPeopleRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(loadpeopleurl,loadpeoplebody);
                Message message = handler.obtainMessage();
                message.what = LOADPEOPLE;
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
                case REFRESH:
                    loadpeoplebody = new FormBody.Builder()
                            .add("keyword",intent.getStringExtra("keyword"))
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .build();
                    new Thread(new LoadPeopleRunnable()).start();
                    binding.activitySearchPeopleRefresh.setRefreshing(false);
                    break;
                case LOADPEOPLE:
                    String loadcollectionresult = (String) msg.obj;
                    if (loadcollectionresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(loadcollectionresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                binding.activitySearchPeopleRefresh.setRefreshing(false);
                                String collectiondata = jsonObject.getString("data");
                                JSONTokener jsonTokener = new JSONTokener(collectiondata);
                                JSONArray jsonArray ;
                                jsonArray = (JSONArray) jsonTokener.nextValue();
                                peosonData.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    PeosonInformation peosonInformation = new PeosonInformation();
                                    peosonInformation.setName(jsonArray.getJSONObject(i).getString("nickname"));
                                    peosonInformation.setHeadimage(headimageloadurl + jsonArray.getJSONObject(i).getString("avatar") + ".jpg");
                                    peosonInformation.setReputation("节操值 " + jsonArray.getJSONObject(i).getInt("reputation"));
                                    peosonInformation.setVerified(jsonArray.getJSONObject(i).getInt("verified"));
                                    peosonData.add(peosonInformation);
                                }
                                adapter.setmData(null);
                                adapter.setmData(peosonData);
                            }else {
                                Toast.makeText(SearchPeopleActivity.this,"加载失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(SearchPeopleActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.activitySearchPeopleRecyclerview.removeAllViews();
                    binding.activitySearchPeopleRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };

}

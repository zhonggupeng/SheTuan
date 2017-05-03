package com.euswag.eu.activity.act;

import android.content.Context;
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
import com.euswag.eu.adapter.NoLoadmoreActivityRecyclerviewAdapter;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.databinding.ActivityCollectionBinding;
import com.euswag.eu.model.DateUtils;
import com.euswag.eu.model.NetWorkState;
import com.euswag.eu.model.OKHttpConnect;
import com.euswag.eu.weight.VerticalSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ActivityCollectionActivity extends AppCompatActivity implements VerticalSwipeRefreshLayout.OnRefreshListener{
    private ActivityCollectionBinding binding;
    private SharedPreferences sharedPreferences;
    private OKHttpConnect okHttpConnect;

    private String loadcollectionurl = "https://euswag.com/eu/activity/collectedav";
    private RequestBody loadcollectionbody;
    private String imageloadurl = "https://euswag.com/picture/activity/";

    private final int REFRESH = 100;
    private final int LOADCOLLECTION = 110;

    private NoLoadmoreActivityRecyclerviewAdapter adapter;
    private ArrayList<ActivityMsg> mData = new ArrayList<ActivityMsg>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_collection);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new NoLoadmoreActivityRecyclerviewAdapter(this);
        binding.activityCollectionRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        onRefresh();
        binding.activityCollectionRefresh.setOnRefreshListener(this);
        binding.activityCollectionRefresh.setDistanceToTriggerSync(300);
        binding.activityCollectionRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        click();
    }

    private void click(){
        binding.activityCollectionBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollectionActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    private class RefreshCollectionRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(loadcollectionurl,loadcollectionbody);
                Message message = handler.obtainMessage();
                message.what = LOADCOLLECTION;
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
                    loadcollectionbody = new FormBody.Builder()
                            .add("uid",sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken",sharedPreferences.getString("accesstoken", "00"))
                            .build();
                    if (NetWorkState.checkNetWorkState(ActivityCollectionActivity.this)) {
                        new Thread(new RefreshCollectionRunnable()).start();
                    }
                    binding.activityCollectionRefresh.setRefreshing(false);
                    break;
                case LOADCOLLECTION:
                    String refreshresult = (String) msg.obj;
                    if (refreshresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(refreshresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String refreshdata = jsonObject.getString("data");
                                if (refreshdata.equals("null")){

                                }else {
                                    JSONTokener activityRefreshJsonTokener = new JSONTokener(refreshdata);
                                    JSONArray activityRefreshJsonArray = null;
                                    activityRefreshJsonArray = (JSONArray) activityRefreshJsonTokener.nextValue();
                                    mData.clear();
                                    for (int i = 0; i < activityRefreshJsonArray.length(); i++) {
                                        //对于时间要进行处理，即时间格式的转换
                                        ActivityMsg activityMsg = new ActivityMsg(activityRefreshJsonArray.getJSONObject(i).getString("avTitle"), activityRefreshJsonArray.getJSONObject(i).getString("avPlace"), DateUtils.timet(activityRefreshJsonArray.getJSONObject(i).getString("avStarttime")), imageloadurl + activityRefreshJsonArray.getJSONObject(i).getString("avLogo") + ".jpg");
                                        activityMsg.setActivityDetailJsonString(activityRefreshJsonArray.getJSONObject(i).toString());
                                        System.out.println(activityRefreshJsonArray.getJSONObject(i).toString());
                                        activityMsg.setIsbuild(0);
                                        activityMsg.setIsparticipate("4");
                                        mData.add(activityMsg);
                                    }
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            }else {
                                Toast.makeText(ActivityCollectionActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ActivityCollectionActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.activityCollectionRecyclerview.removeAllViews();
                    binding.activityCollectionRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
}

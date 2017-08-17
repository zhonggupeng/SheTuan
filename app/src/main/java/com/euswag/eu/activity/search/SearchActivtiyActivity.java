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
import com.euswag.eu.adapter.NoLoadmoreActivityRecyclerviewAdapter;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.databinding.ActivitySearchActivtiyBinding;
import com.euswag.eu.model.DateUtils;
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

public class SearchActivtiyActivity extends AppCompatActivity implements VerticalSwipeRefreshLayout.OnRefreshListener {

    private ActivitySearchActivtiyBinding binding;
    private SharedPreferences sharedPreferences;
    private OKHttpConnect okHttpConnect;
    private Intent intent;

    private String loadactivityurl="/activity/search";
    private RequestBody loadactivitybody;
    private String imageloadurl = "https://eu-1251935523.file.myqcloud.com/activity/av";

    private final int REFRESH = 100;
    private final int LOADACTIVITY = 110;

    private NoLoadmoreActivityRecyclerviewAdapter adapter;
    private ArrayList<ActivityMsg> mData = new ArrayList<ActivityMsg>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_activtiy);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new NoLoadmoreActivityRecyclerviewAdapter(this);
        intent = getIntent();
        binding.activitySearchActivityRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        onRefresh();
        binding.activitySearchActivityRefresh.setOnRefreshListener(this);
        binding.activitySearchActivityRefresh.setDistanceToTriggerSync(300);
        binding.activitySearchActivityRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        click();
    }

    private void click() {
        binding.activitySearchActivityBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchActivtiyActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    private class LoadActivityRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(loadactivityurl,loadactivitybody);
                Message message = handler.obtainMessage();
                message.what = LOADACTIVITY;
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
            switch (msg.what) {
                case REFRESH:
                    loadactivitybody = new FormBody.Builder()
                            .add("keyword",intent.getStringExtra("keyword"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .build();
                    new Thread(new LoadActivityRunnable()).start();
                    break;
                case LOADACTIVITY:
                    String refreshresult = (String) msg.obj;
                    if (refreshresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(refreshresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                binding.activitySearchActivityRefresh.setRefreshing(false);
                                String refreshdata = jsonObject.getString("data");
                                if (refreshdata.equals("null")) {

                                } else {
                                    JSONTokener activityRefreshJsonTokener = new JSONTokener(refreshdata);
                                    JSONArray activityRefreshJsonArray = null;
                                    activityRefreshJsonArray = (JSONArray) activityRefreshJsonTokener.nextValue();
                                    mData.clear();
                                    for (int i = 0; i < activityRefreshJsonArray.length(); i++) {
                                        //对于时间要进行处理，即时间格式的转换
                                        ActivityMsg activityMsg = new ActivityMsg(activityRefreshJsonArray.getJSONObject(i).getString("avTitle"), activityRefreshJsonArray.getJSONObject(i).getString("avPlace"), DateUtils.timet(activityRefreshJsonArray.getJSONObject(i).getString("avStarttime")), imageloadurl + activityRefreshJsonArray.getJSONObject(i).getString("avLogo") + ".jpg");
                                        activityMsg.setActivityDetailJsonString(activityRefreshJsonArray.getJSONObject(i).toString());
                                        if (sharedPreferences.getString("phonenumber", "0").equals(activityRefreshJsonArray.getJSONObject(i).getLong("uid"))) {
                                            activityMsg.setIsbuild(1);
                                        } else {
                                            activityMsg.setIsbuild(0);
                                        }
                                        activityMsg.setIsparticipate("0");
                                        mData.add(activityMsg);
                                    }
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            } else {
                                Toast.makeText(SearchActivtiyActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SearchActivtiyActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    binding.activitySearchActivityRecyclerview.removeAllViews();
                    binding.activitySearchActivityRecyclerview.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };
}

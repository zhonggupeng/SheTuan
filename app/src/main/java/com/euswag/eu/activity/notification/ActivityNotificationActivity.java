package com.euswag.eu.activity.notification;

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
import com.euswag.eu.adapter.ActivityNotificationAdapter;
import com.euswag.eu.bean.ActivityNotification;
import com.euswag.eu.databinding.ActivityActivityNotificationBinding;
import com.euswag.eu.model.DateUtils;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ActivityNotificationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ActivityActivityNotificationBinding binding;
    private SharedPreferences sharedPreferences;
    private ArrayList<ActivityNotification> mData = new ArrayList<>();
    private ActivityNotificationAdapter adapter;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String activitynotificationurl = "https://euswag.com/eu/notification/activity";
    private RequestBody activitynotificationbody;

    private final int GET_NOTIFICATION = 110;
    private final int REFRESH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_activity_notification);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new ActivityNotificationAdapter(this);
        binding.activityNotificationRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        onRefresh();
        binding.activityNotificationRefresh.setOnRefreshListener(this);
        binding.activityNotificationRefresh.setDistanceToTriggerSync(300);
        binding.activityNotificationRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        click();
    }
    private void click(){
        binding.activityNotificationBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNotificationActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    private class GetNotificationRunnable implements Runnable{

        @Override
        public void run() {
            try {
                String resultstring = okHttpConnect.postdata(activitynotificationurl,activitynotificationbody);
                Message message = handler.obtainMessage();
                message.what = GET_NOTIFICATION;
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
                    activitynotificationbody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", ""))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                            .build();
                    new Thread(new GetNotificationRunnable()).start();
                    break;
                case GET_NOTIFICATION:
                    String getnotifitionresult = (String) msg.obj;
                    if (getnotifitionresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getnotifitionresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                binding.activityNotificationRefresh.setRefreshing(false);
                                String getnotificationdata = jsonObject.getString("data");
                                if (getnotificationdata.equals("null")){

                                }else {
                                    JSONTokener jsonTokener = new JSONTokener(getnotificationdata);
                                    JSONArray jsonArray = (JSONArray) jsonTokener.nextValue();
                                    mData.clear();
                                    for (int i = 0;i<jsonArray.length();i++){
                                        ActivityNotification activityNotification = new ActivityNotification();
                                        activityNotification.setUid(jsonArray.getJSONObject(i).getLong("uid"));
                                        activityNotification.setSendtime(DateUtils.timet3(jsonArray.getJSONObject(i).getString("sendtime")));
                                        activityNotification.setSender(jsonArray.getJSONObject(i).getString("sender"));
                                        activityNotification.setMsg(jsonArray.getJSONObject(i).getString("msg"));
                                        activityNotification.setAvid(jsonArray.getJSONObject(i).getInt("avid"));
                                        mData.add(activityNotification);
                                    }
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            }else {
                                Toast.makeText(ActivityNotificationActivity.this,"获取活动消息失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ActivityNotificationActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.activityNotificationRecyclerview.removeAllViews();
                    binding.activityNotificationRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };
}

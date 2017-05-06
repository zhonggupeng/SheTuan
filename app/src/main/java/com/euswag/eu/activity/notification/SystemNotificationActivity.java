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
import com.euswag.eu.adapter.SystemNotificationAdapter;
import com.euswag.eu.bean.SystremNotification;
import com.euswag.eu.databinding.ActivitySystemNotificationBinding;
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

public class SystemNotificationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private ActivitySystemNotificationBinding binding;
    private SharedPreferences sharedPreferences;
    private ArrayList<SystremNotification> mData = new ArrayList<>();
    private SystemNotificationAdapter adapter;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String systemnotificationurl = "https://euswag.com/eu/notification/system";
    private RequestBody systemnotificationbody;

    private final int GET_NOTIFICATION = 110;
    private final int REFRESH = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_system_notification);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new SystemNotificationAdapter(this);
        binding.systemNotificationRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        onRefresh();
        binding.systemNotificationRefresh.setOnRefreshListener(this);
        binding.systemNotificationRefresh.setDistanceToTriggerSync(300);
        binding.systemNotificationRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        click();
    }
    private void click(){
        binding.systemNotificationBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SystemNotificationActivity.this.onBackPressed();
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
                String resultstring = okHttpConnect.postdata(systemnotificationurl,systemnotificationbody);
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
                    systemnotificationbody = new FormBody.Builder()
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
                                binding.systemNotificationRefresh.setRefreshing(false);
                                String getnotificationdata = jsonObject.getString("data");
                                if (getnotificationdata.equals("null")){

                                }else {
                                    JSONTokener jsonTokener = new JSONTokener(getnotificationdata);
                                    JSONArray jsonArray = (JSONArray) jsonTokener.nextValue();
                                    mData.clear();
                                    for (int i = 0;i<jsonArray.length();i++){
                                        SystremNotification systremNotification = new SystremNotification();
                                        systremNotification.setMsg(jsonArray.getJSONObject(i).getString("msg"));
                                        systremNotification.setSender(jsonArray.getJSONObject(i).getString("sender"));
                                        systremNotification.setSendtime(DateUtils.timet3(jsonArray.getJSONObject(i).getString("sendtime")));
                                        systremNotification.setId(jsonArray.getJSONObject(i).getInt("id"));
                                        systremNotification.setNexturl(jsonArray.getJSONObject(i).getString("nexturl"));
                                        mData.add(systremNotification);
                                    }
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            }else {
                                Toast.makeText(SystemNotificationActivity.this,"获取系统消息失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(SystemNotificationActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.systemNotificationRecyclerview.removeAllViews();
                    binding.systemNotificationRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };
}

package com.euswag.eu.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.act.ActivityDetailActivity;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.bean.ActivityNotification;
import com.euswag.eu.bean.InformationFill;
import com.euswag.eu.databinding.ViewActivityNotificationItemBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by ASUS on 2017/5/6.
 */

public class ActivityNotificationAdapter extends RecyclerView.Adapter<ActivityNotificationAdapter.ActivityHolder>{
    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String getactivityurl = "/activity/getactivity";
    private RequestBody getactivitybody;

    private final int ACITVITY = 110;

    private SharedPreferences sharedPreferences;

    private ArrayList<ActivityNotification> mData = new ArrayList<>();

    public void setmData(ArrayList<ActivityNotification> data){
        mData = data;
    }

    private Activity activity;
    public ActivityNotificationAdapter(Activity activity){
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("token", Context.MODE_PRIVATE);
    }

    @Override
    public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewActivityNotificationItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_activity_notification_item,parent,false);
        ActivityHolder holder = new ActivityHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActivityHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getactivitybody = new FormBody.Builder()
                        .add("avid",String.valueOf(mData.get(position).getAvid()))
                        .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                        .build();
                new Thread(new ActivityRunnable()).start();
            }
        });
        holder.getBinding().setActivityNotification(mData.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ActivityHolder extends RecyclerView.ViewHolder{

        public ActivityHolder(View itemView) {
            super(itemView);
        }

        public ViewActivityNotificationItemBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewActivityNotificationItemBinding binding) {
            this.binding = binding;
        }

        private ViewActivityNotificationItemBinding binding;
    }

    private class ActivityRunnable implements Runnable{

        @Override
        public void run() {
            try {
                String resultstring = okHttpConnect.postdata(getactivityurl,getactivitybody);
                Message message = handler.obtainMessage();
                message.what = ACITVITY;
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
                case ACITVITY:
                    String activitystring = (String) msg.obj;
                    if (activitystring.length()!=0){
                        JSONObject jsonObject;
                        int resuult;
                        try {
                            jsonObject = new JSONObject(activitystring);
                            resuult = jsonObject.getInt("status");
                            if (resuult == 200){
                                String activitydata = jsonObject.getString("data");
                                Intent intent = new Intent(activity, ActivityDetailActivity.class);
                                intent.putExtra("datajson1",activitydata);
                                intent.putExtra("isparticipate","0");
                                activity.startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(activity,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };

}

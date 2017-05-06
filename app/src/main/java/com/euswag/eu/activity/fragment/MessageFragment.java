package com.euswag.eu.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.notification.ActivityNotificationActivity;
import com.euswag.eu.activity.notification.ShetuanNotificationActivity;
import com.euswag.eu.activity.notification.SystemNotificationActivity;
import com.euswag.eu.databinding.FragmentMessageBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding = null ;
    private LayoutInflater inflater;
    private SharedPreferences sharedPreferences;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String getmessageurl = "/notification/msglite";
    private RequestBody getmessagebody;

    private final int GET_MESSAGE = 110;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            this.inflater = inflater;
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
            sharedPreferences = inflater.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            getmessagebody = new FormBody.Builder()
                    .add("uid", sharedPreferences.getString("phonenumber", ""))
                    .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                    .build();
            new Thread(new GetMessageRunnable()).start();
            click();
        }
        return binding.getRoot();
    }

    private void click(){
        binding.fragmentMessageShetuangoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), ShetuanNotificationActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMessageActivitygoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), ActivityNotificationActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMessageSystemgoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), SystemNotificationActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
    }

    private class GetMessageRunnable implements Runnable{

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getmessageurl,getmessagebody);
                Message message = handler.obtainMessage();
                message.what = GET_MESSAGE;
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
                case GET_MESSAGE:
                    String getmessageresult = (String) msg.obj;
                    if (getmessageresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getmessageresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                JSONObject getmessagedata = new JSONObject(jsonObject.getString("data"));
                                binding.fragmentMessageSystem.setText(getmessagedata.getString("systemNotification"));
                                binding.fragmentMessageActivity.setText(getmessagedata.getString("activityNotification"));
                                binding.fragmentMessageShetuan.setText(getmessagedata.getString("communityNotification"));
                            }else if (result == 500){
                                binding.fragmentMessageShetuan.setText("当前没有新的通知");
                                binding.fragmentMessageActivity.setText("当前没有新的通知");
                                binding.fragmentMessageSystem.setText("当前没有新的通知");
                            } else {
                                Toast.makeText(inflater.getContext(),"获取消息失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(inflater.getContext(),"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };

}

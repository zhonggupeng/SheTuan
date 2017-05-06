package com.euswag.eu.activity.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.euswag.eu.R;
import com.euswag.eu.activity.notification.ActivityNotificationActivity;
import com.euswag.eu.activity.notification.ShetuanNotificationActivity;
import com.euswag.eu.activity.notification.SystemNotificationActivity;
import com.euswag.eu.databinding.FragmentMessageBinding;
import com.euswag.eu.model.OKHttpConnect;

import okhttp3.RequestBody;


public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding = null ;
    private LayoutInflater inflater;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String getmessageurl = "https://euswag.com/eu/notification/msglite";
    private RequestBody getmessagebody;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            this.inflater = inflater;
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
            click();
        }
        return binding.getRoot();
    }

    private void click(){
        binding.fragmentMessageShetuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), ShetuanNotificationActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMessageActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), ActivityNotificationActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMessageSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), SystemNotificationActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
    }

}

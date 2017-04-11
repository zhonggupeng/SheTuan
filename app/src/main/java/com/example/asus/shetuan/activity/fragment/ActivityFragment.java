package com.example.asus.shetuan.activity.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.FirstAdapter;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.databinding.FragmentActivityBinding;
import com.example.asus.shetuan.databinding.FragmentFindingBinding;

import java.util.ArrayList;


public class ActivityFragment extends Fragment {
    private FragmentActivityBinding binding = null ;

    private ArrayList<ActivityMsg> mData = new ArrayList<ActivityMsg>(){
        {
            for (int i = 0; i < 10; i++) add(new ActivityMsg("环蠡湖骑行", "江南大学北门", "2017年01月24日9：00","http://pic.58pic.com/58pic/13/02/50/57N58PICQIC.jpg"));
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_activity, container, false);
            binding.fragmentActivityRecyclerview.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            binding.fragmentActivityRecyclerview.setAdapter(new FirstAdapter(mData));
            binding.fragmentActivityScrollview.smoothScrollTo(0,40);
        }
        return binding.getRoot();
    }
}

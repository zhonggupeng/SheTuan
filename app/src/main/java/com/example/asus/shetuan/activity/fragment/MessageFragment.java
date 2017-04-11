package com.example.asus.shetuan.activity.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.databinding.FragmentMessageBinding;


public class MessageFragment extends Fragment {

    private FragmentMessageBinding binding = null ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);
        }
        return binding.getRoot();
    }

}

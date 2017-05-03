package com.euswag.eu.activity.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.euswag.eu.R;
import com.euswag.eu.databinding.FragmentMessageBinding;


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

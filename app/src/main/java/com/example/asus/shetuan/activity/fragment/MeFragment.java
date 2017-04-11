package com.example.asus.shetuan.activity.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.ChangePeosonInformationActivity;
import com.example.asus.shetuan.bean.MeFrag;
import com.example.asus.shetuan.databinding.FragmentMeBinding;


public class MeFragment extends Fragment implements View.OnClickListener{

    private FragmentMeBinding binding = null ;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(binding == null) {
            this.inflater = inflater;
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false);
            binding.setMeFrag(new MeFrag(inflater.getContext()));

            binding.fragmentMeMynickname.setOnClickListener(this);
        }
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(inflater.getContext(), ChangePeosonInformationActivity.class);
        inflater.getContext().startActivity(intent);
    }
}

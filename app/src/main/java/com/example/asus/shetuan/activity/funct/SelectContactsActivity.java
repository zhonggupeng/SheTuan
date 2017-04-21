package com.example.asus.shetuan.activity.funct;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.SelectContacts;
import com.example.asus.shetuan.databinding.ActivitySelectContactsBinding;

public class SelectContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectContactsBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_select_contacts);
        binding.setSelectContacts(new SelectContacts(this));
    }
}

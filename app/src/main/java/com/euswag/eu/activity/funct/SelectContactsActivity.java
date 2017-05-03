package com.euswag.eu.activity.funct;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.euswag.eu.R;
import com.euswag.eu.bean.SelectContacts;
import com.euswag.eu.databinding.ActivitySelectContactsBinding;

public class SelectContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySelectContactsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_select_contacts);
        binding.setSelectContacts(new SelectContacts(this));
    }
}

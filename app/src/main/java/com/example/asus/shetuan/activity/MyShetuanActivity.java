package com.example.asus.shetuan.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.MyShetuan;
import com.example.asus.shetuan.databinding.ActivityMyShetuanBinding;

public class MyShetuanActivity extends AppCompatActivity {

    private String[] sItems = new String[]{"物联网科学技术协会","物联网青年志愿者协会"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMyShetuanBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_my_shetuan);
        binding.setMyshetuan(new MyShetuan(this));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,sItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.shetuanNameSpinner.setAdapter(adapter);
        binding.shetuanNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //设置Spinner点击事件
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}

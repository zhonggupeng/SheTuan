package com.example.asus.shetuan.activity.shetuan;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ShetuanMsg;
import com.example.asus.shetuan.databinding.ActivityManageShetuanBinding;

public class ManageShetuanActivity extends AppCompatActivity {
    private ActivityManageShetuanBinding binding;
    private Intent getintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_manage_shetuan);
        getintent = getIntent();
        click();
    }
    private void click(){
        binding.manageShetuanBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageShetuanActivity.this.onBackPressed();
            }
        });
        binding.manageShetuanMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageShetuanActivity.this,ChangeShetuanActivity.class);
                intent.putExtra("cmLogo",getintent.getStringExtra("cmLogo"));
                intent.putExtra("cmAnnouncement",getintent.getStringExtra("cmAnnouncement"));
                intent.putExtra("cmDetail",getintent.getStringExtra("cmDetail"));
                intent.putExtra("cmid",getintent.getIntExtra("cmid",0));
                ManageShetuanActivity.this.startActivity(intent);
            }
        });
        binding.manageShetuanAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageShetuanActivity.this,PressAnnounceActivity.class);
                intent.putExtra("cmid",getintent.getIntExtra("cmid",0));
                ManageShetuanActivity.this.startActivity(intent);
            }
        });
        binding.manageShetuanSetmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageShetuanActivity.this,SetManagerActivity.class);
                intent.putExtra("cmid",getintent.getIntExtra("cmid",0));
                ManageShetuanActivity.this.startActivity(intent);
            }
        });
    }
}

package com.euswag.eu.activity.shetuan;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.databinding.ActivityManageShetuanBinding;

public class ManageShetuanActivity extends AppCompatActivity {
    private ActivityManageShetuanBinding binding;
    private Intent getintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_manage_shetuan);
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
        binding.manageShetuanMovemanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageShetuanActivity.this,CancelManagerActivity.class);
                intent.putExtra("cmid",getintent.getIntExtra("cmid",0));
                ManageShetuanActivity.this.startActivity(intent);
            }
        });
        binding.manageShetuanMovemember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageShetuanActivity.this,MoveMemberActivity.class);
                intent.putExtra("cmid",getintent.getIntExtra("cmid",0));
                ManageShetuanActivity.this.startActivity(intent);
            }
        });
        binding.manageShetuanDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getintent.getIntExtra("position",0)==3) {
                    Intent intent = new Intent(ManageShetuanActivity.this, DeliverShetuanActivity.class);
                    intent.putExtra("cmid", getintent.getIntExtra("cmid", 0));
                    intent.putExtra("boss", getintent.getLongExtra("boss", 0));
                    ManageShetuanActivity.this.startActivity(intent);
                }else {
                    Toast.makeText(ManageShetuanActivity.this,"您没有该权限",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

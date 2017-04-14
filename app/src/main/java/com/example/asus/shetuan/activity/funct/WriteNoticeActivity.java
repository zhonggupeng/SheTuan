package com.example.asus.shetuan.activity.funct;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;

public class WriteNoticeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewServer.get(this).addWindow(this);
        setContentView(R.layout.activity_write_notice);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).addWindow(this);
    }
}

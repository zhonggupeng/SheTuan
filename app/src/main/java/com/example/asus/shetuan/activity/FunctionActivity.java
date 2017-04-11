package com.example.asus.shetuan.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.Function;
import com.example.asus.shetuan.databinding.ActivityFunctionBinding;

public class FunctionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFunctionBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_function);
        binding.setFunction(new Function(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Toast.makeText(this, data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"没有扫描二维码！",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_open,0);
    }
}

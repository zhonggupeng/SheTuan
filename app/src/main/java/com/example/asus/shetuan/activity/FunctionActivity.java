package com.example.asus.shetuan.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.funct.PressActivityActivity;
import com.example.asus.shetuan.activity.funct.WriteNoticeActivity;
import com.example.asus.shetuan.databinding.ActivityFunctionBinding;
import com.xys.libzxing.zxing.activity.CaptureActivity;

public class FunctionActivity extends AppCompatActivity {

    private ActivityFunctionBinding binding;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_function);
        binding.functionViewPressactivity.setVisibility(View.INVISIBLE);
        binding.functionViewWritenotice.setVisibility(View.INVISIBLE);
        binding.functionViewScanning.setVisibility(View.INVISIBLE);
        //菜单按钮动画
        binding.functionViewCloseimage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_rotate_right));
        //选项动画
        binding.functionViewPressactivity.setVisibility(View.VISIBLE);
        binding.functionViewPressactivity.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_push_bottom_in));
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewWritenotice.setVisibility(View.VISIBLE);
                binding.functionViewWritenotice.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_in));
            }
        }, 100);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewScanning.setVisibility(View.VISIBLE);
                binding.functionViewScanning.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_in));
            }
        }, 200);
        click();

    }
    private void click(){
        binding.mainPublishDialogLlBtnMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.functionViewBacklayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.functionViewPressactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this, PressActivityActivity.class);
                FunctionActivity.this.startActivity(intent);
            }
        });
        binding.functionViewWritenotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this,MyActivityActivity.class);
                intent.putExtra("page",1);
                FunctionActivity.this.startActivity(intent);
            }
        });
        binding.functionViewScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FunctionActivity.this,CaptureActivity.class);
                FunctionActivity.this.startActivityForResult(intent,0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        binding.functionViewCloseimage.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_rotate_left));
        binding.functionViewPressactivity.startAnimation(AnimationUtils.loadAnimation(this, R.anim.mainactivity_push_bottom_out));
        binding.functionViewPressactivity.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewWritenotice.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_out));
                binding.functionViewWritenotice.setVisibility(View.INVISIBLE);
            }
        }, 50);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.functionViewScanning.startAnimation(AnimationUtils.loadAnimation(FunctionActivity.this, R.anim.mainactivity_push_bottom_out));
                binding.functionViewScanning.setVisibility(View.INVISIBLE);
            }
        }, 100);
        super.onBackPressed();
        overridePendingTransition(0,R.anim.mainactivity_fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            Toast.makeText(this,bundle.getString("result"),Toast.LENGTH_SHORT).show();
        }
    }
}

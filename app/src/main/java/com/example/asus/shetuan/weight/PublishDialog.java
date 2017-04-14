package com.example.asus.shetuan.weight;

import android.app.Activity;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.databinding.FunctionViewBinding;


public class PublishDialog extends Dialog {

    private Activity context;

    private Handler handler;
    private FunctionViewBinding binding;

    public PublishDialog(Activity context) {
        this(context, R.style.main_publishdialog_style);
    }

    private PublishDialog(Activity context, int theme) {
        super(context, theme);
        this.context = context;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((WindowManager.LayoutParams) params);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        handler = new Handler();
        //填充视图
        binding = DataBindingUtil.setContentView(context,R.layout.function_view);

        binding.activityFunctionLinear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                outputDialog();
//                dismiss();
            }
        });
        binding.functionViewCloselinear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                outputDialog();
//                dismiss();
            }
        });
    }


    /**
     * 进入对话框（带动画）
     */
    private void inputDialog() {
        binding.activityFunctionPressactivity.setVisibility(View.INVISIBLE);
        binding.activityFunctionWritenotice.setVisibility(View.INVISIBLE);
        binding.activityFunctionScanning.setVisibility(View.INVISIBLE);
        //背景动画
        binding.activityFunctionLinear.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_fade_in));
//        //菜单按钮动画
        binding.functionViewCloseimage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_rotate_right));

        binding.activityFunctionPressactivity.setVisibility(View.VISIBLE);
        binding.activityFunctionPressactivity.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_in));
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.activityFunctionWritenotice.setVisibility(View.VISIBLE);
                binding.activityFunctionWritenotice.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_in));
            }
        }, 50);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.activityFunctionScanning.setVisibility(View.VISIBLE);
                binding.activityFunctionScanning.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_in));
            }
        }, 100);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isShowing()) {
            outputDialog();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

//
//    /**
//     * 取消对话框（带动画）
//     */
    private void outputDialog() {

        //退出动画
        binding.activityFunctionLinear.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_fade_out));
        binding.functionViewCloseimage.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_rotate_left));
        binding.activityFunctionPressactivity.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_out));
        binding.activityFunctionPressactivity.setVisibility(View.INVISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        },400);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.activityFunctionWritenotice.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_out));
                binding.activityFunctionWritenotice.setVisibility(View.INVISIBLE);
            }
        }, 50);
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.activityFunctionScanning.startAnimation(AnimationUtils.loadAnimation(context, R.anim.mainactivity_push_bottom_out));
                binding.activityFunctionScanning.setVisibility(View.INVISIBLE);
            }
        }, 100);

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
        inputDialog();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //全屏
//
//    }


    public PublishDialog setPressactivityClickListener(View.OnClickListener clickListener) {
        binding.activityFunctionPressactivity.setOnClickListener(clickListener);
        return this;
    }

    public PublishDialog setWritenoticeClickListener(View.OnClickListener clickListener) {
        binding.activityFunctionWritenotice.setOnClickListener(clickListener);
        return this;
    }

    public PublishDialog setScanningClickListener(View.OnClickListener clickListener) {
        binding.activityFunctionScanning.setOnClickListener(clickListener);
        return this;
    }

}

package com.example.asus.shetuan.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.acker.simplezxing.activity.CaptureActivity;
import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.fragment.FindingFragment;
import com.example.asus.shetuan.activity.fragment.HomepageFragment;
import com.example.asus.shetuan.activity.fragment.MeFragment;
import com.example.asus.shetuan.activity.fragment.MessageFragment;
import com.example.asus.shetuan.activity.funct.PressActivityActivity;
import com.example.asus.shetuan.activity.funct.WriteNoticeActivity;
import com.example.asus.shetuan.databinding.ActivityMainTabBinding;
import com.example.asus.shetuan.weight.PublishDialog;

public class MainTabActivity extends FragmentActivity {

//    private SharedPreferences sharedPreferences;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomepageFragment.class, FindingFragment.class, null, MessageFragment.class, MeFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.home_selector, R.drawable.find_selector, R.drawable.empty, R.drawable.message_selector, R.drawable.me_selector};

    //Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "发现", "  ", "消息", "我的"};
    private ActivityMainTabBinding binding;

    private PublishDialog publishDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewServer.get(this).addWindow(this);
        //setContentView(R.layout.activity_main_tab);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_tab);

//        sharedPreferences = getSharedPreferences("LoginControl", Context.MODE_PRIVATE);
//        sharedPreferences.edit().putBoolean("isLogin",true).commit();

        //实例化布局对象
        layoutInflater = LayoutInflater.from(this);

        //实例化TabHost对象，得到TabHost
        //mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        binding.tabhost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        //得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = binding.tabhost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            binding.tabhost.addTab(tabSpec, fragmentArray[i], null);

            binding.tabhost.getTabWidget().setDividerDrawable(null);
            //设置Tab按钮的背景
            //  mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.screen);

        }
        click();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void click(){
        binding.imageplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (publishDialog == null){
                    publishDialog = new PublishDialog(MainTabActivity.this);
                    publishDialog.setPressactivityClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainTabActivity.this, PressActivityActivity.class);
                            MainTabActivity.this.startActivity(intent);
                        }
                    });
                    publishDialog.setWritenoticeClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainTabActivity.this, WriteNoticeActivity.class);
                            MainTabActivity.this.startActivity(intent);
                        }
                    });
                    publishDialog.setScanningClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainTabActivity.this,CaptureActivity.class);
                            MainTabActivity.this.startActivityForResult(intent,CaptureActivity.REQ_CODE);
                        }
                    });
                }
                publishDialog.show();
            }
        });
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

        return view;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
                // TODO 监听返回键，相当于点击home键  
        PackageManager pm = getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(
                new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ActivityInfo ai = homeInfo.activityInfo;
                    Intent startIntent = new Intent(Intent.ACTION_MAIN);
                    startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
                    startActivitySafely(startIntent);
                    return true;
                } else return super.onKeyDown(keyCode, event);
    }

    private void startActivitySafely(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "ActivityNotFoundExceptionnull",
                        Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(this, "SecurityExceptionnull", Toast.LENGTH_SHORT).show();
                }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Toast.makeText(this, data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT), Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"没有扫描二维码！",Toast.LENGTH_LONG).show();
        }
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

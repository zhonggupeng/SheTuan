package com.example.asus.shetuan.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.fragment.FindingFragment;
import com.example.asus.shetuan.activity.fragment.HomepageFragment;
import com.example.asus.shetuan.activity.fragment.MeFragment;
import com.example.asus.shetuan.activity.fragment.MessageFragment;
import com.example.asus.shetuan.databinding.ActivityMainTabBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainTabActivity extends FragmentActivity {

    private SharedPreferences sharedPreferences;

    //定义一个布局
    private LayoutInflater layoutInflater;

    //定义数组来存放Fragment界面
    private Class fragmentArray[] = {HomepageFragment.class, FindingFragment.class, null, MessageFragment.class, MeFragment.class};
    //定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.home_selector, R.drawable.find_selector, R.drawable.empty, R.drawable.message_selector, R.drawable.me_selector};

    //Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "发现", "  ", "消息", "我的"};
    private ActivityMainTabBinding binding;

    private OKHttpConnect okHttpConnect;
    private String getactivityurl = "https://euswag.com/eu/activity/getactivity";
    private String getactivityparam1;
    private String getactivityparam2;

    private final int GETACTIVITY = 110;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewServer.get(this).addWindow(this);
        //setContentView(R.layout.activity_main_tab);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main_tab);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);

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
                Intent intent = new Intent(MainTabActivity.this,FunctionActivity.class);
                MainTabActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.mainactivity_fade_in,0);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK){
            Bundle bundle = data.getExtras();
            String resultstring = bundle.getString("result");
            if (resultstring.indexOf("www.euswag.com?")==0) {
                String[] resultarray = resultstring.split("\\?|=");
                if (resultarray[1].equals("avid")) {
                    getactivityparam1 = "?avid="+resultarray[2];
                    getactivityparam2 = "&accesstoken=" + sharedPreferences.getString("accesstoken", "00");
                    new Thread(new GetAcitivityRunnable()).start();
                }
            }else {
                Toast.makeText(this, bundle.getString("result"), Toast.LENGTH_SHORT).show();
            }
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

    private class GetAcitivityRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(getactivityurl+getactivityparam1+getactivityparam2);
                Message message = handler.obtainMessage();
                message.what = GETACTIVITY;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GETACTIVITY:
                    String getactivityresult = (String) msg.obj;
                    if (getactivityresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getactivityresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Intent intent = new Intent(MainTabActivity.this,ActivityDetailActivity.class);
                                intent.putExtra("datajson1",jsonObject.getString("data"));
                                intent.putExtra("isparticipate","0");
                                MainTabActivity.this.startActivity(intent);
                            }else {
                                Toast.makeText(MainTabActivity.this,"请求活动详情失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(MainTabActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

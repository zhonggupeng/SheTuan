package com.example.asus.shetuan.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.bean.PressActivity;
import com.example.asus.shetuan.databinding.ActivityCheckActivityBinding;
import com.example.asus.shetuan.model.DateUtils;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

public class CheckActivityActivity extends AppCompatActivity {

    private ActivityCheckActivityBinding binding;
    private String datajsonstring;
    private ActivityMsg activityMsg=null;

    private String activityimageloadurl = "https://euswag.com/picture/activity/";

    private OKHttpConnect okHttpConnect;
    private String participatenumberurl = "https://euswag.com/eu/activity/memberinfolist";
    private String participatenumberparam1;
    private String participatenumberparam2;
    private String participatenumberparam3 = "&choice=0";

    private String startregisterurl = "https://euswag.com/eu/activity/startregister";
    private String startregisterparam1;
    private String startregisterparam2;
    private String startregisterparam3;

    private String deleteactivityurl = "https://euswag.com/eu/activity/deleteav";
    private String deleteactivityparam1;
    private String deleteactivityparam2;
    private String deleteactivityparam3;

    private final int GETNUMBER = 110;
    private final int START_REGISTER = 100;
    private final int DELETE_ACTIVITY = 101;

    private int number;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_check_activity);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        datajsonstring = intent.getStringExtra("datajson2");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(datajsonstring);
            activityMsg = new ActivityMsg(jsonObject.getString("avTitle"),jsonObject.getString("avPlace"), DateUtils.timet(jsonObject.getString("avStarttime")),activityimageloadurl+jsonObject.getString("avLogo")+".jpg");
            activityMsg.setActendtime(DateUtils.timet(jsonObject.getString("avEndtime")));
            activityMsg.setUid(jsonObject.getLong("uid"));
            activityMsg.setActexpectnum(jsonObject.getInt("avExpectnum"));
            activityMsg.setActprice(jsonObject.getDouble("avPrice"));
            activityMsg.setActdetail(jsonObject.getString("avDetail"));
            activityMsg.setActstate(jsonObject.getInt("avState"));
            activityMsg.setActregister(jsonObject.getInt("avRegister"));
            activityMsg.setActid(jsonObject.getInt("avid"));
            //报名截止时间
            activityMsg.setActenrolldeadline(jsonObject.getString("avEnrolldeadline"));
            System.out.println("报名截止时间："+jsonObject.getString("avEnrolldeadline"));

            binding.setCheckActivityMsg(activityMsg);

            if (activityMsg.getActregister()==-1){
                binding.checkActivityIsregister.setText("无需签到");
//                participateparam4 = "&verifystate=2";
            }
            else {
                binding.checkActivityIsregister.setText("需要签到");
//                participateparam4 = "&verifystate=0";
            }

            if(activityMsg.getActprice()==0){
                binding.checkActivityIsfree.setText("免费");
            }
            else {
                binding.checkActivityIsfree.setText(String.valueOf(activityMsg.getActprice()));
            }
            binding.checkActivityActivitytime.setText(activityMsg.getActtime()+"~"+activityMsg.getActendtime());
            //需要知道已报名人数
            participatenumberparam1 = "?avid="+activityMsg.getActid();
            participatenumberparam2 = "&accesstoken="+sharedPreferences.getString("accesstoken","00");
            new Thread(new ParticipateNumberRunnable()).start();

            binding.checkActivityBackground.setImageURI(activityMsg.getImageurl());
            if (activityMsg.getActstate()==0) {
                click();
            }else {
                binding.checkActivityChangeactivity.setText("活动已结束");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void click(){
        binding.checkActivityBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckActivityActivity.this.onBackPressed();
            }
        });
        binding.checkActivityMoreimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(CheckActivityActivity.this,v, ActionBar.LayoutParams.WRAP_CONTENT);
                popupMenu.getMenuInflater().inflate(R.menu.popupmenu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.popupmenu_delete:
                                deleteactivityparam1 = "?avid="+activityMsg.getActid();
                                deleteactivityparam2 = "&accesstoken="+sharedPreferences.getString("accesstoken","00");
                                deleteactivityparam3 = "&uid="+activityMsg.getUid();
                                new Thread(new DeleteActivityRunnable()).start();
                                break;
                        }
                        return false;
                    }
                });
            }
        });
        binding.checkActivityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckActivityActivity.this,CheckListActivity.class);
                intent.putExtra("actid",activityMsg.getActid());
                CheckActivityActivity.this.startActivity(intent);
            }
        });
        binding.checkActivityQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckActivityActivity.this,DetailsQRcodeActivity.class);
                intent.putExtra("title",activityMsg.getActtitle());
                intent.putExtra("id",activityMsg.getActid());
                intent.putExtra("type","act");
                CheckActivityActivity.this.startActivity(intent);
            }
        });
        binding.checkActivityRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断临近时间，如果临近时间一天，则之间签到，否则弹出提示对话框
                startregisterparam1 = "?uid="+activityMsg.getUid();
                startregisterparam2 = "&accesstoken="+sharedPreferences.getString("accesstoken","00");
                startregisterparam3 = "&avid="+activityMsg.getActid();
                new Thread(new StartRegisterRunnable()).start();
            }
        });
        binding.checkActivityChangeactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckActivityActivity.this, PressActivity.class);
            }
        });
    }
    private void click2(final String string){
        binding.checkActivityPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckActivityActivity.this,ParticipateMemberActivity.class);
                intent.putExtra("memberjson",string);
                CheckActivityActivity.this.startActivity(intent);
            }
        });
    }

    private class ParticipateNumberRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(participatenumberurl+participatenumberparam1+participatenumberparam2+participatenumberparam3);
                Message message = handler.obtainMessage();
                message.what = GETNUMBER;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class StartRegisterRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(startregisterurl+startregisterparam1+startregisterparam2+startregisterparam3);
                Message message = handler.obtainMessage();
                message.what = START_REGISTER;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class DeleteActivityRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                System.out.println(deleteactivityurl+deleteactivityparam1+deleteactivityparam2+deleteactivityparam3);
                resultstring = okHttpConnect.getdata(deleteactivityurl+deleteactivityparam1+deleteactivityparam2+deleteactivityparam3);
                Message message = handler.obtainMessage();
                message.what = DELETE_ACTIVITY;
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
                case GETNUMBER:
                    String getnumberresult = (String) msg.obj;
                    if (getnumberresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getnumberresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String getnumberdata = jsonObject.getString("data");
                                if (getnumberdata.equals("null")){
                                    number = 0;
                                }else {
                                    JSONTokener memberjsonTokener = new JSONTokener(getnumberdata);
                                    JSONArray memberjsonArray = (JSONArray) memberjsonTokener.nextValue();
                                    number = memberjsonArray.length();
                                }
                                if (activityMsg.getActexpectnum()==0){
                                    binding.checkActivityPeople.setText("已报名"+number+"人/不限");
                                }else {
                                    binding.checkActivityPeople.setText("已报名"+number + "人/限" + activityMsg.getActexpectnum() + "人");
                                }
                                click2(getnumberdata);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CheckActivityActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case START_REGISTER:
                    String startregisterresult = (String) msg.obj;
                    if (startregisterresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(startregisterresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                System.out.println(jsonObject.getString("data"));
                                Intent intent = new Intent(CheckActivityActivity.this,RegisterDetailActivity.class);
                                intent.putExtra("registercode",jsonObject.getString("data"));
                                intent.putExtra("number",number);
                                intent.putExtra("actid",activityMsg.getActid());
                                CheckActivityActivity.this.startActivity(intent);
                            }else {
                                Toast.makeText(CheckActivityActivity.this,"签到请求失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CheckActivityActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case DELETE_ACTIVITY:
                    String deleteactivityresult = (String) msg.obj;
                    System.out.println("删除返回："+deleteactivityresult);
                    if (deleteactivityresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(deleteactivityresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(CheckActivityActivity.this,"成功删除活动",Toast.LENGTH_SHORT).show();
                                CheckActivityActivity.this.finish();
                            }else {
                                Toast.makeText(CheckActivityActivity.this,"删除活动失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CheckActivityActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

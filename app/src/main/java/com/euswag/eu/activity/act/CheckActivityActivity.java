package com.euswag.eu.activity.act;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.DetailsQRcodeActivity;
import com.euswag.eu.activity.funct.PressActivityActivity;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.databinding.ActivityCheckActivityBinding;
import com.euswag.eu.model.DateUtils;
import com.euswag.eu.model.NetWorkState;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CheckActivityActivity extends AppCompatActivity {

    private ActivityCheckActivityBinding binding;
    private String datajsonstring;
    private ActivityMsg activityMsg=null;

    private String activityimageloadurl = "https://euswag.com/picture/activity/";

    private OKHttpConnect okHttpConnect;
    private String participatenumberurl = "https://euswag.com/eu/activity/memberinfolist";
    private RequestBody participatenumberbody;

    private String startregisterurl = "https://euswag.com/eu/activity/startregister";
    private RequestBody startregisterbody;

    private String deleteactivityurl = "https://euswag.com/eu/activity/deleteav";
    private RequestBody deleteactivitybody;

    private final int GETNUMBER = 110;
    private final int START_REGISTER = 100;
    private final int DELETE_ACTIVITY = 101;

    private int number;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_activity);
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

            binding.setCheckActivityMsg(activityMsg);

            if (activityMsg.getActregister()==-1){
                binding.checkActivityIsregister.setText("无需签到");
//                participateparam4 = "&verifystate=2";
            }
            else {
                binding.checkActivityIsregister.setText("需要签到");
//                participateparam4 = "&verifystate=0";
                if (activityMsg.getActregister()>0){
                    binding.checkActivityChangeactivity.setText("活动已发起签到");
                }
            }

            if(activityMsg.getActprice()==0){
                binding.checkActivityIsfree.setText("免费");
            }
            else {
                binding.checkActivityIsfree.setText(String.valueOf(activityMsg.getActprice()));
            }
            binding.checkActivityActivitytime.setText(activityMsg.getActtime()+"~"+activityMsg.getActendtime());
            //需要知道已报名人数
            participatenumberbody = new FormBody.Builder()
                    .add("avid",String.valueOf(activityMsg.getActid()))
                    .add("accesstoken",sharedPreferences.getString("accesstoken", "00"))
                    .add("choice","0")
                    .build();
            if (NetWorkState.checkNetWorkState(CheckActivityActivity.this)) {
                new Thread(new ParticipateNumberRunnable()).start();
            }

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
                                deleteactivitybody = new FormBody.Builder()
                                        .add("avid",String.valueOf(activityMsg.getActid()))
                                        .add("accesstoken",sharedPreferences.getString("accesstoken","00"))
                                        .add("uid",String.valueOf(activityMsg.getUid()))
                                        .build();
                                if (NetWorkState.checkNetWorkState(CheckActivityActivity.this)) {
                                    new Thread(new DeleteActivityRunnable()).start();
                                }
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

                if (activityMsg.getActregister()>0) {
                    Intent intent = new Intent(CheckActivityActivity.this,RegisterDetailActivity.class);
                    intent.putExtra("registercode",String.valueOf(activityMsg.getActregister()));
                    intent.putExtra("number",number);
                    intent.putExtra("actid",activityMsg.getActid());
                    CheckActivityActivity.this.startActivity(intent);
                }else {
                    //判断临近时间，如果临近时间一天，则之间签到，否则弹出提示对话框
                    if (DateUtils.timediff(activityMsg.getActtime())>1){
                        new AlertDialog.Builder(CheckActivityActivity.this).setTitle("提示").setMessage("该活动开始时间距今还有"+DateUtils.timediff(activityMsg.getActtime())+"天，你确定要打开签到吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startregisterbody = new FormBody.Builder()
                                        .add("uid",String.valueOf(activityMsg.getUid()))
                                        .add("accesstoken",sharedPreferences.getString("accesstoken", "00"))
                                        .add("avid",String.valueOf(activityMsg.getActid()))
                                        .build();
                                if (NetWorkState.checkNetWorkState(CheckActivityActivity.this)) {
                                    new Thread(new StartRegisterRunnable()).start();
                                }
                            }
                        }).setNegativeButton("取消",null).show();
                    }else{
                        startregisterbody = new FormBody.Builder()
                                .add("uid",String.valueOf(activityMsg.getUid()))
                                .add("accesstoken",sharedPreferences.getString("accesstoken", "00"))
                                .add("avid",String.valueOf(activityMsg.getActid()))
                                .build();
                        if (NetWorkState.checkNetWorkState(CheckActivityActivity.this)) {
                            new Thread(new StartRegisterRunnable()).start();
                        }
                    }

                }
            }
        });
        binding.checkActivityChangeactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改活动
                if (activityMsg.getActregister()>0) {

                }else {
                    Intent intent = new Intent(CheckActivityActivity.this, PressActivityActivity.class);
                    intent.putExtra("presstype", 1);
                    intent.putExtra("datajson", datajsonstring);
                    CheckActivityActivity.this.startActivity(intent);
                }
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
                resultstring = okHttpConnect.postdata(participatenumberurl,participatenumberbody);
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
                resultstring = okHttpConnect.postdata(startregisterurl,startregisterbody);
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
                resultstring = okHttpConnect.postdata(deleteactivityurl,deleteactivitybody);
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

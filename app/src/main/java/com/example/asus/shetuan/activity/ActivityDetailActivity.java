package com.example.asus.shetuan.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.databinding.ActivityActivityDetailBinding;
import com.example.asus.shetuan.model.DateUtils;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ActivityDetailActivity extends AppCompatActivity {
    private ActivityActivityDetailBinding binding =null;
    private String datajsonstring;
    private ActivityMsg activityMsg=null;

    private OKHttpConnect okHttpConnect;
    private String getoriginatorurl = "https://euswag.com/eu/info/introinfo";
    private String getoriginatorparam1;
    private String getoriginatorparam2;

    private final int GETORIGINATOR  = 110;
    private final int PARTICIPATE = 101;
    private final int QUIT = 111;
    private final int COLLECTE = 100;
    private final int CANCELCOLLECTE = 121;

    private String headimageloadurl = "https://euswag.com/picture/user/";
    private String activityimageloadurl = "https://euswag.com/picture/activity/";

    private String participateurl = "https://euswag.com/eu/activity/participateav";
    private String participateparam1;
    private String participateparam2;
    private String participateparam3;
    private String participateparam4;

    private String quiturl = "https://euswag.com/eu/activity/quitav";
    private String quitparam1;
    private String quitparam2;
    private String quitparam3;

    private String collecteurl = "https://euswag.com/eu/activity/collectav";
    private String collecteparam1;
    private String collecteparam2;
    private String collecteparam3;

    private String cancelcollecteurl = "https://euswag.com/eu/activity/discollectav";
    private String cancelcollecteparam1;
    private String cancelcollecteparam2;
    private String cancelcollecteparam3;

    //是否已经收藏该活动，
    private boolean hascollection;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_activity_detail);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        datajsonstring = intent.getStringExtra("datajson1");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(datajsonstring);
            activityMsg = new ActivityMsg(jsonObject.getString("avTitle"),jsonObject.getString("avPlace"),DateUtils.timet(jsonObject.getString("avStarttime")),activityimageloadurl+jsonObject.getString("avLogo")+".jpg");
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.setActivityDetailMsg(activityMsg);

        //请求发起活动者信息
        getoriginatorparam1 = "?uid="+activityMsg.getUid();
        getoriginatorparam2 = "&accesstoken="+sharedPreferences.getString("accesstoken","00");
        System.out.println("getoriginatorparam1"+getoriginatorparam1);
        System.out.println("getoriginatorparam2"+getoriginatorparam2);
        new Thread(new GetOriginatorRunnable()).start();

        if (activityMsg.getActregister()==-1){
            binding.activityDetailIsregister.setText("无需签到");
            participateparam4 = "&verifystate=2";
        }
        else {
            binding.activityDetailIsregister.setText("需要签到");
            participateparam4 = "&verifystate=0";
        }

        if(activityMsg.getActprice()==0){
            binding.activityDetailIsfree.setText("免费");
        }
        else {
            binding.activityDetailIsfree.setText(String.valueOf(activityMsg.getActprice()));
        }
        binding.activityDetailActivitytime.setText(activityMsg.getActtime()+"~"+activityMsg.getActendtime());
        //需要知道已报名人数
        binding.activityDetailPeople.setText("已报名"+"人/限"+activityMsg.getActexpectnum()+"人");
        //设置参加按钮
        //通过验证来确定
        //请求是否已经参加该活动
        if (intent.getStringExtra("isparticipate").equals("0")) {
            binding.activityDetailIsenroll.setText("我要参加");
        }else {
            binding.activityDetailIsenroll.setText("退出活动");
        }
        binding.activityDetailBackground.setImageURI(activityMsg.getImageurl());
        click();
    }

    private void click(){
        binding.activityDetailCallphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.activityDetailOriginator==null||binding.activityDetailOriginator.length()==0){
                    Toast.makeText(ActivityDetailActivity.this,"数据未加载成功，你不能进行此操作",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + activityMsg.getUid()));
                    if (ActivityCompat.checkSelfPermission(ActivityDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    ActivityDetailActivity.this.startActivity(intent);
                }
            }
        });
        //参加活动
        if (binding.activityDetailIsenroll.getText().equals("我要参加")) {
            binding.activityDetailIsenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    participateparam1 = "?uid=" + sharedPreferences.getString("phonenumber", "0");
                    participateparam2 = "&accesstoken=" + sharedPreferences.getString("accesstoken", "00");
                    participateparam3 = "&avid=" + activityMsg.getActid();
                    new Thread(new ParticipateRunnable()).start();
                }
            });
        }else {
            binding.activityDetailIsenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitparam1 = "?uid=" + sharedPreferences.getString("phonenumber", "0");
                    quitparam2 = "&accesstoken=" + sharedPreferences.getString("accesstoken", "00");
                    quitparam3 = "&avid=" + activityMsg.getActid();
                    new Thread(new QuitRunnable()).start();
                }
            });
        }
        binding.activityDetailCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hascollection){
                    cancelcollecteparam1 = "?uid=" + sharedPreferences.getString("phonenumber", "0");
                    cancelcollecteparam2 = "&accesstoken=" + sharedPreferences.getString("accesstoken", "00");
                    cancelcollecteparam3 = "&avid="+activityMsg.getActid();
                    new Thread(new CancelCollecteRunnable()).start();
                }else {
                    collecteparam1 = "?uid=" + sharedPreferences.getString("phonenumber", "0");
                    collecteparam2 = "&accesstoken=" + sharedPreferences.getString("accesstoken", "00");
                    collecteparam3 = "&avid="+activityMsg.getActid();
                    new Thread(new CollecteRunnable()).start();
                }
            }
        });
    }

    private class GetOriginatorRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect =new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(getoriginatorurl+getoriginatorparam1+getoriginatorparam2);
                Message message = handler.obtainMessage();
                message.what = GETORIGINATOR;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ParticipateRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(participateurl+participateparam1+participateparam2+participateparam3+participateparam4);
                Message message = handler.obtainMessage();
                message.what = PARTICIPATE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class QuitRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(quiturl+quitparam1+quitparam2+quitparam3);
                Message message = handler.obtainMessage();
                message.what = QUIT;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class CollecteRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(collecteurl+collecteparam1+collecteparam2+collecteparam3);
                Message message = handler.obtainMessage();
                message.what = COLLECTE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class CancelCollecteRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(cancelcollecteurl+cancelcollecteparam1+cancelcollecteparam2+cancelcollecteparam3);
                Message message = handler.obtainMessage();
                message.what = CANCELCOLLECTE;
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
                case GETORIGINATOR:
                    String getoringinatorresult = (String) msg.obj;
                    if (getoringinatorresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getoringinatorresult);
                            result = jsonObject.getInt("status");
                            System.out.println("result:"+result);
                            if (result == 200){
                                String oringingationdata;
                                oringingationdata = jsonObject.getString("data");
                                JSONObject jsonObject1 = new JSONObject(oringingationdata);
                                binding.activityDetailOriginator.setText(jsonObject1.getString("name"));
                                binding.activityDetailReputation.setText(jsonObject1.getString("reputation"));
                                binding.activityDetailHeadimage.setImageURI(headimageloadurl+jsonObject1.getString("avatar")+".jpg");
                            }
                            else {
                                Toast.makeText(ActivityDetailActivity.this,"活动发起人信息加载失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ActivityDetailActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PARTICIPATE:
                    String participateresult = (String) msg.obj;
                    if (participateresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(participateresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(ActivityDetailActivity.this,"参加成功",Toast.LENGTH_SHORT).show();
                            }else if (result == 500){
                                Toast.makeText(ActivityDetailActivity.this,"已参加了该活动，不能重复参加",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(ActivityDetailActivity.this,"活动未参加成功",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ActivityDetailActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case QUIT:
                    String quitresult = (String) msg.obj;
                    if (quitresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(quitresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(ActivityDetailActivity.this,"你已经退出该活动",Toast.LENGTH_SHORT).show();
                                ActivityDetailActivity.this.finish();

                            }else {
                                Toast.makeText(ActivityDetailActivity.this,"退出失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ActivityDetailActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case COLLECTE:
                    String collecteresult = (String) msg.obj;
                    if (collecteresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(collecteresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(ActivityDetailActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                                binding.activityDetailCollection.setImageResource(R.drawable.ic_collection_after);
                                hascollection = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ActivityDetailActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CANCELCOLLECTE:
                    String cancelcollecteresult = (String) msg.obj;
                    if (cancelcollecteresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(cancelcollecteresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                Toast.makeText(ActivityDetailActivity.this,"已取消收藏",Toast.LENGTH_SHORT).show();
                                binding.activityDetailCollection.setImageResource(R.drawable.ic_collection_before);
                            }else {
                                Toast.makeText(ActivityDetailActivity.this,"取消失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ActivityDetailActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

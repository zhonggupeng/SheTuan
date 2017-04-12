package com.example.asus.shetuan.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ActivityDetail;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.databinding.ActivityActivityDetailBinding;
import com.example.asus.shetuan.model.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ActivityDetailActivity extends AppCompatActivity {
    private ActivityActivityDetailBinding binding =null;
    private String datajsonstring;

    private String imageloadurl = "https://euswag.com/picture/activity/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_activity_detail);
        Intent intent = getIntent();
        datajsonstring = intent.getStringExtra("datajson1");
        JSONObject jsonObject=null;
        ActivityMsg activityMsg=null;
        try {
            jsonObject = new JSONObject(datajsonstring);
            activityMsg = new ActivityMsg(jsonObject.getString("avTitle"),jsonObject.getString("avPlace"),DateUtils.timet(jsonObject.getString("avStarttime")),imageloadurl+jsonObject.getString("avLogo")+".jpg");
            activityMsg.setActendtime(DateUtils.timet(jsonObject.getString("avEndtime")));
            activityMsg.setUid(jsonObject.getInt("uid"));
            activityMsg.setActexpectnum(jsonObject.getInt("avExpectnum"));
            activityMsg.setActprice(jsonObject.getDouble("avPrice"));
            activityMsg.setActdetail(jsonObject.getString("avDetail"));
            activityMsg.setActstate(jsonObject.getInt("avState"));
            activityMsg.setActregister(jsonObject.getInt("avRegister"));
            activityMsg.setActid(jsonObject.getInt("avid"));
            //报名截止时间
            activityMsg.setActenrolldeadline(jsonObject.getString("avEnrolldeadline"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.setActivityDetailMsg(activityMsg);
        ActivityDetail activityDetail = new ActivityDetail(this);
        binding.setActivityDetail(activityDetail);
        if (activityMsg.getActregister()==-1){
            binding.activityDetailIsregister.setText("无需签到");
        }
        else {
            binding.activityDetailIsregister.setText("需要签到");
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
        binding.activityDetailIsenroll.setText("我要参加");
        binding.activityDetailBackground.setImageURI(activityMsg.getImageurl());
    }
}

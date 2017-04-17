package com.example.asus.shetuan.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.databinding.ActivityCheckActivityBinding;
import com.example.asus.shetuan.model.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class CheckActivityActivity extends AppCompatActivity {

    private ActivityCheckActivityBinding binding;
    private String datajsonstring;
    private ActivityMsg activityMsg=null;

    private String activityimageloadurl = "https://euswag.com/picture/activity/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_check_activity);
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
            if (activityMsg.getActexpectnum()==0){
                binding.checkActivityPeople.setText("已报名"+"人/不限");
            }else {
                binding.checkActivityPeople.setText("已报名" + "人/限" + activityMsg.getActexpectnum() + "人");
            }
            binding.checkActivityBackground.setImageURI(activityMsg.getImageurl());

            click();
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
    }
}

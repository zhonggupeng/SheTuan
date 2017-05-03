package com.euswag.eu.activity.person;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.databinding.ActivityAccountPasswordReviseBinding;
import com.euswag.eu.model.NetWorkState;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class AccountPasswordReviseActivity extends AppCompatActivity {
    private ActivityAccountPasswordReviseBinding binding;
    private SharedPreferences sharedPreferences;

    private OKHttpConnect okHttpConnect;
    private String passwordreviseurl="https://euswag.com/eu/user/changepwd";
    private RequestBody passwordrevisebody;

    private final int PASSWORD_REVISE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_password_revise);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        click();
    }
    private void click(){
        binding.accountPasswordReviseBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountPasswordReviseActivity.this.onBackPressed();
            }
        });
        binding.accountPasswordReviseFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.accountPasswordReviseOldpassword.getText()==null||binding.accountPasswordReviseOldpassword.getText().length()==0){
                    Toast.makeText(AccountPasswordReviseActivity.this,"请填写旧密码",Toast.LENGTH_SHORT).show();
                }else if (binding.accountPasswordReviseNewpassword.getText()==null||binding.accountPasswordReviseNewpassword.getText().length()==0){
                    Toast.makeText(AccountPasswordReviseActivity.this,"请填写新密码",Toast.LENGTH_SHORT).show();
                }else if (binding.accountPasswordReviseNewpasswordConfirm.getText()==null||binding.accountPasswordReviseNewpasswordConfirm.getText().length()==0){
                    Toast.makeText(AccountPasswordReviseActivity.this,"请确认新密码",Toast.LENGTH_SHORT).show();
                }else if (!(binding.accountPasswordReviseNewpassword.getText().equals(binding.accountPasswordReviseNewpasswordConfirm.getText()))){
                    Toast.makeText(AccountPasswordReviseActivity.this,"新密码不一致",Toast.LENGTH_SHORT).show();
                }else {
                    passwordrevisebody = new FormBody.Builder()
                            .add("uid",sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken",sharedPreferences.getString("accesstoken","00"))
                            .add("oldpwd",binding.accountPasswordReviseOldpassword.getText().toString())
                            .add("newpwd",binding.accountPasswordReviseNewpassword.getText().toString())
                            .build();
                    if (NetWorkState.checkNetWorkState(AccountPasswordReviseActivity.this)) {
                        new Thread(new PasswordReviseRunnable()).start();
                    }
                }
            }
        });
    }

    private class PasswordReviseRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(passwordreviseurl,passwordrevisebody);
                Message message = handler.obtainMessage();
                message.what = PASSWORD_REVISE;
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
                case PASSWORD_REVISE:
                    String passwordreviseresult = (String) msg.obj;
                    if (passwordreviseresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(passwordreviseresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("accesstoken", jsonObject.getString("data"));
                                editor.commit();
                                AccountPasswordReviseActivity.this.finish();
                            }else if (result == 401){
                                Toast.makeText(AccountPasswordReviseActivity.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(AccountPasswordReviseActivity.this,"修改密码失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(AccountPasswordReviseActivity.this,"",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

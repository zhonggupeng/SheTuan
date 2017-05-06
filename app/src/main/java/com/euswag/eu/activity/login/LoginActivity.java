package com.euswag.eu.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.MainTabActivity;
import com.euswag.eu.bean.Login;
import com.euswag.eu.databinding.ActivityLoginBinding;
import com.euswag.eu.model.NetWorkState;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    private String sendvercodeurl = "/user/verifyphone";
    private RequestBody sendvercodebody;

    private String loginurl = "/user/login";
    private RequestBody loginbody;

    private OKHttpConnect okHttpConnect;

    private ActivityLoginBinding binding;
    private Login login;
    private String phonenumber;

    private final int SENDVERCODE = 11;
    private final int LOGIN = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        login = new Login(LoginActivity.this);
        binding.setLogin(login);
        Intent intent = getIntent();
        login.setAccountnumber(intent.getStringExtra("phonenumber"));

        click();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void click(){
        binding.loginLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginbody = new FormBody.Builder()
                        .add("uid",login.getAccountnumber())
                        .add("password",login.getPassword())
                        .build();
                if (NetWorkState.checkNetWorkState(LoginActivity.this)) {
                    new Thread(new LoginRunnable()).start();
                }
            }
        });

        binding.loginForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.getAccountnumber()==null||login.getAccountnumber().length()!=11||login.getAccountnumber().charAt(0)!='1'){
                    Intent intent1 = new Intent(LoginActivity.this,InputPhoneActivity.class);
                    LoginActivity.this.startActivity(intent1);
                }
                else {
                    phonenumber = login.getAccountnumber();
                    if (NetWorkState.checkNetWorkState(LoginActivity.this)) {
                        sendvercodebody = new FormBody.Builder()
                                .add("choice","1")
                                .add("phone",phonenumber)
                                .build();
                        new Thread(new SendVercodeRunnable()).start();
                    }
                }
            }
        });
    }

    private class LoginRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(loginurl,loginbody);
                Message message = handler.obtainMessage();
                message.what = LOGIN;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendVercodeRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(sendvercodeurl,sendvercodebody);
                Message message = handler.obtainMessage();
                message.what = SENDVERCODE;
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
                case SENDVERCODE:
                    String resultstring = (String) msg.obj;
                    if (resultstring.length()!=0) {
                        JSONObject jsonObject;
                        int result;
                        String returnvercode;
                        try {
                            System.out.println(1111);
                            jsonObject = new JSONObject(resultstring);
                            result = jsonObject.getInt("status");
                            System.out.println("result"+result);
                            if (result == 200){
                                returnvercode = jsonObject.getString("data");
                                Intent intent = new Intent(LoginActivity.this,InputPINActivity.class);
                                intent.putExtra("phonenumber",login.getAccountnumber());
                                intent.putExtra("isregister","1");
                                intent.putExtra("returnvercode",returnvercode);
                                LoginActivity.this.startActivity(intent);
                            }
                            else if (result == 400){
                                Toast.makeText(LoginActivity.this,"该手机号未被注册",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,InputPhoneActivity.class);
                                intent.putExtra("phonenumber",login.getAccountnumber());
                                intent.putExtra("isregister","0");
                                LoginActivity.this.startActivity(intent);
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case LOGIN:
                    String loginresultstring = (String) msg.obj;
                    if (loginresultstring.length()!=0){
                        JSONObject jsonObject;
                        int resutlt;
                        try {
                            jsonObject = new JSONObject(loginresultstring);
                            resutlt = jsonObject.getInt("status");
                            if (resutlt == 200){
                                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("accesstoken", jsonObject.getString("data"));
                                editor.putString("phonenumber",login.getAccountnumber());
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, MainTabActivity.class);
                                LoginActivity.this.startActivity(intent);
                            }
                            else if(resutlt == 401){
                                Toast.makeText(LoginActivity.this,jsonObject.getString("data"),Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(LoginActivity.this,"出现异常",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

package com.example.asus.shetuan.activity.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.MainTabActivity;
import com.example.asus.shetuan.bean.Login;
import com.example.asus.shetuan.databinding.ActivityLoginBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class LoginActivity extends AppCompatActivity {

    private String sendvercodeurl = "https://euswag.com/eu/user/verifyphone";
    private String sendvercodeparam1 = "?choice=1";
    private String sendvercodeparam2 = "&phone=";

    private String loginurl = "https://euswag.com/eu/user/login";
    private String loginparam1 = "?uid=";
    private String loginparam2 = "&password=";

    private OKHttpConnect okHttpConnect;

    private ActivityLoginBinding binding;
    private Login login;
    private String phonenumber;

    private final int SENDVERCODE = 11;
    private final int LOGIN = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewServer.get(this).addWindow(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
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
                loginparam1 = loginparam1+login.getAccountnumber();
                loginparam2 = loginparam2+login.getPassword();
                new Thread(new LoginRunnable()).start();
            }
        });

        binding.loginForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (login.getAccountnumber()==null||login.getAccountnumber().length()!=11||login.getAccountnumber().charAt(0)!='1'){
                    Intent intent1 = new Intent(LoginActivity.this,InputPhoneActivity.class);
                    intent1.putExtra("isregister","1");
                    LoginActivity.this.startActivity(intent1);
                }
                else {
                    phonenumber = login.getAccountnumber();
                    new Thread(new SendVercodeRunnable()).start();
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
                resultstring = okHttpConnect.getdata(loginurl+loginparam1+loginparam2);
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
                resultstring = okHttpConnect.getdata(sendvercodeurl+sendvercodeparam1+sendvercodeparam2+phonenumber);
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
                    System.out.println("resultstring"+resultstring);
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
                    System.out.println("登录返回："+loginresultstring);
                    if (loginresultstring.length()!=0){
                        JSONObject jsonObject;
                        int resutlt;
                        try {
                            jsonObject = new JSONObject(loginresultstring);
                            resutlt = jsonObject.getInt("status");
                            if (resutlt == 200){
                                SharedPreferences sharedPreferences = getSharedPreferences("tocken", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("accesstocken", jsonObject.getString("data"));
                                editor.putString("phonenumber",login.getAccountnumber());
                                editor.commit();
                                System.out.println("sharedPreferences  accesstocken:  " + sharedPreferences.getString("accesstocken", "00"));
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

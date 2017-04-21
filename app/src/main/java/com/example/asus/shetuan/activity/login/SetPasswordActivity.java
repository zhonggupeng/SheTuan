package com.example.asus.shetuan.activity.login;

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

import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.MainTabActivity;
import com.example.asus.shetuan.bean.SetPassword;
import com.example.asus.shetuan.databinding.ActivitySetPasswordBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SetPasswordActivity extends AppCompatActivity {

    private OKHttpConnect okHttpConnect;
    private String sendurl = "https://euswag.com/eu/user/newuser";
    private String sendparam;

    //头像上传网址
    private String sendavatarurl = "https://euswag.com/eu/upload/user";

    private String changepasswordurl = "https://euswag.com/eu/user/changepwdbyphone";
    private String changepasswordparam1;
    private String changepasswordparam2;

    private ActivitySetPasswordBinding binding;
    private SetPassword setPassword;
    private Intent dataintent;
    private File file;

    private final int SENDAVATARIMAGE = 1;
    private final int CHANGEPASSWORD = 2;
    private final int SENDUSERINFO = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_password);
        setPassword = new SetPassword(this);
        binding.setSetPassword(setPassword);
        dataintent = getIntent();

        click();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void click() {
        binding.setPasswordFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (setPassword.getSetpassword() == null || setPassword.getSetpassword().length() == 0 || setPassword.getConfirmpassword() == null || setPassword.getConfirmpassword().length() == 0) {
                    Toast.makeText(SetPasswordActivity.this, "密码为空，请设置密码", Toast.LENGTH_SHORT).show();
                } else if (!setPassword.getSetpassword().equals(setPassword.getConfirmpassword())) {
                    Toast.makeText(SetPasswordActivity.this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    if (dataintent.getStringExtra("isregister").equals("0")) {
                        //先进行图片的发送，即头像的发送
                        file = new File(dataintent.getStringExtra("headimagepath"));
                        new Thread(new SendAvatarRunnable()).start();
                        //然后进行数据的发送
                    } else if (dataintent.getStringExtra("isregister").equals("1")) {
                        //仅仅修改密码，返回新的tocken
                        changepasswordparam1 = "?uid=" + dataintent.getStringExtra("phonenumber");
                        System.out.println("phonenumber:    " + changepasswordparam1);
                        changepasswordparam2 = "&newpwd=" + setPassword.getConfirmpassword();
                        System.out.println("new password:    " + changepasswordparam2);
                        new Thread(new ChangePasswordRunnable()).start();
                    }
                }
            }
        });
    }

    private class SendUserinfoRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(sendurl+sendparam);
                Message message = handler.obtainMessage();
                message.what = SENDUSERINFO;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SendAvatarRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String sendavatarresult;
            try {
                sendavatarresult = okHttpConnect.postfile(sendavatarurl, file);
                Message message = handler.obtainMessage();
                message.what = SENDAVATARIMAGE;
                message.obj = sendavatarresult;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangePasswordRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String changepasswordresult;
            try {
                changepasswordresult = okHttpConnect.getdata(changepasswordurl + changepasswordparam1 + changepasswordparam2);
                Message message = handler.obtainMessage();
                message.what = CHANGEPASSWORD;
                message.obj = changepasswordresult;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SENDAVATARIMAGE:
                    String sendavatarresult = (String) msg.obj;
                    if (sendavatarresult.length() != 0) {
                        System.out.println("sendavatarresult:" + sendavatarresult);
                        JSONObject jsonObject;
                        int result;
                        String returnstring;
                        try {
                            jsonObject = new JSONObject(sendavatarresult);
                            result = jsonObject.getInt("status");
//                            returnstring = jsonObject.getString("data");
                            if (result == 200) {
                                returnstring = jsonObject.getString("data");
                                int gender;
                                if (dataintent.getStringExtra("sex").equals("男")){
                                    gender = 0;
                                }
                                else if (dataintent.getStringExtra("sex").equals("女")){
                                    gender = 1;
                                }else {
                                    gender = 2;
                                }
                                sendparam = "?uid=" + dataintent.getStringExtra("phonenumber") + "&avatar=" + returnstring.substring(0,returnstring.indexOf("."))
                                        + "&nickname=" + dataintent.getStringExtra("nickname") + "&gender=" + gender
                                        + "&professionclass=" + dataintent.getStringExtra("academe") + "&studentid=" + dataintent.getStringExtra("studentid")
                                        + "&name=" + dataintent.getStringExtra("name") + "&userdescription=" + dataintent.getStringExtra("personedit")
                                        + "&password=" + setPassword.getConfirmpassword()+"&reputation="+100+"&verified="+0;
                                System.out.println("发送数据"+sendparam);
                                new Thread(new SendUserinfoRunnable()).start();
                            }else {
                                Toast.makeText(SetPasswordActivity.this,"上传头像失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(SetPasswordActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CHANGEPASSWORD:
                    String changepasswordresult = (String) msg.obj;
                    if (changepasswordresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(changepasswordresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                //返回的data为新的tocken，将其存入本地
//                                jsonObject.getString("data");
                                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("accesstoken", jsonObject.getString("data"));
                                editor.putString("phonenumber",dataintent.getStringExtra("phonenumber"));
                                editor.commit();
                                System.out.println("sharedPreferences  accesstoken:  " + sharedPreferences.getString("accesstoken", "00"));
                                Intent intent = new Intent(SetPasswordActivity.this, MainTabActivity.class);
                                SetPasswordActivity.this.startActivity(intent);
                            } else if (result == 500) {
                                Toast.makeText(SetPasswordActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SetPasswordActivity.this, "出现异常，请点击重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SetPasswordActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SENDUSERINFO:
                    String senduserinforesult = (String) msg.obj;
                    System.out.println("senduserinforesult"+senduserinforesult);
                    if (senduserinforesult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(senduserinforesult);
                            result = jsonObject.getInt("status");
                            System.out.println("result"+result);
                            if (result == 200) {
                                SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("accesstoken", jsonObject.getString("data"));
                                editor.putString("phonenumber",dataintent.getStringExtra("phonenumber"));
                                editor.commit();
                                System.out.println("sharedPreferences  accesstoken:  " + sharedPreferences.getString("accesstoken", "00"));
                                Intent intent = new Intent(SetPasswordActivity.this, MainTabActivity.class);
                                SetPasswordActivity.this.startActivity(intent);
                            } else if (result == 500) {
                                Toast.makeText(SetPasswordActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SetPasswordActivity.this, "出现异常，请点击重试", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SetPasswordActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;

            }
        }
    };
}

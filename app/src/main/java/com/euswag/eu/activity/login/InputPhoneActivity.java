package com.euswag.eu.activity.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.bean.Phone;
import com.euswag.eu.databinding.ActivityInputPhoneBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

//第一次启动APP进入这个界面，或者退出账号后重新登陆时进入这个界面

public class InputPhoneActivity extends AppCompatActivity {

    private OKHttpConnect okHttpConnect;
    private String stringurl = "/user/verifyphone";
    private RequestBody body;
    private String phonenumber;

    private ActivityInputPhoneBinding binding;
    private Phone phone;

    private final int SENDVERCODE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        //新设置的图片影响了背景
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input_phone);
        phone = new Phone(this);
        binding.setPhone(phone);
        //关闭已有账号登录口
//        Intent intent = getIntent();
//        if (intent.getStringExtra("isregister")!=null) {
//            phone.setIsregister(intent.getStringExtra("isregister"));
//        }
//        else phone.setIsregister("0");
//        if (phone.getIsregister().equals("0")){
//            binding.inputPhoneTologin.setText("【已有账号】");
//            binding.inputPhoneTologin.setClickable(true);
//        }
//        else if (phone.getIsregister().equals("1")){
//            binding.inputPhoneTologin.setText("");
//            binding.inputPhoneTologin.setClickable(false);
//        }
        click();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void click() {
        binding.inputPhoneSendvercode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.inputPhoneSendvercode.setClickable(false);
                phonenumber = phone.getPhonenumber();
//                    requesturl = stringurl+param1+0+param2+phonenumber;
                body = new FormBody.Builder()
                        .add("choice", "0")
                        .add("phone", phonenumber)
                        .build();
                new Thread(new SendVercodeRunnable()).start();
            }
        });
    }

    private class SendVercodeRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(stringurl, body);
                Message message = handler.obtainMessage();
                message.what = SENDVERCODE;
                message.obj = resultstring;
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
                case SENDVERCODE:
                    String resultstring = (String) msg.obj;
                    System.out.println("发送验证码的返回信息：" + resultstring);
                    if (resultstring.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        String returnvercode;
                        try {
                            jsonObject = new JSONObject(resultstring);
                            result = jsonObject.getInt("status");
                            System.out.println("result" + result);
                            if (result == 200) {
                                returnvercode = jsonObject.getString("data");
                                System.out.println("returnvercode" + returnvercode);
                                Intent intent = new Intent(InputPhoneActivity.this, InputPINActivity.class);
                                intent.putExtra("isregister", "0");
                                intent.putExtra("phonenumber", phonenumber);
                                intent.putExtra("returnvercode", returnvercode);
                                InputPhoneActivity.this.startActivity(intent);
                            } else if (result == 400) {
                                Toast.makeText(InputPhoneActivity.this, "该手机号已被注册", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(InputPhoneActivity.this, LoginActivity.class);
                                intent.putExtra("phonenumber", phonenumber);
                                intent.putExtra("isregister", "1");
                                InputPhoneActivity.this.startActivity(intent);
                            } else {
                                Toast.makeText(InputPhoneActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(InputPhoneActivity.this, "网络出错", Toast.LENGTH_SHORT).show();
                    }
                    binding.inputPhoneSendvercode.setClickable(true);
                    break;
                default:
                    break;
            }
        }
    };
}

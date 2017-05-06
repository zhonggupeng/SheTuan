package com.euswag.eu.activity.act;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.bean.InputPINTest;
import com.euswag.eu.databinding.ActivityInputRegisterCodeBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class InputRegisterCodeActivity extends AppCompatActivity {
    private ActivityInputRegisterCodeBinding binding;
    private int registercode;
    private int actid;
    private InputPINTest inputPINTest;

    private OKHttpConnect okHttpConnect;
    private String registerfinishurl = "/activity/participateregister";
    private RequestBody registerfinishbody;

    private final int REGISTER_FINISH = 110;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_input_register_code);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        inputPINTest = new InputPINTest(this);
        binding.setInputPINTest(inputPINTest);
        Intent intent = getIntent();
        registercode = intent.getIntExtra("register", 0);
        actid = intent.getIntExtra("actid", 0);
        click();
    }

    private void click() {
        binding.inputRegisterCodeConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (String.valueOf(registercode).equals(inputPINTest.getEdit1() + inputPINTest.getEdit2() + inputPINTest.getEdit3() + inputPINTest.getEdit4())) {
                    registerfinishbody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("avid", String.valueOf(actid))
                            .build();
                    new Thread(new RegisterFinishRunnable()).start();
                } else {
                    Toast.makeText(InputRegisterCodeActivity.this, "签到码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class RegisterFinishRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(registerfinishurl, registerfinishbody);
                Message message = handler.obtainMessage();
                message.what = REGISTER_FINISH;
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
                case REGISTER_FINISH:
                    String registerfinishresult = (String) msg.obj;
                    if (registerfinishresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(registerfinishresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(InputRegisterCodeActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                                InputRegisterCodeActivity.this.finish();
                            } else {
                                Toast.makeText(InputRegisterCodeActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(InputRegisterCodeActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}

package com.example.asus.shetuan.activity.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.InputPINTest;
import com.example.asus.shetuan.databinding.ActivityInputPinBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class InputPINActivity extends AppCompatActivity {

    private String stringurl = "https://euswag.com/eu/user/verifyphone";
    private String param1 = "&choice=0";
    private String param2 = "&phone=";
    private OKHttpConnect okHttpConnect;

    private String phonenumber;
    private String countdowntext;
    private TimeCount timeCount;
    private InputPINTest inputPINTest;
    private ActivityInputPinBinding binding;

    // 0 表示用户想进行注册，1 表示用户忘记密码转入此页面
    private String isregister ;
    private String returnvercode;

    private final int RESENDVERCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewServer.get(this).addWindow(this);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_input_pin);
        inputPINTest = new InputPINTest(this);
        binding.setInputPINTest(inputPINTest);
        //传入类一样传入方法

        Intent intent = getIntent();
        phonenumber = intent.getStringExtra("phonenumber");
        isregister = intent.getStringExtra("isregister");
        returnvercode = intent.getStringExtra("returnvercode");
        timeCount = new TimeCount(60000,1000);
        //基本上内容是实现了，但是时间在这里没有设置停止，阻塞了主线程
        timeCount.start();
        click();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void click(){
        binding.inputPinShowtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new ResendVercodeRunnable()).start();
            }
        });
        binding.inputPinNextstep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果超时发送，不知道要不要限制一下
                if (returnvercode.equals(inputPINTest.getEdit1()+inputPINTest.getEdit2()+inputPINTest.getEdit3()+inputPINTest.getEdit4())){
                    if (isregister.equals("0")){
                        Intent intent = new Intent(InputPINActivity.this,FillInformationActivity.class);
                        //能够到达下一个activity说明 isregister.equals("0")，不必传
                        intent.putExtra("phonenumber",phonenumber);
                        InputPINActivity.this.startActivity(intent);
                    }
                    else if (isregister.equals("1")){
                        Intent intent = new Intent(InputPINActivity.this,SetPasswordActivity.class);
                        intent.putExtra("isregister",isregister);
                        intent.putExtra("phonenumber",phonenumber);
                        InputPINActivity.this.startActivity(intent);
                    }
                }
                else {
                    Toast.makeText(InputPINActivity.this,"验证码输入错误",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DEL){
            if (!(binding.inputPinNextstep.isClickable())) {
                if (binding.inputPinEdit4.length() == 0) {
                    if (binding.inputPinEdit3.length() == 0) {
                        if (binding.inputPinEdit2.length() == 0) {
                            binding.inputPinEdit1.setText("");
                            binding.inputPinEdit1.setFocusable(true);
                            binding.inputPinEdit1.setFocusableInTouchMode(true);
                        } else {
                            binding.inputPinEdit2.setText("");
                            binding.inputPinEdit2.setFocusable(true);
                            binding.inputPinEdit2.setFocusableInTouchMode(true);
                        }
                    } else {
                        binding.inputPinEdit3.setText("");
                        binding.inputPinEdit3.setFocusable(true);
                        binding.inputPinEdit3.setFocusableInTouchMode(true);
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public class TimeCount extends CountDownTimer{

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            binding.inputPinShowtime.setClickable(false);
            countdowntext = new String(phonenumber+"   "+l/1000+"秒后重发");
            binding.inputPinShowtime.setText(countdowntext);
        }

        @Override
        public void onFinish() {
            //倒计时结束之后，重新请求验证码操作
            countdowntext = new String(phonenumber+"  "+"点击重新发送");
            binding.inputPinShowtime.setText(countdowntext);
            binding.inputPinShowtime.setClickable(true);
        }
    }

    private class ResendVercodeRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(stringurl+param1+param2+phonenumber);
                Message message = handler.obtainMessage();
                message.what = RESENDVERCODE;
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
                case RESENDVERCODE:
                    String resultstring = (String) msg.obj;
                    System.out.println("result: "+resultstring);
                    if (resultstring.length()==0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(resultstring);
                            result = jsonObject.getInt("status");
                            returnvercode = jsonObject.getString("data");
                            System.out.println("returnvercode" + returnvercode);
                            if (result == 200) {
                                timeCount = new TimeCount(60000,1000);
                                timeCount.start();
                            } else {
                                Toast.makeText(InputPINActivity.this, "出现异常，请点击重试！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(InputPINActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
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

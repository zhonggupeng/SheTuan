package com.euswag.eu.activity.shetuan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.DetailsQRcodeActivity;
import com.euswag.eu.bean.ShetuanInformation;
import com.euswag.eu.bean.ShetuanMsg;
import com.euswag.eu.databinding.ActivityShetuanInformationBinding;
import com.euswag.eu.model.OKHttpConnect;
import com.euswag.eu.weight.EditTextWithDel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ShetuanInformationActivity extends AppCompatActivity {
    private String datajsonstring;
    private ActivityShetuanInformationBinding binding = null;
    private ShetuanMsg shetuanMsg = null;
    private SharedPreferences sharedPreferences;

    private OKHttpConnect okHttpConnect;
    private String shetuanmemberurl = "/community/memberlist";
    private RequestBody shetuanmemberbody;

    private String collecteurl = "/community/collectcm";
    private RequestBody collectebody;

    private String cancelcollecteurl = "/community/discollectcm";
    private RequestBody cancelcollectebody;

    private String participateurl = "/community/participatecm";
    private RequestBody participatebody;

    private String getheaderurl = "/info/introinfo";
    private RequestBody getheaderbody;

    private String shetuanlogourl = "https://eu-1251935523.file.myqcloud.com/community/logo/cmlogo";
    private String shetuanbackgroundurl = "https://eu-1251935523.file.myqcloud.com/community/background/cmbg";

    private final int SHETUAN_MEMBER = 110;
    private final int COLLECTE = 100;
    private final int CANCEL_COLLECTE = 101;
    private final int PARTICIPATE = 1100;
    private final int GET_HEADER = 1111;

    //是否收藏社团
    private boolean hascollection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shetuan_information);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        hascollection = false;
        Intent intent = getIntent();
        if (intent.getStringExtra("collection").equals("0")) {
            binding.shetuanInformationFunctbutton.setText("申请加入");
        } else {
            binding.shetuanInformationFunctbutton.setText("取消收藏");
        }
        datajsonstring = intent.getStringExtra("datajson3");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(datajsonstring);
            shetuanMsg = new ShetuanMsg(jsonObject.getString("cmName"), jsonObject.getString("cmDetail"), shetuanbackgroundurl + jsonObject.getString("cmBackground") + ".jpg", shetuanlogourl + jsonObject.getString("cmLogo")+".png");
            shetuanMsg.setShetuanid(jsonObject.getInt("cmid"));
            shetuanMsg.setShetuantype(jsonObject.getInt("cmType"));
            shetuanMsg.setShetuanattr(jsonObject.getInt("cmAttr"));
            shetuanMsg.setShetuanrecruit(jsonObject.getInt("cmRecruit"));
            shetuanMsg.setShetuanheat(jsonObject.getInt("cmHeat"));
            shetuanMsg.setShtuanannouncement(jsonObject.getString("cmAnnouncement"));
            shetuanMsg.setShetuanschool(jsonObject.getString("cmSchool"));
            System.out.println(jsonObject.getLong("cmBoss"));
            shetuanMsg.setShetuanboss(jsonObject.getLong("cmBoss"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.setShetuanInformationData(shetuanMsg);
        binding.setShetuanInformation(new ShetuanInformation(this));
        //社长信息请求
        getheaderbody = new FormBody.Builder()
                .add("uid", String.valueOf(shetuanMsg.getShetuanboss()))
                .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                .build();
        new Thread(new GetHeaderRunnable()).start();
        //社团成员请求
        shetuanmemberbody = new FormBody.Builder()
                .add("cmid", String.valueOf(shetuanMsg.getShetuanid()))
                .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                .build();
        new Thread(new ShetuanMemberRunnable()).start();

        if (shetuanMsg.getShetuantype() == 0) {
            binding.shetuanInformationShetuantype.setText("兴趣");
        } else if (shetuanMsg.getShetuantype() == 1) {
            binding.shetuanInformationShetuantype.setText("学术");
        } else if (shetuanMsg.getShetuantype() == 2) {
            binding.shetuanInformationShetuantype.setText("运动");
        }
        binding.shetuanInformationBackground.setImageURI(shetuanMsg.getBackgroundimage());
        binding.shetuanInformationLogo.setImageURI(shetuanMsg.getLogoimage());
        click();
    }

    private void click() {
        binding.shetuanInformationQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShetuanInformationActivity.this, DetailsQRcodeActivity.class);
                intent.putExtra("title", shetuanMsg.getName());
                intent.putExtra("type", "shetuan");
                intent.putExtra("id", shetuanMsg.getShetuanid());
                ShetuanInformationActivity.this.startActivity(intent);
            }
        });
        binding.shetuanInformationCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hascollection) {
                    cancelcollectebody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("cmid", String.valueOf(shetuanMsg.getShetuanid()))
                            .build();
                    new Thread(new CancelCollecteRunnable()).start();

                } else {
                    collectebody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("cmid", String.valueOf(shetuanMsg.getShetuanid()))
                            .build();
                    new Thread(new CollecteRunnable()).start();
                }
            }
        });
        binding.shetuanInformationFunctbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.shetuanInformationFunctbutton.getText().equals("申请加入")) {
                    final EditTextWithDel editTextWithDel = new EditTextWithDel(ShetuanInformationActivity.this);
                    editTextWithDel.setBackgroundResource(R.drawable.pin_edit_sharp);
                    new AlertDialog.Builder(ShetuanInformationActivity.this).setTitle("申请理由")
                            .setView(editTextWithDel)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (editTextWithDel.getText() == null || editTextWithDel.getText().length() == 0) {
                                        Toast.makeText(ShetuanInformationActivity.this, "请填写理由", Toast.LENGTH_SHORT).show();
                                    } else {
                                        participatebody = new FormBody.Builder()
                                                .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                                                .add("uid", sharedPreferences.getString("phonenumber", "0"))
                                                .add("cmid", String.valueOf(shetuanMsg.getShetuanid()))
                                                .add("cmname", shetuanMsg.getName())
                                                .add("reason", editTextWithDel.getText().toString())
                                                .build();

                                        new Thread(new ParticipateRunnable()).start();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else if (binding.shetuanInformationFunctbutton.getText().equals("取消收藏")) {
                    cancelcollectebody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("cmid", String.valueOf(shetuanMsg.getShetuanid()))
                            .build();
                    new Thread(new CancelCollecteRunnable()).start();
                }
            }
        });
    }

    private void click2(final String datajson) {
        binding.shetuanInformationShetuanmember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShetuanInformationActivity.this, ShetuanMemberActivity.class);
                intent.putExtra("memberjson", datajson);
                ShetuanInformationActivity.this.startActivity(intent);
            }
        });
    }

    private class ShetuanMemberRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(shetuanmemberurl, shetuanmemberbody);
                Message message = handler.obtainMessage();
                message.what = SHETUAN_MEMBER;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class CollecteRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(collecteurl, collectebody);
                Message message = handler.obtainMessage();
                message.what = COLLECTE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class CancelCollecteRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(cancelcollecteurl, cancelcollectebody);
                Message message = handler.obtainMessage();
                message.what = CANCEL_COLLECTE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ParticipateRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(participateurl, participatebody);
                Message message = handler.obtainMessage();
                message.what = PARTICIPATE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetHeaderRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getheaderurl, getheaderbody);
                Message message = handler.obtainMessage();
                message.what = GET_HEADER;
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
                case SHETUAN_MEMBER:
                    String shetuanmemberresult = (String) msg.obj;
                    if (shetuanmemberresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(shetuanmemberresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String shetuannumberdata = jsonObject.getString("data");
                                int number;
                                if (shetuannumberdata.equals("null")) {
                                    number = 0;
                                } else {
                                    JSONTokener memberjsonTokener = new JSONTokener(shetuannumberdata);
                                    JSONArray memberjsonArray = (JSONArray) memberjsonTokener.nextValue();
                                    number = memberjsonArray.length();
                                }
                                binding.shetuanInformationShetuanmember.setText("有" + number + "个小伙伴哦");
                                click2(shetuannumberdata);
                            } else {
                                Toast.makeText(ShetuanInformationActivity.this, "活动成员获取失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShetuanInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case COLLECTE:
                    String collecteresult = (String) msg.obj;
                    if (collecteresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(collecteresult);
                            result = jsonObject.getInt("status");
                            System.out.println("result"+result);
                            if (result == 200) {
                                Toast.makeText(ShetuanInformationActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                binding.shetuanInformationCollection.setImageResource(R.drawable.ic_collection_after);
                                hascollection = true;
                            } else if (result == 500){
                                Toast.makeText(ShetuanInformationActivity.this, "你已经收藏过该活动", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShetuanInformationActivity.this, "收藏失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShetuanInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CANCEL_COLLECTE:
                    String cancelcollecteresult = (String) msg.obj;
                    if (cancelcollecteresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(cancelcollecteresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                if (binding.shetuanInformationFunctbutton.getText().equals("取消收藏")) {
                                    Toast.makeText(ShetuanInformationActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                    ShetuanInformationActivity.this.finish();
                                } else if (binding.shetuanInformationFunctbutton.getText().equals("申请加入")) {
                                    Toast.makeText(ShetuanInformationActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                    binding.shetuanInformationCollection.setImageResource(R.drawable.ic_collection_before);
                                }
                            } else {
                                Toast.makeText(ShetuanInformationActivity.this, "取消失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShetuanInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PARTICIPATE:
                    String partivipateresult = (String) msg.obj;
                    if (partivipateresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(partivipateresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(ShetuanInformationActivity.this, "成功发送申请", Toast.LENGTH_SHORT).show();
                            } else if (result == 500) {
                                Toast.makeText(ShetuanInformationActivity.this, "你已经发送过申请，不能再次发送", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ShetuanInformationActivity.this, "申请发送失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShetuanInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GET_HEADER:
                    String getheaderresult = (String) msg.obj;
                    if (getheaderresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getheaderresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String getheaderdata = jsonObject.getString("data");
                                JSONObject headerdata = new JSONObject(getheaderdata);
                                shetuanMsg.setShetuanbossname(headerdata.getString("name"));
                            } else {
                                Toast.makeText(ShetuanInformationActivity.this, "获取社长失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShetuanInformationActivity.this, "网络异常，获取社长失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };
}

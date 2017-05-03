package com.example.asus.shetuan.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.shetuan.ManageShetuanActivity;
import com.example.asus.shetuan.activity.shetuan.ShetuanPhonelistActivity;
import com.example.asus.shetuan.activity.shetuan.ShetuanRecruitActivity;
import com.example.asus.shetuan.bean.CommunityContacts;
import com.example.asus.shetuan.bean.MyShetuan;
import com.example.asus.shetuan.bean.ShetuanMsg;
import com.example.asus.shetuan.bean.ShetuanRequest;
import com.example.asus.shetuan.databinding.ActivityMyShetuanBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MyShetuanActivity extends AppCompatActivity {
    private ActivityMyShetuanBinding binding;
    private SharedPreferences sharedPreferences;
    private ArrayList<ShetuanRequest> requestsData = new ArrayList<>();
    private ShetuanMsg shetuanMsg;

    private String[] sItems;
    private int spinnercurrentposition;
    private int max;

    private OKHttpConnect okHttpConnect;
    private String getmyshetuanurl = "https://euswag.com/eu/community/mycommunitylist";
    private RequestBody getmyshetuanbody;

    private String getshetuanurl = "https://euswag.com/eu/community/getcommunity";
    private RequestBody getshetuanbody;

    private String setdefaultshetuanurl = "https://euswag.com/eu/community/setdefaultcm";
    private RequestBody setdefaultshetuanbody;

    private String shetuanlogourl = "https://euswag.com/picture/community/logo/";
    private String shetuanbackgroundurl = "https://euswag.com/picture/community/background/";

    private final int GET_MY_SHETUAN = 110;
    private final int GET_SHETUAN = 100;
    private final int SET_DEFAULT_ST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_shetuan);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        binding.setMyshetuan(new MyShetuan(this));
        spinnercurrentposition = -2;
        getmyshetuanbody = new FormBody.Builder()
                .add("uid", sharedPreferences.getString("phonenumber", "0"))
                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                .build();
        new Thread(new GetMyShetuanRunnable()).start();
    }

    private void click2() {
        binding.shetuanNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //设置Spinner点击事件
//                System.out.println(view.toString());
//                System.out.println(sItems[i]);
//                System.out.println(l);
                //i和l都表示所选的位置，从0开始
                if (spinnercurrentposition != i) {
                    spinnercurrentposition = i;
                    System.out.println("----" + spinnercurrentposition);
                    getshetuanbody = new FormBody.Builder()
                            .add("cmid", String.valueOf(requestsData.get(spinnercurrentposition).getCmid()))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                            .build();
                    new Thread(new GetShetuanRunnable()).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void click3() {
        binding.myShetuanManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestsData.get(spinnercurrentposition).getPosition() > 1) {
                    Intent intent = new Intent(MyShetuanActivity.this, ManageShetuanActivity.class);
                    intent.putExtra("cmLogo", shetuanMsg.getLogoimage());
                    intent.putExtra("cmAnnouncement", shetuanMsg.getShtuanannouncement());
                    intent.putExtra("cmDetail", shetuanMsg.getBriefintroduction());
                    intent.putExtra("cmid", shetuanMsg.getShetuanid());
                    intent.putExtra("boss",shetuanMsg.getShetuanboss());
                    intent.putExtra("position",requestsData.get(spinnercurrentposition).getPosition());
                    MyShetuanActivity.this.startActivity(intent);
                }else if (requestsData.get(spinnercurrentposition).getPosition()<1){
                    Toast.makeText(MyShetuanActivity.this,"您未成为该社团的正式成员，没有该权限",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MyShetuanActivity.this,"您不是该社团管理员，没有该权限",Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.myShetuanRecruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行权限判断,1为普通社员，2为管理员，3为社长，0为已面试
                if (requestsData.get(spinnercurrentposition).getPosition() > 1) {
                    Intent intent = new Intent(MyShetuanActivity.this, ShetuanRecruitActivity.class);
                    intent.putExtra("recruit", shetuanMsg.getShetuanrecruit());
                    intent.putExtra("cmid", shetuanMsg.getShetuanid());
                    MyShetuanActivity.this.startActivity(intent);
                }else if (requestsData.get(spinnercurrentposition).getPosition()<1){
                    Toast.makeText(MyShetuanActivity.this,"您未成为该社团的正式成员，没有该权限",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MyShetuanActivity.this,"您不是该社团管理员，没有该权限",Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.myShetuanPhonelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestsData.get(spinnercurrentposition).getPosition()>0){
                    Intent intent = new Intent(MyShetuanActivity.this, ShetuanPhonelistActivity.class);
                    intent.putExtra("cmid", shetuanMsg.getShetuanid());
                    MyShetuanActivity.this.startActivity(intent);
                }else {
                    Toast.makeText(MyShetuanActivity.this,"您不是该社团成员，无法获取通讯录",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class GetMyShetuanRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getmyshetuanurl, getmyshetuanbody);
                Message message = handler.obtainMessage();
                message.what = GET_MY_SHETUAN;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetShetuanRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getshetuanurl, getshetuanbody);
                Message message = handler.obtainMessage();
                message.what = GET_SHETUAN;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class SetDefaultShetuanRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(setdefaultshetuanurl, setdefaultshetuanbody);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_MY_SHETUAN:
                    String getmyshetuanresult = (String) msg.obj;
                    if (getmyshetuanresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getmyshetuanresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String getmyshetuandata = jsonObject.getString("data");
                                if (getmyshetuandata.equals("null")) {

                                } else {
                                    JSONTokener jsonTokener = new JSONTokener(getmyshetuandata);
                                    JSONArray jsonArray = (JSONArray) jsonTokener.nextValue();
                                    requestsData.clear();
                                    sItems = new String[jsonArray.length()];
                                    max = jsonArray.getJSONObject(0).getInt("lastselect");
                                    int flag = 0;
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ShetuanRequest shetuanRequest = new ShetuanRequest(jsonArray.getJSONObject(i).getInt("cmid"), jsonArray.getJSONObject(i).getInt("position"), jsonArray.getJSONObject(i).getString("reason"), jsonArray.getJSONObject(i).getString("cmname"), jsonArray.getJSONObject(i).getInt("lastselect"));
                                        requestsData.add(shetuanRequest);
                                        sItems[i] = requestsData.get(i).getCmname();
                                        if (max < requestsData.get(i).getLastselect()) {
                                            max = requestsData.get(i).getLastselect();
                                            flag = i;
                                        }
                                    }
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MyShetuanActivity.this, R.layout.simple_spinner_item, sItems);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    binding.shetuanNameSpinner.setAdapter(adapter);
                                    binding.shetuanNameSpinner.setSelection(flag, true);
                                    spinnercurrentposition = flag;
                                    getshetuanbody = new FormBody.Builder()
                                            .add("cmid", String.valueOf(requestsData.get(spinnercurrentposition).getCmid()))
                                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                            .build();
                                    new Thread(new GetShetuanRunnable()).start();
                                    click2();
                                }

                            } else {
                                Toast.makeText(MyShetuanActivity.this, "我的社团获取失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MyShetuanActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GET_SHETUAN:
                    String getshetuanresult = (String) msg.obj;
                    if (getshetuanresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getshetuanresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String getshetuandata = jsonObject.getString("data");
                                JSONObject getshetuan = new JSONObject(getshetuandata);
                                shetuanMsg = new ShetuanMsg(getshetuan.getString("cmName"), getshetuan.getString("cmDetail"), shetuanbackgroundurl + getshetuan.getString("cmBackground") + ".jpg", shetuanlogourl + getshetuan.getString("cmLogo"));
                                shetuanMsg.setShetuanid(getshetuan.getInt("cmid"));
                                shetuanMsg.setShetuantype(getshetuan.getInt("cmType"));
                                shetuanMsg.setShetuanattr(getshetuan.getInt("cmAttr"));
                                shetuanMsg.setShetuanrecruit(getshetuan.getInt("cmRecruit"));
                                shetuanMsg.setShetuanheat(getshetuan.getInt("cmHeat"));
                                shetuanMsg.setShtuanannouncement(getshetuan.getString("cmAnnouncement"));
                                shetuanMsg.setShetuanschool(getshetuan.getString("cmSchool"));
                                shetuanMsg.setShetuanboss(getshetuan.getLong("cmBoss"));
                                binding.myShetuanAnnouncement.setText(shetuanMsg.getShtuanannouncement());
                                binding.myShetuanLogo.setImageURI(shetuanMsg.getLogoimage());
                                if (requestsData.get(spinnercurrentposition).getPosition()==-3){
                                    Toast.makeText(MyShetuanActivity.this,"您已向该社团提交申请",Toast.LENGTH_SHORT).show();
                                }else if (requestsData.get(spinnercurrentposition).getPosition()==-2){
                                    Toast.makeText(MyShetuanActivity.this,"您提交的社团申请已通过审核",Toast.LENGTH_SHORT).show();
                                }else if (requestsData.get(spinnercurrentposition).getPosition()==-1){
                                    Toast.makeText(MyShetuanActivity.this,"您已通过该社团的笔试",Toast.LENGTH_SHORT).show();
                                }else if (requestsData.get(spinnercurrentposition).getPosition()==0){
                                    Toast.makeText(MyShetuanActivity.this,"您已通过该社团的面试",Toast.LENGTH_SHORT).show();
                                }else if (requestsData.get(spinnercurrentposition).getPosition()>0) {
                                    click3();
                                }
                            } else {
                                Toast.makeText(MyShetuanActivity.this, "获取社团信息失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MyShetuanActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        setdefaultshetuanbody = new FormBody.Builder()
                .add("cmid", String.valueOf(requestsData.get(spinnercurrentposition).getCmid()))
                .add("uid", sharedPreferences.getString("phonenumber", "0"))
                .add("num", String.valueOf(max + 1))
                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                .build();
        new Thread(new SetDefaultShetuanRunnable()).start();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (spinnercurrentposition == -2) {

        }else {
            getshetuanbody = new FormBody.Builder()
                    .add("cmid", String.valueOf(requestsData.get(spinnercurrentposition).getCmid()))
                    .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                    .build();
            new Thread(new GetShetuanRunnable()).start();
        }
        super.onResume();
    }
}

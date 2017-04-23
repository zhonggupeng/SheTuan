package com.example.asus.shetuan.activity.act;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.MemberSliderDeleteAdapter;
import com.example.asus.shetuan.bean.PeosonInformation;
import com.example.asus.shetuan.databinding.ActivityCheckListBinding;
import com.example.asus.shetuan.model.NetWorkState;
import com.example.asus.shetuan.model.OKHttpConnect;
import com.example.asus.shetuan.model.OnItemActionListener;
import com.example.asus.shetuan.weight.EditTextWithDel;
import com.example.asus.shetuan.weight.VerticalSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CheckListActivity extends AppCompatActivity implements VerticalSwipeRefreshLayout.OnRefreshListener{

    private ActivityCheckListBinding binding;
    private SharedPreferences sharedPreferences;
    private MemberSliderDeleteAdapter adapter;
    private Intent intent;

    private OKHttpConnect okHttpConnect;
    private String requestmemberurl = "https://euswag.com/eu/activity/memberinfolist";
    private RequestBody requestmemberbody;

    private String headimageloadurl = "https://euswag.com/picture/user/";

    private String rejecturl = "https://euswag.com/eu/activity/manage";
    private RequestBody rejectbody;

    private final int REQUEST_PHONE = 110;
    private final int REFRESH = 101;
    private final int REJECT = 100;

    private int itemposition;

    private ArrayList<PeosonInformation> peosonData = new ArrayList<PeosonInformation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_check_list);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new MemberSliderDeleteAdapter(this);
        binding.checkListRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        intent = getIntent();
        requestmemberbody = new FormBody.Builder()
                .add("avid",String.valueOf(intent.getIntExtra("actid",-1)))
                .add("accesstoken",sharedPreferences.getString("accesstoken","00"))
                .add("choice","0")
                .build();
        onRefresh();
        binding.checkListRefresh.setOnRefreshListener(this);
        binding.checkListRefresh.setDistanceToTriggerSync(300);
        binding.checkListRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        click();
    }
    private void click(){
        binding.checkListRecyclerview.setOnItemActionListener(new OnItemActionListener() {
            @Override
            public void OnItemClick(int position) {

            }

            @Override
            public void OnItemDelete(final int position) {
                final EditTextWithDel editTextWithDel = new EditTextWithDel(CheckListActivity.this);
                new AlertDialog.Builder(CheckListActivity.this).setTitle("理由")
                        .setView(editTextWithDel)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextWithDel.getText()==null||editTextWithDel.getText().length()==0){
                                    Toast.makeText(CheckListActivity.this,"请填写理由",Toast.LENGTH_SHORT).show();
                                }else {
                                    itemposition = position;
                                    rejectbody = new FormBody.Builder()
                                            .add("uid",String.valueOf(peosonData.get(position).getUid()))
                                            .add("accesstoken",sharedPreferences.getString("accesstoken","00"))
                                            .add("avid",String.valueOf(intent.getIntExtra("actid",-1)))
                                            .add("verifystate","-1")
                                            .add("reason",editTextWithDel.getText().toString())
                                            .build();
                                    if (NetWorkState.checkNetWorkState(CheckListActivity.this)) {
                                        new Thread(new RejectRunnable()).start();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
            }
        });
        binding.checkListBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckListActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    private class RequestMemberRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(requestmemberurl,requestmemberbody);
                Message message = handler.obtainMessage();
                message.what = REQUEST_PHONE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class RejectRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(rejecturl,rejectbody);
                Message message = handler.obtainMessage();
                message.what = REJECT;
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
                case REQUEST_PHONE:
                    String requestphoneresult = (String) msg.obj;
                    if (requestphoneresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(requestphoneresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                peosonData.clear();
                                String requestphonedata = jsonObject.getString("data");
                                //返回的时"null"，而不是null
                                if (requestphonedata.equals("null")) {
                                }else {
                                    JSONTokener memberjsonTokener = new JSONTokener(requestphonedata);
                                    JSONArray memberjsonArray = (JSONArray) memberjsonTokener.nextValue();
                                    for (int i = 0; i < memberjsonArray.length(); i++) {
                                        PeosonInformation peosonInformation = new PeosonInformation();
                                        peosonInformation.setUid(memberjsonArray.getJSONObject(i).getLong("uid"));
                                        peosonInformation.setNickname(memberjsonArray.getJSONObject(i).getString("nickname"));
                                        peosonInformation.setName(memberjsonArray.getJSONObject(i).getString("name"));
                                        peosonInformation.setHeadimage(headimageloadurl + memberjsonArray.getJSONObject(i).getString("avatar") + ".jpg");
                                        peosonInformation.setReputation("节操值 " + memberjsonArray.getJSONObject(i).getInt("reputation"));
                                        peosonInformation.setVerified(memberjsonArray.getJSONObject(i).getInt("verified"));
                                        peosonData.add(peosonInformation);
                                    }
                                }
                                adapter.setmData(null);
                                adapter.setmData(peosonData);
                            }else {
                                Toast.makeText(CheckListActivity.this,"参加活动成员的数据请求失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CheckListActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.checkListRecyclerview.removeAllViews();
                    binding.checkListRecyclerview.setAdapter(adapter);
                    break;
                case REFRESH:
                    if (NetWorkState.checkNetWorkState(CheckListActivity.this)) {
                        new Thread(new RequestMemberRunnable()).start();
                    }
                    binding.checkListRefresh.setRefreshing(false);
                    break;
                case REJECT:
                    String rejectresult = (String) msg.obj;
                    if (rejectresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(rejectresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                peosonData.remove(itemposition);
                                adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(CheckListActivity.this,"拒绝失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(CheckListActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

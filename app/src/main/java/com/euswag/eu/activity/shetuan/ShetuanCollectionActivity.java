package com.euswag.eu.activity.shetuan;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.adapter.ShetuanCollectionAdapter;
import com.euswag.eu.bean.ShetuanMsg;
import com.euswag.eu.databinding.ActivityShetuanCollectionBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ShetuanCollectionActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityShetuanCollectionBinding binding;
    private SharedPreferences sharedPreferences;
    private ShetuanCollectionAdapter adapter;
    private ArrayList<ShetuanMsg> mData = new ArrayList<>();

    private OKHttpConnect okHttpConnect;
    private String loadcollectionurl = "/community/collectedcm";
    private RequestBody loadcollectionbody;

    private String shetuanlogourl = "https://eu-1251935523.file.myqcloud.com/community/logo/cmlogo";
    private String shetuanbackgroundurl = "https://eu-1251935523.file.myqcloud.com/community/background/cmbackground";

    private final int REFRESH = 110;
    private final int LOAD_COLLECTION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_shetuan_collection);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new ShetuanCollectionAdapter(this);
        onRefresh();
        binding.shetuanCollectionRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.shetuanCollectionRefresh.setOnRefreshListener(this);
        binding.shetuanCollectionRefresh.setDistanceToTriggerSync(300);
        binding.shetuanCollectionRefresh.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }
    private class LoadCollectionRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(loadcollectionurl,loadcollectionbody);
                Message message = handler.obtainMessage();
                message.what = LOAD_COLLECTION;
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
                case REFRESH:
                    loadcollectionbody = new FormBody.Builder()
                            .add("uid",sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .build();
                    new Thread(new LoadCollectionRunnable()).start();
                    binding.shetuanCollectionRefresh.setRefreshing(false);
                    break;
                case LOAD_COLLECTION:
                    String loadcollectionresult = (String) msg.obj;
                    if (loadcollectionresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(loadcollectionresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String collectiondata = jsonObject.getString("data");
                                JSONTokener jsonTokener = new JSONTokener(collectiondata);
                                JSONArray jsonArray ;
                                jsonArray = (JSONArray) jsonTokener.nextValue();
                                mData.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ShetuanMsg shetuanMsg = new ShetuanMsg(jsonArray.getJSONObject(i).getString("cmName"), jsonArray.getJSONObject(i).getString("cmDetail"), shetuanbackgroundurl+jsonArray.getJSONObject(i).getString("cmBackground")+".jpg", shetuanlogourl+jsonArray.getJSONObject(i).getString("cmLogo")+".png");
                                    shetuanMsg.setShetuanJsonString(jsonArray.getJSONObject(i).toString());
                                    mData.add(shetuanMsg);
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            }else {
                                Toast.makeText(ShetuanCollectionActivity.this,"加载失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ShetuanCollectionActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.shetuanCollectionRecyclerview.removeAllViews();
                    binding.shetuanCollectionRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
}

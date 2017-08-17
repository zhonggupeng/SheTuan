package com.euswag.eu.activity.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.adapter.ShetuanCollectionAdapter;
import com.euswag.eu.bean.ShetuanMsg;
import com.euswag.eu.databinding.ActivitySearchShetuanBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class SearchShetuanActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivitySearchShetuanBinding binding;
    private SharedPreferences sharedPreferences;
    private ShetuanCollectionAdapter adapter;
    private ArrayList<ShetuanMsg> mData = new ArrayList<>();
    private Intent intent;

    private OKHttpConnect okHttpConnect;
    private String loadshetuan = "/community/search";
    private RequestBody loadshetuanbody;

    private String shetuanlogourl = "https://eu-1251935523.file.myqcloud.com/community/logo/cmlogo";
    private String shetuanbackgroundurl = "https://eu-1251935523.file.myqcloud.com/community/background/cmbg";

    private final int REFRESH = 110;
    private final int LOADSHETUAN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_shetuan);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new ShetuanCollectionAdapter(this);
        intent = getIntent();
        onRefresh();
        binding.activitySearchShetuanRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.activitySearchShetuanRefresh.setOnRefreshListener(this);
        binding.activitySearchShetuanRefresh.setDistanceToTriggerSync(300);
        binding.activitySearchShetuanRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        click();
    }

    private void click(){
        binding.activitySearchShetuanBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchShetuanActivity.this.onRefresh();
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    private class LoadShetuanRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(loadshetuan,loadshetuanbody);
                Message message = handler.obtainMessage();
                message.what = LOADSHETUAN;
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
                    loadshetuanbody = new FormBody.Builder()
                            .add("keyword",intent.getStringExtra("keyword"))
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .build();
                    new Thread(new LoadShetuanRunnable()).start();
                    binding.activitySearchShetuanRefresh.setRefreshing(false);
                    break;
                case LOADSHETUAN:
                    String loadcollectionresult = (String) msg.obj;
                    if (loadcollectionresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(loadcollectionresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                binding.activitySearchShetuanRefresh.setRefreshing(false);
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
                                Toast.makeText(SearchShetuanActivity.this,"加载失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(SearchShetuanActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.activitySearchShetuanRecyclerview.removeAllViews();
                    binding.activitySearchShetuanRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };

}

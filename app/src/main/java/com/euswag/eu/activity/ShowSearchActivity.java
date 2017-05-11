package com.euswag.eu.activity;

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
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.adapter.ActivityRecyclerviewAdapter;
import com.euswag.eu.adapter.FindingRecyclerviewAdapter;
import com.euswag.eu.adapter.SearchPeopleAdapter;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.bean.PeosonInformation;
import com.euswag.eu.bean.ShetuanMsg;
import com.euswag.eu.databinding.ActivityShowSearchBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ShowSearchActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityShowSearchBinding binding;
    private SharedPreferences sharedPreferences;
    private Intent getintent;
    private ArrayList<ActivityMsg> activityData = new ArrayList<>();
    private ArrayList<ShetuanMsg> shetuanData = new ArrayList<>();
    private ArrayList<PeosonInformation> peopleData = new ArrayList<>();
    private SearchPeopleAdapter peopleAdapter;
    private ActivityRecyclerviewAdapter activityAdapter;
    private FindingRecyclerviewAdapter shetuanAdapter;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String activityurl = "/activity/search";
    private RequestBody activitybody;

    private String shetuanurl = "/community/search";
    private RequestBody shetuanbody;

    private String peopleurl = "/info/search";
    private RequestBody peoplebody;

    private String headimageloadurl = "https://eu-1251935523.file.myqcloud.com/user/user";
    private String shetuanlogourl = "https://eu-1251935523.file.myqcloud.com/community/logo/cmlogo";
    private String shetuanbackgroundurl = "https://eu-1251935523.file.myqcloud.com/community/background/cmbg";
    private String activityimageloadurl = "https://eu-1251935523.file.myqcloud.com/activity/av";

    private int page = 2;

    private final int ACTIVITYS = 110;
    private final int SHETUANS = 100;
    private final int PEOPLES = 101;
    private final int REFRESH = 111;
    private final int LOAD_MORE = 121;
    private final int LOAD_MOREACTIVITY = 131;
    private final int LOAD_MORESHETUAN = 141;
    private final int LOAD_MOREPEOPLE = 151;

    private final int NORMAL = 1110;
    private final int LOADING = 1111;
    private final int THEEND = 1100;
    private final int LOADERROR = 1101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_search);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        peopleAdapter = new SearchPeopleAdapter(this);
        activityAdapter = new ActivityRecyclerviewAdapter(this);
        shetuanAdapter = new FindingRecyclerviewAdapter(this);
        getintent = getIntent();
        binding.showSearchRecycleview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.showSearchRefresh.setOnRefreshListener(this);
        binding.showSearchRefresh.setDistanceToTriggerSync(300);
        binding.showSearchRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        onRefresh();
        click();
    }

    private void click() {
        binding.showSearchBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowSearchActivity.this.onBackPressed();
            }
        });
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH);
    }

    public void LoadMore() {
        handler.sendEmptyMessage(LOAD_MORE);
    }

    private class SearchActivityRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(activityurl, activitybody);
                Message message = handler.obtainMessage();
                message.what = ACTIVITYS;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SearchShetuanRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(shetuanurl, shetuanbody);
                Message message = handler.obtainMessage();
                message.what = SHETUANS;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SearchPeopleRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(peopleurl, peoplebody);
                Message message = handler.obtainMessage();
                message.what = PEOPLES;
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
                case REFRESH:
                    if (getintent.getStringExtra("type").equals("activity")) {
                        activitybody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("keyword", getintent.getStringExtra("keyword"))
                                .build();
                        new Thread(new SearchActivityRunnable()).start();
                    } else if (getintent.getStringExtra("type").equals("shetuan")) {
                        shetuanbody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("keyword", getintent.getStringExtra("keyword"))
                                .build();
                        new Thread(new SearchShetuanRunnable()).start();
                    } else {
                        peoplebody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("keyword", getintent.getStringExtra("keyword"))
                                .build();
                        new Thread(new SearchPeopleRunnable()).start();
                    }
                    binding.showSearchRefresh.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    if (getintent.getStringExtra("type").equals("activity")) {
                        activitybody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("keyword", getintent.getStringExtra("keyword"))
                                .add("page", String.valueOf(page))
                                .build();
                        new Thread(new SearchActivityRunnable()).start();
                    } else if (getintent.getStringExtra("type").equals("shetuan")) {
                        shetuanbody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("keyword", getintent.getStringExtra("keyword"))
                                .add("page", String.valueOf(page))
                                .build();
                        new Thread(new SearchShetuanRunnable()).start();
                    } else {
                        peoplebody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("keyword", getintent.getStringExtra("keyword"))
                                .add("page", String.valueOf(page))
                                .build();
                        new Thread(new SearchPeopleRunnable()).start();
                    }
                    break;
                case ACTIVITYS:
                    String activityresult = (String) msg.obj;
                    if (activityresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(activityresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                //搜索结果为活动数组
                            } else {
                                Toast.makeText(ShowSearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShowSearchActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case SHETUANS:
                    String shetuanresult = (String) msg.obj;
                    if (shetuanresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(shetuanresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                //搜索结果为社团数组
                                String shetuandata = jsonObject.getString("data");
                                JSONTokener jsonTokener = new JSONTokener(shetuandata);
                                JSONArray jsonArray;
                                jsonArray = (JSONArray) jsonTokener.nextValue();
                                shetuanData.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ShetuanMsg shetuanMsg = new ShetuanMsg(jsonArray.getJSONObject(i).getString("cmName"), jsonArray.getJSONObject(i).getString("cmDetail"), shetuanbackgroundurl + jsonArray.getJSONObject(i).getString("cmBackground") + ".jpg", shetuanlogourl + jsonArray.getJSONObject(i).getString("cmLogo")+".png");
                                    shetuanMsg.setShetuanJsonString(jsonArray.getJSONObject(i).toString());
                                    shetuanData.add(shetuanMsg);
                                }
                                shetuanAdapter.setmData(null);
                                shetuanAdapter.setmData(shetuanData);
                            } else {
                                Toast.makeText(ShowSearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShowSearchActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    binding.showSearchRecycleview.removeAllViews();
                    binding.showSearchRecycleview.setAdapter(shetuanAdapter);
                    binding.showSearchScrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            View childView = binding.showSearchScrollview.getChildAt(0);
                            if ((childView.getMeasuredHeight() < binding.showSearchScrollview.getScrollY() + binding.showSearchScrollview.getHeight()) && (shetuanAdapter.getStatus() == NORMAL)) {
                                shetuanAdapter.setStatus(LOADING);
                                changeAdapterStateST(LOADING);
                                LoadMore();
                            } else {
                                if (shetuanAdapter.getStatus() == THEEND || shetuanAdapter.getStatus() == LOADERROR) {
                                    shetuanAdapter.setStatus(NORMAL);
                                }
                            }
                        }
                    });
                    break;
                case PEOPLES:
                    String peopleresult = (String) msg.obj;
                    if (peopleresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(peopleresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                //搜索结果为信息数组
                            } else {
                                Toast.makeText(ShowSearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ShowSearchActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case LOAD_MOREACTIVITY:
                    break;
                case LOAD_MORESHETUAN:
                    break;
                case LOAD_MOREPEOPLE:
                    break;
                default:
                    break;
            }
        }
    };
    protected void changeAdapterStateST(int status) {
        if (shetuanAdapter != null && shetuanAdapter.footerHolder != null) {
            shetuanAdapter.footerHolder.setData(status);
        }
    }
    protected void changeAdapterStateAT(int status) {
        if (activityAdapter != null && activityAdapter.footerHolder != null) {
            activityAdapter.footerHolder.setData(status);
        }
    }
    protected void changeAdapterStatePP(int status) {
        if (peopleAdapter != null && peopleAdapter.footerHolder != null) {
            peopleAdapter.footerHolder.setData(status);
        }
    }
}

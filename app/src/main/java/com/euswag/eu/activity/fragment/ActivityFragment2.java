package com.euswag.eu.activity.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.adapter.NoLoadmoreActivityRecyclerviewAdapter;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.databinding.FragmentActivityBinding;
import com.euswag.eu.model.DateUtils;
import com.euswag.eu.model.OKHttpConnect;
import com.euswag.eu.weight.VerticalSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class ActivityFragment2 extends Fragment implements VerticalSwipeRefreshLayout.OnRefreshListener {
    private FragmentActivityBinding binding = null;

    private NoLoadmoreActivityRecyclerviewAdapter adapter;

    private OKHttpConnect okHttpConnect;
    private String createdurl = "/activity/createdav";
    private RequestBody creatbody;
    private String imageloadurl = "https://eu-1251935523.file.myqcloud.com/activity/av";

    private final int REFRESH_COMPLETE = 0x1101;
    private final int ACTIVITYREFRESH = 0x1010;

    private LayoutInflater inflater;

    private ArrayList<ActivityMsg> mData = new ArrayList<ActivityMsg>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            this.inflater = inflater;
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_activity, container, false);
            adapter = new NoLoadmoreActivityRecyclerviewAdapter(inflater.getContext());
            SharedPreferences sharedPreferences = inflater.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            creatbody = new FormBody.Builder()
                    .add("uid", sharedPreferences.getString("phonenumber", "0"))
                    .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                    .build();
            onRefresh();
            binding.fragmentActivityRecyclerview.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            binding.fragmentActivityScrollview.smoothScrollTo(0, 40);
            binding.fragmentActivityRefresh.setOnRefreshListener(this);
            binding.fragmentActivityRefresh.setDistanceToTriggerSync(300);
            binding.fragmentActivityRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        }
        return binding.getRoot();
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH_COMPLETE);
    }

    private class CreatedRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(createdurl, creatbody);
                Message message = handler.obtainMessage();
                message.what = ACTIVITYREFRESH;
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
                case REFRESH_COMPLETE:
                    new Thread(new CreatedRunnable()).start();
                    binding.fragmentActivityRefresh.setRefreshing(false);
                    break;
                case ACTIVITYREFRESH:
                    String activityrefreshresult = (String) msg.obj;
                    if (activityrefreshresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(activityrefreshresult);
                            result = jsonObject.getInt("status");
                            System.out.println("result:  " + result);
                            if (result == 200) {
                                String activityrefreshdata;
                                activityrefreshdata = jsonObject.getString("data");
                                System.out.println("activityrefreshdata" + activityrefreshdata);
                                JSONTokener activityRefreshJsonTokener = new JSONTokener(activityrefreshdata);
                                JSONArray activityRefreshJsonArray = null;
                                activityRefreshJsonArray = (JSONArray) activityRefreshJsonTokener.nextValue();
//                        changeData.clear();
//                        changeData.addAll(mData);
                                mData.clear();
                                for (int i = 0; i < activityRefreshJsonArray.length(); i++) {
                                    //对于时间要进行处理，即时间格式的转换
                                    ActivityMsg activityMsg = new ActivityMsg(activityRefreshJsonArray.getJSONObject(i).getString("avTitle"), activityRefreshJsonArray.getJSONObject(i).getString("avPlace"), DateUtils.timet(activityRefreshJsonArray.getJSONObject(i).getString("avStarttime")), imageloadurl + activityRefreshJsonArray.getJSONObject(i).getString("avLogo") + ".jpg");
                                    activityMsg.setActivityDetailJsonString(activityRefreshJsonArray.getJSONObject(i).toString());
                                    activityMsg.setIsbuild(1);
                                    mData.add(activityMsg);
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            } else {
                                Toast.makeText(inflater.getContext(), "加载失败，刷新试试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(inflater.getContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    binding.fragmentActivityRecyclerview.removeAllViews();
                    binding.fragmentActivityRecyclerview.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}

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
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.adapter.FindingRecyclerviewAdapter;
import com.euswag.eu.bean.ShetuanMsg;
import com.euswag.eu.databinding.FragmentFindingBinding;
import com.euswag.eu.model.NetWorkState;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class FindingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private FragmentFindingBinding binding = null ;
    private SharedPreferences sharedPreferences;

    private ArrayList<ShetuanMsg> mData = new ArrayList<>();
    private FindingRecyclerviewAdapter findingRecyclerviewAdapter;
    private LayoutInflater inflater;
    private OKHttpConnect okHttpConnect;
    private String shetuanurl="https://euswag.com/eu/community/commoncm";
    private int page = 2;
    private RequestBody shetuanbody;

    private String shetuanlogourl = "https://euswag.com/picture/community/logo/";
    private String shetuanbackgroundurl = "https://euswag.com/picture/community/background/";

    private final int REFRESH_COMPLETE = 0x1100;
    private final int LOAD_MORE = 0x1111;
    private final int LOADSHETUAN = 0x1000;
    private final int LOAD_MORE_SHETUAN = 0x1001;
    private final int NORMAL = 110;
    private final int LOADING = 111;
    private final int THEEND = 100;
    private final int LOADERROR =101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            this.inflater = inflater;
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_finding, container, false);
            findingRecyclerviewAdapter = new FindingRecyclerviewAdapter(inflater.getContext());
            sharedPreferences = inflater.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            onRefresh();
            binding.shetuanItemRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            changeAdapterState(NORMAL);
            findingRecyclerviewAdapter.setStatus(NORMAL);
            binding.findFragmentSwiperRefreshlayout.setOnRefreshListener(this);
            binding.findFragmentSwiperRefreshlayout.setDistanceToTriggerSync(300);
            binding.findFragmentSwiperRefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
        }
        return binding.getRoot();
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH_COMPLETE);
    }

    public void LoadMore() {
        handler.sendEmptyMessage(LOAD_MORE);
    }

    private class ShetuanRunable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(shetuanurl,shetuanbody);
                Message message = handler.obtainMessage();
                message.what = LOADSHETUAN;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ShetuanLoadmoreRunable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(shetuanurl,shetuanbody);
                Message message = handler.obtainMessage();
                message.what = LOAD_MORE_SHETUAN;
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
                case REFRESH_COMPLETE:
                    shetuanbody = new FormBody.Builder()
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .build();
                    if (NetWorkState.checkNetWorkState(inflater.getContext())) {
                        new Thread(new ShetuanRunable()).start();
                    }
                    binding.findFragmentSwiperRefreshlayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    shetuanbody = new FormBody.Builder()
                            .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                            .add("page",String.valueOf(page))
                            .build();
                    if (NetWorkState.checkNetWorkState(inflater.getContext())) {
                        new Thread(new ShetuanLoadmoreRunable()).start();
                    }
                    break;
                case LOADSHETUAN:
                    String loadshetuanresult = (String) msg.obj;
                    if (loadshetuanresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(loadshetuanresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String shetuandata = jsonObject.getString("data");
                                JSONTokener jsonTokener = new JSONTokener(shetuandata);
                                JSONArray jsonArray ;
                                jsonArray = (JSONArray) jsonTokener.nextValue();
                                mData.clear();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    ShetuanMsg shetuanMsg = new ShetuanMsg(jsonArray.getJSONObject(i).getString("cmName"), jsonArray.getJSONObject(i).getString("cmDetail"), shetuanbackgroundurl+jsonArray.getJSONObject(i).getString("cmBackground")+".jpg", shetuanlogourl+jsonArray.getJSONObject(i).getString("cmLogo"));
                                    shetuanMsg.setShetuanJsonString(jsonArray.getJSONObject(i).toString());
                                    mData.add(shetuanMsg);
                                }
                                findingRecyclerviewAdapter.setmData(null);
                                findingRecyclerviewAdapter.setmData(mData);
                            }
                            else {
                                Toast.makeText(inflater.getContext(),"加载失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(inflater.getContext(),"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.shetuanItemRecyclerView.removeAllViews();
                    binding.shetuanItemRecyclerView.setAdapter(findingRecyclerviewAdapter);
                    System.out.println("page1:  "+page);
                    binding.findFragmentScrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                        @Override
                        public void onScrollChanged() {
                            View childView = binding.findFragmentScrollview.getChildAt(0);
                            if ((childView.getMeasuredHeight() <binding.findFragmentScrollview.getScrollY() +binding.findFragmentScrollview.getHeight())&&(findingRecyclerviewAdapter.getStatus()==NORMAL)){
                                findingRecyclerviewAdapter.setStatus(LOADING);
                                changeAdapterState(LOADING);
                                LoadMore();
                            }
                            else {
                                if (findingRecyclerviewAdapter.getStatus()==THEEND||findingRecyclerviewAdapter.getStatus()==LOADERROR) {
                                    findingRecyclerviewAdapter.setStatus(NORMAL);
                                }
                            }
                        }
                    });
                    break;
                case LOAD_MORE_SHETUAN:
                    String loadmoreshetuanresult = (String) msg.obj;
                    if (loadmoreshetuanresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(loadmoreshetuanresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String loadmoreshetuandata = jsonObject.getString("data");
                                JSONTokener loadmoreJsonTokener = new JSONTokener(loadmoreshetuandata);
                                JSONArray loadmoreJsonArray = null;
                                loadmoreJsonArray = (JSONArray) loadmoreJsonTokener.nextValue();
                                if (loadmoreJsonArray.length()!=0){
                                    for (int i = 0; i < loadmoreJsonArray.length(); i++) {
                                        ShetuanMsg shetuanMsg = new ShetuanMsg(loadmoreJsonArray.getJSONObject(i).getString("cmName"), loadmoreJsonArray.getJSONObject(i).getString("cmDetail"),shetuanbackgroundurl+loadmoreJsonArray.getJSONObject(i).getString("cmBackground")+".jpg", shetuanlogourl+loadmoreJsonArray.getJSONObject(i).getString("cmLogo"));
                                        shetuanMsg.setShetuanJsonString(loadmoreJsonArray.getJSONObject(i).toString());
                                        mData.add(shetuanMsg);
                                    }
                                    //
                                    //可能会有逻辑上的错误
                                    //
                                    page++;
                                    findingRecyclerviewAdapter.setStatus(NORMAL);
                                    changeAdapterState(NORMAL);
                                }
                                else {
                                    findingRecyclerviewAdapter.setStatus(THEEND);
                                    changeAdapterState(THEEND);
                                }
                            }
                            else {
                                Toast.makeText(inflater.getContext(),"加载失败，刷新试试",Toast.LENGTH_SHORT).show();
                                findingRecyclerviewAdapter.setStatus(LOADERROR);
                                changeAdapterState(LOADERROR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(inflater.getContext(),"网络异常",Toast.LENGTH_SHORT).show();
                        findingRecyclerviewAdapter.setStatus(LOADERROR);
                        changeAdapterState(LOADERROR);
                    }
                    findingRecyclerviewAdapter.notifyDataSetChanged();
                    System.out.println("page2:  "+page);
                    break;
                default:break;
            }
        }
    };
    protected void changeAdapterState(int status){
        if (findingRecyclerviewAdapter!=null&&findingRecyclerviewAdapter.footerHolder!=null){
            findingRecyclerviewAdapter.footerHolder.setData(status);
        }
    }

}

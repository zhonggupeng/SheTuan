package com.example.asus.shetuan.activity.fragment;

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

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.FindingRecyclerviewAdapter;
import com.example.asus.shetuan.bean.ShetuanMsg;
import com.example.asus.shetuan.databinding.FragmentFindingBinding;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;


public class FindingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private FragmentFindingBinding binding = null ;

    private ArrayList<ShetuanMsg> mData = new ArrayList<>();
    private FindingRecyclerviewAdapter findingRecyclerviewAdapter;
    private LayoutInflater inflater;
    private OKHttpConnect okHttpConnect;
    private OKHttpConnect loadmoreOkHttpConnect;
    private String jsonString;
    private String loadmoreJsonString;
    private String url="https://euswag.com/eu/community/commoncm";
    private String tocken = "?accesstocken";
    private String paramName1 = "&page=";
    private int page = 1;
    private String pageparam = paramName1+page;

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
            onRefresh();
            binding.shetuanItemRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
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

//    @Override
//    public void onItemClick(View view, int indexOfData) {
//        Toast.makeText(inflater.getContext(), "点击了第" + indexOfData + "条", Toast.LENGTH_LONG).show();
//    }


    private class ShetuanRunable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(url+tocken);
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
            loadmoreOkHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = loadmoreOkHttpConnect.getdata(url+tocken+pageparam);
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
                    new Thread(new ShetuanRunable()).start();

                    binding.findFragmentSwiperRefreshlayout.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    new Thread(new ShetuanLoadmoreRunable()).start();

                    if (loadmoreJsonString.length()!=0){
                        JSONTokener loadmoreJsonTokener = new JSONTokener(loadmoreJsonString);
                        JSONArray loadmoreJsonArray = null;
                        try {
                            loadmoreJsonArray = (JSONArray) loadmoreJsonTokener.nextValue();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (loadmoreJsonArray.length()!=0){
                            for (int i = 0; i < loadmoreJsonArray.length(); i++) {
                                try {
                                    ShetuanMsg shetuanMsg = new ShetuanMsg(loadmoreJsonArray.getJSONObject(i).getString("cmName"), loadmoreJsonArray.getJSONObject(i).getString("cmDetail"), loadmoreJsonArray.getJSONObject(i).getString("cmBackground"), loadmoreJsonArray.getJSONObject(i).getString("cmLogo"));
                                    shetuanMsg.setShetuanJsonString(loadmoreJsonArray.getJSONObject(i).toString());
                                    mData.add(shetuanMsg);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            //
                            //可能会有逻辑上的错误
                            //
                            page++;
                            pageparam = paramName1+page;
                            findingRecyclerviewAdapter.setStatus(NORMAL);
                            changeAdapterState(NORMAL);
                        }
                        else {
                            findingRecyclerviewAdapter.setStatus(THEEND);
                            changeAdapterState(THEEND);
                        }
                    }
                    else {
                        findingRecyclerviewAdapter.setStatus(LOADERROR);
                        changeAdapterState(LOADERROR);
                    }
                    findingRecyclerviewAdapter.notifyDataSetChanged();
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
                                    ShetuanMsg shetuanMsg = new ShetuanMsg(jsonArray.getJSONObject(i).getString("cmName"), jsonArray.getJSONObject(i).getString("cmDetail"), jsonArray.getJSONObject(i).getString("cmBackground"), jsonArray.getJSONObject(i).getString("cmLogo"));
                                    shetuanMsg.setShetuanJsonString(jsonArray.getJSONObject(i).toString());
                                    mData.add(shetuanMsg);
                                }
                                page++;
                                pageparam = paramName1+page;
                                findingRecyclerviewAdapter.setmData(null);
                                findingRecyclerviewAdapter.setmData(mData);
                                binding.shetuanItemRecyclerView.removeAllViews();
                                binding.shetuanItemRecyclerView.setAdapter(findingRecyclerviewAdapter);
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
                    break;
                case LOAD_MORE_SHETUAN:
                    String loadmoreshetuanresult = (String) msg.obj;
                    if (loadmoreshetuanresult.length()!=0){

                    }
                    else {
                        Toast.makeText(inflater.getContext(),"",Toast.LENGTH_SHORT).show();
                    }
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

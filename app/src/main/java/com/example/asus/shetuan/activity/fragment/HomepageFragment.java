package com.example.asus.shetuan.activity.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
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
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.adapter.ActivityRecyclerviewAdapter;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.bean.Homepage;
import com.example.asus.shetuan.databinding.FragmentHomepageBinding;
import com.example.asus.shetuan.model.DateUtils;
import com.example.asus.shetuan.model.OKHttpConnect;
import com.example.asus.shetuan.model.ViewPagerJson;
import com.example.asus.shetuan.weight.MyScrollView;
import com.example.asus.shetuan.weight.VerticalSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;


public class HomepageFragment extends Fragment implements MyScrollView.OnScrollListener,VerticalSwipeRefreshLayout.OnRefreshListener,ViewPagerEx.OnPageChangeListener{

    private FragmentHomepageBinding binding = null;
//    private String[] urls = new String[]{"http://img.redocn.com/sheji/20151107/youmengxiangjiuxingdongqilaibaxianzaiweishibuwanhengbanhaibao_5270355.jpg",
//                                            "http://i2.sinaimg.cn/hs/2011/1021/U6011P667DT20111021104629.jpg",
//                                            "http://pic.58pic.com/58pic/13/02/50/57N58PICQIC.jpg",
//                                             "http://att2.citysbs.com/hangzhou/2014/05/21/22/middle_630x357-222331_13761400682211555_d78cdd4647741d487ef05ed3fbb43c35.jpg"};
    //测试，可用detailurl替代
    private String[] sliderdetailurls = new String[]{
        "https://www.baidu.com/","http://euswag.com",
        "http://baijia.baidu.com/",
        "https://www.taobao.com/"};

    private ArrayList<ActivityMsg> mData = new ArrayList<ActivityMsg>();
//    private ArrayList<ActivityMsg> changeData = new ArrayList<ActivityMsg>();

    private ActivityRecyclerviewAdapter homepageActivityAdapter;

    private JSONArray viewpagerdata;
    private OKHttpConnect okHttpConnect;
    private ViewPagerJson viewPagerJson;
    private String viewpagerurl = "https://euswag.com/eu/viewpager/show";
    private String[] imageurl;
    private String[] detailurl;
    private String[] imagetitle;
    private TextSliderView textSliderView;

    private String activityurl="https://euswag.com/eu/activity/commonav";
    private String paramName2 = "?maxtime=";
    private String loadmoreParam = paramName2+"99495884120000";
    private OKHttpConnect activityRefreshOkHttpConnect;
    private OKHttpConnect activityLoadMoreOkHttpConnect;
    private String imageloadurl = "https://euswag.com/picture/activity/";


    private final int REFRESH_COMPLETE = 0x1101;
    private final int LOAD_MORE = 0x1111;
    private final int LOADVIEWPAGER = 0x1000;
    private final int ACTIVITYREFRESH = 0x1010;
    private final int ACTIVITYLOADMORE = 0x1100;

    private final int NORMAL = 110;
    private final int LOADING = 111;
    private final int THEEND = 100;
    private final int LOADERROR =101;

    private LayoutInflater inflater;

    private int jjj;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_COMPLETE:
                    //需要更新的UI操作
                    //比如
                    //加载数据线程放到刷新中
                    new Thread(new ViewpagerRunnable()).start();

                    new Thread(new ActivityRefreshRunable()).start();
                    binding.homepageSwipeRefresh.setRefreshing(false);
                    break;
                case LOAD_MORE:
                    new Thread(new ActivityLoadMoreRunable()).start();
                    break;
                case LOADVIEWPAGER:
                    String loadviewpagerresult = (String) msg.obj;
                    int length;
                    if(loadviewpagerresult.length()!=0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(loadviewpagerresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String viewpagerdatastring;
                                viewpagerdatastring = jsonObject.getString("data");
                                viewPagerJson = new ViewPagerJson();
                                viewpagerdata = viewPagerJson.jsonParser(viewpagerdatastring);
                                length = viewpagerdata.length();
                                imageurl = new String[length];
                                detailurl = new String[length];
                                imagetitle = new String[length];
                                for (int i = 0; i < length; i++) {
                                    imageurl[i] = viewpagerdata.getJSONObject(i).getString("imgurl");
                                    detailurl[i] = viewpagerdata.getJSONObject(i).getString("detailurl");
                                    imagetitle[i] = viewpagerdata.getJSONObject(i).getString("title");
                                }
                                binding.homepageSlider.setBackgroundResource(0);
                                binding.homepageSlider.removeAllSliders();
                                for (int i = 0; i < length; i++) {
                                    textSliderView = new TextSliderView(inflater.getContext());
                                    textSliderView.description(imagetitle[i])      //图片的描述信息
                                            .setScaleType(BaseSliderView.ScaleType.Fit)
                                            .image(imageurl[i]);//图片url
                                    textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                        @Override
                                        public void onSliderClick(BaseSliderView slider) {
                                            Intent intent = new Intent();
                                            intent.setData(Uri.parse(sliderdetailurls[jjj]));
                                            //      intent.setData(Uri.parse(detailurl[jjj]));
                                            intent.setAction(Intent.ACTION_VIEW);
                                            inflater.getContext().startActivity(intent);
//                                   Toast.makeText(inflater.getContext(),"suibianba",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    binding.homepageSlider.addSlider(textSliderView);
                                }
                                //设置默认过渡效果
                                binding.homepageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                //设置默认指示器位置
                                binding.homepageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                //设置持续时间
                                binding.homepageSlider.setDuration(2000);
                            }else {
                                Toast.makeText(inflater.getContext(),"加载失败，刷新试试！",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {

                        Toast.makeText(inflater.getContext(),"网络异常",Toast.LENGTH_LONG).show();
                        //当没有网的时候，就加载一张图片作为背景放在这里
//                        binding.homepageSlider.setBackgroundResource(R.drawable.welcome);
                    }
                    break;
                case ACTIVITYREFRESH:
                    String activityrefreshresult = (String) msg.obj;
                    if(activityrefreshresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(activityrefreshresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String activityrefreshdatastring;
                                activityrefreshdatastring = jsonObject.getString("data");
                                JSONTokener activityRefreshJsonTokener = new JSONTokener(activityrefreshdatastring);
                                JSONArray activityRefreshJsonArray = null;
                                activityRefreshJsonArray = (JSONArray) activityRefreshJsonTokener.nextValue();
//                        changeData.clear();
//                        changeData.addAll(mData);
                                mData.clear();
                                for (int i =0;i<activityRefreshJsonArray.length();i++){
                                        //对于时间要进行处理，即时间格式的转换
                                    ActivityMsg activityMsg = new ActivityMsg(activityRefreshJsonArray.getJSONObject(i).getString("avTitle"),activityRefreshJsonArray.getJSONObject(i).getString("avPlace"),DateUtils.timet(activityRefreshJsonArray.getJSONObject(i).getString("avStarttime")),imageloadurl+activityRefreshJsonArray.getJSONObject(i).getString("avLogo")+".jpg");
                                    activityMsg.setActivityDetailJsonString(activityRefreshJsonArray.getJSONObject(i).toString());
                                    activityMsg.setIsparticipate("0");
                                    activityMsg.setActprice(activityRefreshJsonArray.getJSONObject(i).getDouble("avPrice"));
                                    mData.add(activityMsg);
                                }
//                        mData.addAll(changeData);
                                loadmoreParam = paramName2+DateUtils.data2(mData.get(mData.size()-1).getActtime());
                                homepageActivityAdapter.setmData(null);
                                homepageActivityAdapter.setmData(mData);
                            }
                            else {
                                Toast.makeText(inflater.getContext(),"加载失败，刷新试试！",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        Toast.makeText(inflater.getContext(),"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.homepageActivityRecyclerView.removeAllViews();
                    binding.homepageActivityRecyclerView.setAdapter(homepageActivityAdapter);
                    break;
                case ACTIVITYLOADMORE:
                    String activityloadmoreresult = (String) msg.obj;
                    if (activityloadmoreresult.length()!=0){
                        JSONObject jsonObject ;
                        int result;
                        try {
                            jsonObject = new JSONObject(activityloadmoreresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String activityloadmoredatastring;
                                activityloadmoredatastring = jsonObject.getString("data");
                                JSONTokener activityLoadMoreJsonTokener = new JSONTokener(activityloadmoredatastring);
                                JSONArray activityLoadMoreJsonArray = null;
                                activityLoadMoreJsonArray = (JSONArray) activityLoadMoreJsonTokener.nextValue();
                                if (activityLoadMoreJsonArray.length()==0){
                                    homepageActivityAdapter.setStatus(THEEND);
                                    changeAdapterState(THEEND);
                                }
                                else {
                                    for (int i = 0; i < activityLoadMoreJsonArray.length(); i++) {
                                        ActivityMsg activityMsg = new ActivityMsg(activityLoadMoreJsonArray.getJSONObject(i).getString("avTitle"), activityLoadMoreJsonArray.getJSONObject(i).getString("avPlace"), DateUtils.timet(activityLoadMoreJsonArray.getJSONObject(i).getString("avStarttime")), imageloadurl+activityLoadMoreJsonArray.getJSONObject(i).getString("avLogo")+".jpg");
                                        activityMsg.setActivityDetailJsonString(activityLoadMoreJsonArray.getJSONObject(i).toString());
                                        activityMsg.setIsparticipate("0");
                                        activityMsg.setActprice(activityLoadMoreJsonArray.getJSONObject(i).getDouble("avPrice"));
                                        mData.add(activityMsg);
                                    }
                                    loadmoreParam = paramName2+DateUtils.data2(mData.get(mData.size()-1).getActtime());
                                    homepageActivityAdapter.setStatus(NORMAL);
                                    changeAdapterState(NORMAL);
                                }
                            }
                            else {
                                Toast.makeText(inflater.getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                                homepageActivityAdapter.setStatus(LOADERROR);
                                changeAdapterState(LOADERROR);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
//                        Toast.makeText(inflater.getContext(),"网络较慢，刷新试试！",Toast.LENGTH_SHORT).show();
                        homepageActivityAdapter.setStatus(LOADERROR);
//                        homepageActivityAdapter.setStatus(THEEND);
                        changeAdapterState(LOADERROR);
                    }
                    homepageActivityAdapter.notifyDataSetChanged();
                    break;
                default:break;
            }
        }
    };
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(binding == null) {
            this.inflater = inflater;
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_homepage, container, false);
            homepageActivityAdapter = new ActivityRecyclerviewAdapter(inflater.getContext());

            onRefresh();
            binding.homepageActivityRecyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            binding.homepageScrollview.smoothScrollTo(0, 40);
            binding.setHomepage(new Homepage(inflater.getContext()));

            //图片轮询
            //网络请求放到另一个线程，其操作也应该放到线程之中
            binding.homepageSlider.setBackgroundResource(R.drawable.welcome);
            //对sliderview的监听
            //设置页面改变监听
            binding.homepageSlider.addOnPageChangeListener(this);
            binding.homepageScrollview.setOnScrollListener(this);

            //下拉刷新和scroll view有冲突
            binding.homepageSwipeRefresh.setOnRefreshListener(this);
            binding.homepageSwipeRefresh.setDistanceToTriggerSync(300);
            binding.homepageSwipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);
            //解决下拉刷新和scrollview冲突问题
            binding.homepageScrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    binding.homepageSwipeRefresh.setEnabled(binding.homepageScrollview.getScrollY()==0);
                    View childView = binding.homepageScrollview.getChildAt(0);
                    if ((childView.getMeasuredHeight() <binding.homepageScrollview.getScrollY() +binding.homepageScrollview.getHeight())&&(homepageActivityAdapter.getStatus()==NORMAL)){
                        homepageActivityAdapter.setStatus(LOADING);
                        changeAdapterState(LOADING);
                        LoadMore();
                    }
                    else {
                        if (homepageActivityAdapter.getStatus()==THEEND||homepageActivityAdapter.getStatus()==LOADERROR) {
                            homepageActivityAdapter.setStatus(NORMAL);
                        }
                    }
                }
            });
            changeAdapterState(NORMAL);
            homepageActivityAdapter.setStatus(NORMAL);
            //上拉自动刷新的recycler view

        }
        return binding.getRoot();
    }
    @Override
    public void onDestroy(){
        //fragment 销毁前调用，来停止图片轮询
        binding.homepageSlider.stopAutoCycle();
        super.onDestroy();
    }


    @Override
    public void onScroll(int scrollY) {
        if(scrollY>(binding.homepageSlider.getHeight()-binding.homepageTitleLayout.getHeight())){
            binding.homepageTitleLayout.setBackgroundResource(R.color.main_color);
            binding.homepageTitleSearchView.setBackgroundResource(R.color.search_view_color);
        }
        else {
            binding.homepageTitleLayout.setBackgroundResource(0);
            binding.homepageTitleSearchView.setBackgroundResource(0);
        }
    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(REFRESH_COMPLETE);
    }
    public void LoadMore() {
        //不延迟进行加载请求
        handler.sendEmptyMessage(LOAD_MORE);
    }
    protected void changeAdapterState(int status){
        if (homepageActivityAdapter!=null&&homepageActivityAdapter.footerHolder!=null){
            homepageActivityAdapter.footerHolder.setData(status);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        jjj=position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
//        //活动Item的响应，可以Intent携带数据跳转到下一个界面，数据好像全部都是在mData里面

    private class ViewpagerRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(viewpagerurl);
                Message message = handler.obtainMessage();
                message.what = LOADVIEWPAGER;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ActivityRefreshRunable implements Runnable{

        @Override
        public void run() {
            activityRefreshOkHttpConnect = new OKHttpConnect();
            String restultstring;
            try {
                restultstring = activityRefreshOkHttpConnect.getdata(activityurl);
                Message message = handler.obtainMessage();
                message.what = ACTIVITYREFRESH;
                message.obj = restultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ActivityLoadMoreRunable implements Runnable{

        @Override
        public void run() {
            activityLoadMoreOkHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                System.out.println("loadmoreParam  "+loadmoreParam);
                resultstring = activityLoadMoreOkHttpConnect.getdata(activityurl+loadmoreParam);
                Message message = handler.obtainMessage();
                message.what = ACTIVITYLOADMORE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

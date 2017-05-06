package com.euswag.eu.activity.shetuan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


import com.euswag.eu.R;
import com.euswag.eu.adapter.PhonelistAdapter;
import com.euswag.eu.bean.ShetuanContacts;
import com.euswag.eu.databinding.ActivityShetuanPhonelistBinding;
import com.euswag.eu.model.DateUtils;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ShetuanPhonelistActivity extends AppCompatActivity implements OnClickListener {
    private ActivityShetuanPhonelistBinding binding;
    private SharedPreferences sharedPreferences;
    private PhonelistAdapter adapter;
    private ArrayList<ShetuanContacts> mData = new ArrayList<>();
    private ArrayList<ShetuanContacts> showData = new ArrayList<>();
    private ArrayList<ShetuanContacts> midData = new ArrayList<>();
    private ArrayList<ShetuanContacts> midData2 = new ArrayList<>();

    private OKHttpConnect okHttpConnect;
    private String getcontactsurl = "/community/contacts";
    private RequestBody getcontactsbody;

    private String headimageloadurl = "https://eu-1251935523.file.myqcloud.com/user/user";

    private final int GET_CONTACTS = 100;

    private ListView popListView;
    private List<Map<String, String>> menuData1, menuData2, menuData3;
    private PopupWindow popMenu;
    private SimpleAdapter menuAdapter1, menuAdapter2, menuAdapter3;

    private String currentGrender = "所有性别", currentPlace = "全部职位", currentYear = "全部年级";
    private int currentGrenderint = -1,currentPlaceint = 4;
    private long currentYearlong;
    private int menuIndex = 0;

    private Intent getintent;
    private int phonelistlength;
    private int middatalength;
    private int middatalength2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_shetuan_phonelist);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        adapter = new PhonelistAdapter(this);
        getintent = getIntent();
        binding.shetuanPhonelistRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        getcontactsbody = new FormBody.Builder()
                .add("cmid",String.valueOf(getintent.getIntExtra("cmid",-1)))
                .add("accesstoken",sharedPreferences.getString("accesstoken",""))
                .build();
        new Thread(new GetContactsRunnable()).start();

        binding.supplierListGrender.setOnClickListener(this);
        binding.supplierListPlace.setOnClickListener(this);
        binding.supplierListYear.setOnClickListener(this);
        binding.shetuanPhonelistBackimage.setOnClickListener(this);
        initMenuData();
        initPopMenu();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.supplier_list_grender:
                popListView.setAdapter(menuAdapter1);
                popMenu.showAsDropDown(binding.supplierListGrender, 0, 2);
                menuIndex = 0;
                break;
            case R.id.supplier_list_place:
                popListView.setAdapter(menuAdapter2);
                popMenu.showAsDropDown(binding.supplierListGrender, 0, 2);
                menuIndex = 1;
                break;
            case R.id.supplier_list_year:
                popListView.setAdapter(menuAdapter3);
                popMenu.showAsDropDown(binding.supplierListGrender, 0, 2);
                menuIndex = 2;
                break;
            case R.id.shetuan_phonelist_backimage:
                ShetuanPhonelistActivity.this.onBackPressed();
                break;
            default:break;

        }
    }

    private class GetContactsRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getcontactsurl,getcontactsbody);
                Message message = handler.obtainMessage();
                message.what = GET_CONTACTS;
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
                case GET_CONTACTS:
                    String getcontactsresult = (String) msg.obj;
                    System.out.println("getcontactsresult"+getcontactsresult);
                    if (getcontactsresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getcontactsresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String getcontactsdata = jsonObject.getString("data");
                                JSONTokener jsonTokener = new JSONTokener(getcontactsdata);
                                JSONArray jsonArray = (JSONArray) jsonTokener.nextValue();
                                mData.clear();
                                phonelistlength = 0;
                                for (int i=0;i<jsonArray.length();i++){
                                    if (jsonArray.getJSONObject(i).getInt("position")>0) {
                                        ShetuanContacts shetuanContacts = new ShetuanContacts();
                                        shetuanContacts.setUid(jsonArray.getJSONObject(i).getLong("uid"));
                                        shetuanContacts.setAcademe(jsonArray.getJSONObject(i).getString("professionclass"));
                                        shetuanContacts.setAvatar(headimageloadurl+jsonArray.getJSONObject(i).getString("avatar")+".jpg");
                                        shetuanContacts.setGender(jsonArray.getJSONObject(i).getInt("gender"));
                                        shetuanContacts.setGrade(jsonArray.getJSONObject(i).getInt("grade"));
                                        shetuanContacts.setName(jsonArray.getJSONObject(i).getString("name"));
                                        shetuanContacts.setPosition(jsonArray.getJSONObject(i).getInt("position"));
                                        shetuanContacts.setStudentid(jsonArray.getJSONObject(i).getString("studentid"));
                                        mData.add(shetuanContacts);
                                        phonelistlength++;
                                    }
                                }
                                adapter.setmData(null);
                                adapter.setmData(mData);
                            }else {
                                Toast.makeText(ShetuanPhonelistActivity.this,"信息请求失败，刷新试试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ShetuanPhonelistActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    binding.shetuanPhonelistRecyclerview.removeAllViews();
                    binding.shetuanPhonelistRecyclerview.setAdapter(adapter);
                    break;
                default:break;
            }
        }
    };

    private void initMenuData() {
        menuData1 = new ArrayList<Map<String, String>>();
        String[] menuStr1 = new String[] { "所有性别", "男", "女","保密"};
        Map<String, String> map1;
        for (int i = 0, len = menuStr1.length; i < len; ++i) {
            map1 = new HashMap<String, String>();
            map1.put("name", menuStr1[i]);
            menuData1.add(map1);
        }

        menuData2 = new ArrayList<Map<String, String>>();
        String[] menuStr2 = new String[] { "全部职位", "社长","管理员","普通成员" };
        Map<String, String> map2;
        for (int i = 0, len = menuStr2.length; i < len; ++i) {
            map2 = new HashMap<String, String>();
            map2.put("name", menuStr2[i]);
            menuData2.add(map2);
        }
        System.out.println("当前年份:"+DateUtils.getCurrentYear());
        menuData3 = new ArrayList<Map<String, String>>();
        String[] menuStr3 = new String[] { "全部年级", DateUtils.getCurrentYear(), String.valueOf(Long.parseLong(DateUtils.getCurrentYear())-1),
                String.valueOf(Long.parseLong(DateUtils.getCurrentYear())-2), String.valueOf(Long.parseLong(DateUtils.getCurrentYear())-3),
                String.valueOf(Long.parseLong(DateUtils.getCurrentYear())-4)};
        Map<String, String> map3;
        for (int i = 0, len = menuStr3.length; i < len; ++i) {
            map3 = new HashMap<String, String>();
            map3.put("name", menuStr3[i]);
            menuData3.add(map3);
        }
    }
    private void initPopMenu() {
        View contentView = View.inflate(this, R.layout.popwin_supplier_list,
                null);
        popMenu = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        popMenu.setOutsideTouchable(true);
        popMenu.setBackgroundDrawable(new BitmapDrawable());
        popMenu.setFocusable(true);
        popMenu.setAnimationStyle(R.style.popwin_anim_style);

        popListView = (ListView) contentView
                .findViewById(R.id.popwin_supplier_list_lv);
        contentView.findViewById(R.id.popwin_supplier_list_bottom)
                .setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        popMenu.dismiss();
                    }
                });
        menuAdapter1 = new SimpleAdapter(this, menuData1,
                R.layout.item_listview_popwin, new String[] { "name" },
                new int[] { R.id.listview_popwind_tv });
        menuAdapter2 = new SimpleAdapter(this, menuData2,
                R.layout.item_listview_popwin, new String[] { "name" },
                new int[] { R.id.listview_popwind_tv });
        menuAdapter3 = new SimpleAdapter(this, menuData3,
                R.layout.item_listview_popwin, new String[] { "name" },
                new int[] { R.id.listview_popwind_tv });

        popListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                popMenu.dismiss();
                if (menuIndex == 0) {
                    currentGrender = menuData1.get(pos).get("name");
                    binding.supplierListGrenderTv.setText(currentGrender);
                    Toast.makeText(ShetuanPhonelistActivity.this, currentGrender, Toast.LENGTH_SHORT).show();
                    if (currentGrender.equals("所有性别")){
                        currentGrenderint = -1;
                    }else if (currentGrender.equals("男")){
                        currentGrenderint = 0;
                    }else if (currentGrender.equals("女")){
                        currentGrenderint = 1;
                    }else {
                        currentGrenderint = 2;
                    }
                    System.out.println("currentGrenderint"+currentGrenderint);
                } else if (menuIndex == 1) {
                    currentPlace = menuData2.get(pos).get("name");
                    binding.supplierListPlaceTv.setText(currentPlace);
                    if (currentPlace.equals("全部职位")){
                        currentPlaceint = 4;
                    }else if (currentPlace.equals("社长")){
                        currentPlaceint = 3;
                    }else if (currentPlace.equals("管理员")){
                        currentPlaceint = 2;
                    }else {
                        currentPlaceint = 1;
                    }
                    Toast.makeText(ShetuanPhonelistActivity.this, currentPlace, Toast.LENGTH_SHORT).show();
                } else {
                    currentYear = menuData3.get(pos).get("name");
                    binding.supplierListYearTv.setText(currentYear);
                    Toast.makeText(ShetuanPhonelistActivity.this, currentYear, Toast.LENGTH_SHORT).show();
                }
                showData.clear();
                if (currentYear.equals("全部年级")){
                    System.out.println("全部年级");
                    if (currentGrenderint == -1){
                        System.out.println("全部性别");
                        if (currentPlaceint == 4){
                            showData.addAll(mData);
                            System.out.println("000000000000");
                        }else {
                            for (int i = 0;i<phonelistlength;i++){
                                if (currentPlaceint == mData.get(i).getPosition()){
                                    showData.add(mData.get(i));
                                }
                            }
                        }
                    }else {
                        System.out.println("currentGrenderint"+currentGrenderint);
                        middatalength = 0;
                        midData.clear();
                        System.out.println("phonelistlength"+phonelistlength);
                        for (int i=0;i<phonelistlength;i++){
                            if (currentGrenderint == mData.get(i).getGender()){
                                midData.add(mData.get(i));
                                middatalength++;
                            }
                        }
                        if (currentPlaceint == 4){
                            showData.addAll(midData);
                        }else {
                            for (int i=0;i<middatalength;i++){
                                if (currentPlaceint == midData.get(i).getPosition()){
                                    showData.add(midData.get(i));
                                }
                            }
                        }
                    }
                }else {
                    currentYearlong = Long.parseLong(currentYear);
                    middatalength = 0;
                    midData.clear();
                    for (int i=0;i<phonelistlength;i++){
                        if (currentYearlong == mData.get(i).getGrade()){
                            midData.add(mData.get(i));
                            middatalength++;
                        }
                    }
                    if (currentGrenderint == -1){
                        if (currentPlaceint == 4){
                            showData.addAll(midData);
                        }else {
                            for (int i = 0;i<middatalength;i++){
                                if (currentPlaceint == midData.get(i).getPosition()){
                                    showData.add(midData.get(i));
                                }
                            }
                        }
                    }else {
                        middatalength2 = 0;
                        midData2.clear();
                        for (int i=0;i<middatalength;i++){
                            if (currentGrenderint == midData.get(i).getGender()){
                                midData2.add(midData.get(i));
                                middatalength2++;
                            }
                        }
                        if (currentPlaceint == 4){
                            showData.addAll(midData2);
                        }else {
                            for (int i=0;i<middatalength2;i++){
                                if (currentPlaceint == midData2.get(i).getPosition()){
                                    showData.add(midData2.get(i));
                                }
                            }
                        }

                    }

                }
                adapter.setmData(showData);
                binding.shetuanPhonelistRecyclerview.removeAllViews();
                binding.shetuanPhonelistRecyclerview.setAdapter(adapter);
            }
        });
    }

}

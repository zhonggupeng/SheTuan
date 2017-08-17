package com.euswag.eu.activity.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.databinding.ActivitySearch2Binding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class Search2Activity extends AppCompatActivity {
    private ActivitySearch2Binding binding;
    private SharedPreferences sharedPreferences;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String searchurl = "/common/searchnum";
    private RequestBody searchbody;

    private final int SEARCH = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search2);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        click();
    }
    private void click(){
        binding.search2Backimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search2Activity.this.onBackPressed();
            }
        });
        binding.search2Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.search2Searchcontent.getText()==null||binding.search2Searchcontent.getText().length()==0){
                    Toast.makeText(Search2Activity.this,"请输入搜索内容",Toast.LENGTH_SHORT).show();
                }else {
                    searchbody = new FormBody.Builder()
                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                            .add("keyword",binding.search2Searchcontent.getText().toString())
                            .build();
                    new Thread(new SearchRunnable()).start();
                }
            }
        });
    }
    private void click2(){
        binding.search2Activityactivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search2Activity.this,SearchActivtiyActivity.class);
                intent.putExtra("keyword",binding.search2Searchcontent.getText().toString());
                Search2Activity.this.startActivity(intent);
            }
        });
        binding.search2Shetuanshetuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search2Activity.this,SearchShetuanActivity.class);
                intent.putExtra("keyword",binding.search2Searchcontent.getText().toString());
                Search2Activity.this.startActivity(intent);
            }
        });
        binding.search2Peoplepeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Search2Activity.this,SearchPeopleActivity.class);
                intent.putExtra("keyword",binding.search2Searchcontent.getText().toString());
                Search2Activity.this.startActivity(intent);
            }
        });
    }
    private class SearchRunnable implements Runnable{

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(searchurl,searchbody);
                Message message = handler.obtainMessage();
                message.what = SEARCH;
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
                case SEARCH:
                    String searchresult = (String) msg.obj;
                    if (searchresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(searchresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String searchdata = jsonObject.getString("data");
                                JSONObject searchdatajson = new JSONObject(searchdata);
                                binding.search2Activitycount.setText(searchdatajson.getString("activityNum"));
                                binding.search2Shetuancount.setText(searchdatajson.getString("communityNum"));
                                binding.search2Peoplecount.setText(searchdatajson.getString("userNum"));
                                click2();
                            }else {
                                Toast.makeText(Search2Activity.this,"搜索失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(Search2Activity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };
}

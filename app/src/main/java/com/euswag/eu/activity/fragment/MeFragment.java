package com.euswag.eu.activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.shetuan.MyShetuanActivity;
import com.euswag.eu.activity.act.ActivityCollectionActivity;
import com.euswag.eu.activity.act.MyActivityActivity;
import com.euswag.eu.activity.person.ChangePeosonInformationActivity;
import com.euswag.eu.activity.shetuan.ShetuanCollectionActivity;
import com.euswag.eu.databinding.FragmentMeBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class MeFragment extends Fragment {

    private FragmentMeBinding binding = null;
    private LayoutInflater inflater;

    private OKHttpConnect okHttpConnect;
    private String loadpersonurl = "https://euswag.com/eu/info/introinfo";
    private RequestBody loadpersonbody;

    private String imageloadurl = "https://euswag.com/picture/user/";

    private final int LOADPERSON = 110;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding == null) {
            this.inflater = inflater;
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_me, container, false);
            SharedPreferences sharedPreferences = inflater.getContext().getSharedPreferences("token", Context.MODE_PRIVATE);
            loadpersonbody = new FormBody.Builder()
                    .add("uid", sharedPreferences.getString("phonenumber", "0"))
                    .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                    .build();
            new Thread(new LoadPersonRunnable()).start();
            click();
        }
        return binding.getRoot();
    }

    private void click() {
        binding.fragmentMeMyactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), MyActivityActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMeMynickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), ChangePeosonInformationActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMeActivityCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), ActivityCollectionActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMeShetuanCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), ShetuanCollectionActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
        binding.fragmentMeMyshetuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(inflater.getContext(), MyShetuanActivity.class);
                inflater.getContext().startActivity(intent);
            }
        });
    }

    private class LoadPersonRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(loadpersonurl, loadpersonbody);
                Message message = handler.obtainMessage();
                message.what = LOADPERSON;
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
                case LOADPERSON:
                    String loadpersonstring = (String) msg.obj;
                    if (loadpersonstring.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(loadpersonstring);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String loadpersondata;
                                loadpersondata = jsonObject.getString("data");
                                JSONObject jsonObject1 = new JSONObject(loadpersondata);
                                binding.fragmentMeNickname.setText(jsonObject1.getString("nickname"));
                                binding.fragmentMeReputation.setText("节操值 " + jsonObject1.getString("reputation"));
                                binding.fragmentMeHeadimage.setImageURI(imageloadurl + jsonObject1.getString("avatar") + ".jpg");
                            } else {
                                Toast.makeText(inflater.getContext(), "活动发起人信息加载失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(inflater.getContext(), "", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    };

}

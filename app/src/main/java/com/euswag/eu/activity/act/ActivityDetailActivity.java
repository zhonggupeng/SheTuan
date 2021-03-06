package com.euswag.eu.activity.act;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.euswag.eu.R;
import com.euswag.eu.activity.DetailsQRcodeActivity;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.databinding.ActivityActivityDetailBinding;
import com.euswag.eu.model.DateUtils;
import com.euswag.eu.model.OKHttpConnect;
import com.xys.libzxing.zxing.activity.CaptureActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ActivityDetailActivity extends AppCompatActivity {
    private ActivityActivityDetailBinding binding = null;
    private String datajsonstring;
    private ActivityMsg activityMsg = null;

    private OKHttpConnect okHttpConnect = new OKHttpConnect();
    private String getoriginatorurl = "/info/introinfo";
    private RequestBody getoriginatorbody;

    private final int GETORIGINATOR = 110;
    private final int PARTICIPATE = 101;
    private final int QUIT = 111;
    private final int COLLECTE = 100;
    private final int CANCELCOLLECTE = 121;
    private final int GETNUMBER = 131;
    private final int REGISTER_FINISH = 141;
    private final int PUSH_ACTIVITY = 151;

    private final int SET_TAGS = 1100;

    private String headimageloadurl = "https://eu-1251935523.file.myqcloud.com/user/user";
    private String activityimageloadurl = "https://eu-1251935523.file.myqcloud.com/activity/av";

    private String participateurl = "/activity/participateav";
    private String participateparam;
    private RequestBody participatebody;

    private String quiturl = "/activity/quitav";
    private RequestBody quitbody;

    private String collecteurl = "/activity/collectav";
    private RequestBody collectebody;

    private String cancelcollecteurl = "/activity/discollectav";
    private RequestBody cancelcollectebody;

    private String participatenumberurl = "/activity/memberinfolist";
    private RequestBody participatenumberbody;

    private String registerfinishurl = "/activity/participateregister";
    private RequestBody registerfinishbody;

//    private String pushactivityurl = "/push/activitytagpush";
//    private RequestBody pushactivitybody;

    //是否已经收藏该活动，
    private boolean hascollection;

    private SharedPreferences sharedPreferences;

    private PopupWindow popWindow;
    private LayoutInflater layoutInflater;
    private TextView photograph, albums;
    private LinearLayout cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_activity_detail);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        datajsonstring = intent.getStringExtra("datajson1");
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(datajsonstring);
            activityMsg = new ActivityMsg(jsonObject.getString("avTitle"), jsonObject.getString("avPlace"), DateUtils.timet(jsonObject.getString("avStarttime")), activityimageloadurl + jsonObject.getString("avLogo") + ".jpg");
            activityMsg.setActendtime(DateUtils.timet(jsonObject.getString("avEndtime")));
            activityMsg.setUid(jsonObject.getLong("uid"));
            activityMsg.setActexpectnum(jsonObject.getInt("avExpectnum"));
            activityMsg.setActprice(jsonObject.getDouble("avPrice"));
            activityMsg.setActdetail(jsonObject.getString("avDetail"));
            activityMsg.setActstate(jsonObject.getInt("avState"));
            activityMsg.setActregister(jsonObject.getInt("avRegister"));
            activityMsg.setActid(jsonObject.getInt("avid"));
            //报名截止时间
            activityMsg.setActenrolldeadline(jsonObject.getString("avEnrolldeadline"));

            binding.setActivityDetailMsg(activityMsg);

            //请求发起活动者信息
            getoriginatorbody = new FormBody.Builder()
                    .add("uid", String.valueOf(activityMsg.getUid()))
                    .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                    .build();
            new Thread(new GetOriginatorRunnable()).start();

            if (activityMsg.getActregister() == -1) {
                binding.activityDetailIsregister.setText("无需签到");
                participateparam = "2";
            } else {
                binding.activityDetailIsregister.setText("需要签到");
                participateparam = "0";
            }

            if (activityMsg.getActprice() == 0) {
                binding.activityDetailIsfree.setText("免费");
            } else {
                binding.activityDetailIsfree.setText(String.valueOf(activityMsg.getActprice()));
            }
            binding.activityDetailActivitytime.setText(activityMsg.getActtime() + "~" + activityMsg.getActendtime());
            //需要知道已报名人数
            //请求已报名人数
            participatenumberbody = new FormBody.Builder()
                    .add("avid", String.valueOf(activityMsg.getActid()))
                    .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                    .add("choice", "0")
                    .build();
            new Thread(new ParticipateNumberRunnable()).start();

            //设置参加按钮
            //通过验证来确定
            //请求是否已经参加该活动
            if (intent.getStringExtra("isparticipate").equals("0")) {
                if (activityMsg.getActregister() > 0) {
                    binding.activityDetailIsenroll.setText("活动已开始签到");
                } else {
                    binding.activityDetailIsenroll.setText("我要参加");
                }
            } else if (intent.getStringExtra("isparticipate").equals("4")) {
                binding.activityDetailIsenroll.setText("取消收藏");
            } else {
                if (activityMsg.getActregister() > 0) {
                    binding.activityDetailIsenroll.setText("我要签到");
                } else {
                    binding.activityDetailIsenroll.setText("退出活动");
                }
            }
            System.out.println("报名截止时间：" + jsonObject.getString("avEnrolldeadline"));
            System.out.println("当前时间" + DateUtils.data(DateUtils.getCurrentTime()));
            if (Long.parseLong(DateUtils.data(DateUtils.getCurrentTime())) >= Long.parseLong(jsonObject.getString("avEnrolldeadline"))) {
                binding.activityDetailIsenroll.setText("活动报名已截止");
            }
            binding.activityDetailBackground.setImageURI(activityMsg.getImageurl());
            hascollection = false;
            click();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void click() {
        binding.activitydetailBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityDetailActivity.this.onBackPressed();
            }
        });
        binding.activityDetailCallphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.activityDetailOriginator == null || binding.activityDetailOriginator.length() == 0) {
                    Toast.makeText(ActivityDetailActivity.this, "数据未加载成功，你不能进行此操作", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + activityMsg.getUid()));
                    if (ActivityCompat.checkSelfPermission(ActivityDetailActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    ActivityDetailActivity.this.startActivity(intent);
                }
            }
        });
        //参加活动
        if (binding.activityDetailIsenroll.getText().equals("我要参加")) {
            binding.activityDetailIsenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    participatebody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("avid", String.valueOf(activityMsg.getActid()))
                            .add("verifystate", participateparam)
                            .build();
                    new Thread(new ParticipateRunnable()).start();
                }
            });
        } else if (binding.activityDetailIsenroll.getText().equals("取消收藏")) {
            binding.activityDetailIsenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelcollectebody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("avid", String.valueOf(activityMsg.getActid()))
                            .build();
                    new Thread(new CancelCollecteRunnable()).start();
                }
            });
        } else if (binding.activityDetailIsenroll.getText().equals("我要签到")) {
            binding.activityDetailIsenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupWindow(binding.activityDetailIsenroll);
                }
            });
        } else if (binding.activityDetailIsenroll.getText().equals("退出活动")) {
            binding.activityDetailIsenroll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quitbody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("avid", String.valueOf(activityMsg.getActid()))
                            .build();
                    new Thread(new QuitRunnable()).start();
                }
            });
        }
        binding.activityDetailCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hascollection) {
                    cancelcollectebody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("avid", String.valueOf(activityMsg.getActid()))
                            .build();
                    new Thread(new CancelCollecteRunnable()).start();
                } else {
                    collectebody = new FormBody.Builder()
                            .add("uid", sharedPreferences.getString("phonenumber", "0"))
                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                            .add("avid", String.valueOf(activityMsg.getActid()))
                            .build();
                    new Thread(new CollecteRunnable()).start();
                }
            }
        });
        binding.activityDetailQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDetailActivity.this, DetailsQRcodeActivity.class);
                intent.putExtra("title", activityMsg.getActtitle());
                intent.putExtra("id", activityMsg.getActid());
                intent.putExtra("type", "act");
                ActivityDetailActivity.this.startActivity(intent);
            }
        });
    }

    private void click2(final String string) {
        binding.activityDetailPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityDetailActivity.this, ParticipateMemberActivity.class);
                intent.putExtra("memberjson", string);
                ActivityDetailActivity.this.startActivity(intent);
            }
        });
    }

    private class GetOriginatorRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(getoriginatorurl, getoriginatorbody);
                Message message = handler.obtainMessage();
                message.what = GETORIGINATOR;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ParticipateRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(participateurl, participatebody);
                Message message = handler.obtainMessage();
                message.what = PARTICIPATE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class QuitRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(quiturl, quitbody);
                Message message = handler.obtainMessage();
                message.what = QUIT;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class CollecteRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(collecteurl, collectebody);
                Message message = handler.obtainMessage();
                message.what = COLLECTE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class CancelCollecteRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(cancelcollecteurl, cancelcollectebody);
                Message message = handler.obtainMessage();
                message.what = CANCELCOLLECTE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ParticipateNumberRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(participatenumberurl, participatenumberbody);
                Message message = handler.obtainMessage();
                message.what = GETNUMBER;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class RegisterFinishRunnable implements Runnable {

        @Override
        public void run() {
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(registerfinishurl, registerfinishbody);
                Message message = handler.obtainMessage();
                message.what = REGISTER_FINISH;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private class PushActivityRunnable implements Runnable {
//
//        @Override
//        public void run() {
//            String resultstring;
//            try {
//                resultstring = okHttpConnect.postdata(pushactivityurl, pushactivitybody);
//                Message message = handler.obtainMessage();
//                message.what = PUSH_ACTIVITY;
//                message.obj = resultstring;
//                handler.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

//    //TODO
//    private final TagAliasCallback tagAliasCallback = new TagAliasCallback() {
//        @Override
//        public void gotResult(int i, String s, Set<String> set) {
//            switch (i){
//                case 0:
//                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
////                    pushactivitybody = new FormBody.Builder()
////                            .add("alert", "您成功参加" + activityMsg.getActtitle())
////                            .add("avid", String.valueOf(activityMsg.getActid()))
////                            .add("avname", activityMsg.getActtitle())
////                            .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
////                            .build();
////                    new Thread(new PushActivityRunnable()).start();
//                    break;
//                case 6002:
//                    handler.sendMessageDelayed(handler.obtainMessage(SET_TAGS, "av" + activityMsg.getActid()),1000*60);
//                    break;
//                default:break;
//            }
//        }
//    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETORIGINATOR:
                    String getoringinatorresult = (String) msg.obj;
                    if (getoringinatorresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getoringinatorresult);
                            result = jsonObject.getInt("status");
                            System.out.println("result:" + result);
                            if (result == 200) {
                                String oringingationdata;
                                oringingationdata = jsonObject.getString("data");
                                JSONObject jsonObject1 = new JSONObject(oringingationdata);
                                binding.activityDetailOriginator.setText(jsonObject1.getString("name"));
                                binding.activityDetailReputation.setText("节操值 " + jsonObject1.getString("reputation"));
                                binding.activityDetailHeadimage.setImageURI(headimageloadurl + jsonObject1.getString("avatar") + ".jpg");
                            } else {
                                Toast.makeText(ActivityDetailActivity.this, "活动发起人信息加载失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PARTICIPATE:
                    String participateresult = (String) msg.obj;
                    if (participateresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(participateresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(ActivityDetailActivity.this, "参加成功", Toast.LENGTH_SHORT).show();
//                                handler.sendMessage(handler.obtainMessage(SET_TAGS, "av" + activityMsg.getActid()));
                            } else if (result == 500) {
                                Toast.makeText(ActivityDetailActivity.this, "你已参加了该活动，不要重复参加", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityDetailActivity.this, "活动未参加成功", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case QUIT:
                    String quitresult = (String) msg.obj;
                    if (quitresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(quitresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(ActivityDetailActivity.this, "你已经退出该活动", Toast.LENGTH_SHORT).show();
                                ActivityDetailActivity.this.finish();
                            } else {
                                Toast.makeText(ActivityDetailActivity.this, "退出失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case COLLECTE:
                    String collecteresult = (String) msg.obj;
                    if (collecteresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(collecteresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(ActivityDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                                binding.activityDetailCollection.setImageResource(R.drawable.ic_collection_after);
                                hascollection = true;
                            } else {
                                Toast.makeText(ActivityDetailActivity.this, "收藏失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CANCELCOLLECTE:
                    String cancelcollecteresult = (String) msg.obj;
                    if (cancelcollecteresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(cancelcollecteresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                if (binding.activityDetailIsenroll.getText().equals("取消收藏")) {
                                    ActivityDetailActivity.this.finish();
                                } else {
                                    Toast.makeText(ActivityDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                                    binding.activityDetailCollection.setImageResource(R.drawable.ic_collection_before);
                                }
                            } else {
                                Toast.makeText(ActivityDetailActivity.this, "取消失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case GETNUMBER:
                    String getnumberresult = (String) msg.obj;
                    if (getnumberresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(getnumberresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String getnumberdata = jsonObject.getString("data");
                                int number;
                                if (getnumberdata.equals("null")) {
                                    number = 0;
                                } else {
                                    JSONTokener memberjsonTokener = new JSONTokener(getnumberdata);
                                    JSONArray memberjsonArray = (JSONArray) memberjsonTokener.nextValue();
                                    number = memberjsonArray.length();
                                }
                                if (activityMsg.getActexpectnum() == 0) {
                                    binding.activityDetailPeople.setText("已报名" + number + "人/不限");
                                } else {
                                    binding.activityDetailPeople.setText("已报名" + number + "人/限" + activityMsg.getActexpectnum() + "人");
                                }
                                click2(getnumberdata);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REGISTER_FINISH:
                    String registerfinishresult = (String) msg.obj;
                    if (registerfinishresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(registerfinishresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(ActivityDetailActivity.this, "签到成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityDetailActivity.this, "签到失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ActivityDetailActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
//                case SET_TAGS:
//                    //TODO
//                    String tags = (String) msg.obj;
//                    Set<String> set = new HashSet<>();
//                    set.add(tags);
//                    JPushInterface.setTags(getApplicationContext(), set , tagAliasCallback);
//                    break;
//                case PUSH_ACTIVITY:
//                    String pushactivityresult = (String) msg.obj;
//                    System.out.println("推送返回" + pushactivityresult);
//                    break;
                default:
                    break;
            }
        }
    };

    private void showPopupWindow(View parent) {
        if (popWindow == null) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pop_select_photo, null);
            popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            initPop(view);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void initPop(View view) {
        photograph = (TextView) view.findViewById(R.id.photograph);
        photograph.setText("输入签到码签到");
        albums = (TextView) view.findViewById(R.id.albums);
        albums.setText("扫描二维码签到");
        cancel = (LinearLayout) view.findViewById(R.id.cancel);
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                Intent intent = new Intent(ActivityDetailActivity.this, InputRegisterCodeActivity.class);
                intent.putExtra("register", activityMsg.getActregister());
                intent.putExtra("actid", activityMsg.getActid());
                ActivityDetailActivity.this.startActivity(intent);
            }
        });
        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                Intent intent = new Intent(ActivityDetailActivity.this, CaptureActivity.class);
                ActivityDetailActivity.this.startActivityForResult(intent, 0);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            String resultstring = bundle.getString("result");
            if (resultstring.indexOf("www.euswag.com?") == 0) {
                String[] resultarray = resultstring.split("\\?|=|&");
                if (resultarray.length == 5) {
                    if (resultarray[2].equals(String.valueOf(activityMsg.getActid())) && resultarray[4].equals(String.valueOf(activityMsg.getActregister()))) {
                        registerfinishbody = new FormBody.Builder()
                                .add("uid", sharedPreferences.getString("phonenumber", "0"))
                                .add("accesstoken", sharedPreferences.getString("accesstoken", "00"))
                                .add("avid", String.valueOf(activityMsg.getActid()))
                                .build();
                        new Thread(new RegisterFinishRunnable()).start();
                    } else {
                        Toast.makeText(this, "扫描的二维码不是本活动的签到码", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "扫描的二维码不是签到码", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "扫描的二维码不是签到码", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        if (popWindow != null && popWindow.isShowing()) {
//            System.out.println("pop显示状态"+popWindow.isShowing());
//            popWindow.dismiss();
//        } else {
//            super.onBackPressed();
//        }
//    }
}

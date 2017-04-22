package com.example.asus.shetuan.activity.funct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
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
import android.widget.ViewSwitcher;

import com.android.debug.hv.ViewServer;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.ChangePeosonInformationActivity;
import com.example.asus.shetuan.bean.PressActivity;
import com.example.asus.shetuan.clipimage.ClipActivity;
import com.example.asus.shetuan.databinding.ActivityPressActivityBinding;
import com.example.asus.shetuan.dateselector.DataSelect;
import com.example.asus.shetuan.model.DateUtils;
import com.example.asus.shetuan.model.NetWorkState;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class PressActivityActivity extends AppCompatActivity {

    private PopupWindow popWindow;
    private LayoutInflater layoutInflater;
    private TextView photograph, albums;
    private LinearLayout cancel;
    public static final int PHOTOZOOM = 0;
    public static final int PHOTOTAKE = 1;
    public static final int IMAGE_COMPLETE = 2;
    private String photoSavePath;
    private String photoSaveName;
    private String path;
    private String imagepath = null;

    private LayoutInflater inflater = null;
    private DataSelect dataSelect = null;
    private final int STARTTIMEID = 10;
    private final int ENDTIMEID = 11;
    private final int ENROLLDEADTIME = 110;

    private final int PRESSACTIVITY = 0x1000;
    private final int PRESSIMAGE = 0x1100;

    private OKHttpConnect okHttpConnect;
    private String creaturl = "https://euswag.com/eu/activity/createav";
    private RequestBody creatbody;

    private String pressimageurl = "https://euswag.com/eu/upload/activity";

    private SharedPreferences sharedPreferences;

    private PressActivity pressActivity;

    public ActivityPressActivityBinding getBinding() {
        return binding;
    }

    private ActivityPressActivityBinding binding = null;
    private File imagefile;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_press_activity);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        pressActivity = new PressActivity(this);
        binding.setPressactivity(pressActivity);
        dataSelect = new DataSelect(this);

        File file = new File(Environment.getExternalStorageDirectory(), "SheTuan/cache");
        if (!file.exists()) {
            Toast.makeText(PressActivityActivity.this, "无法使用存储器，该功能无法正常使用", Toast.LENGTH_LONG).show();
            file.mkdirs();
        }
        photoSavePath = Environment.getExternalStorageDirectory().getPath() + "/SheTuan/cache/";
        System.out.println(photoSavePath);
        photoSaveName = System.currentTimeMillis() + ".jpeg";
        click();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void click(){
        binding.pressActivitySelectStarttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSelect.showDateAndTime(STARTTIMEID);
            }
        });
        binding.pressActivitySelectEndtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSelect.showDateAndTime(ENDTIMEID);
            }
        });
        binding.pressActivitySelectEnrolldeadtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataSelect.showDateAndTime(ENROLLDEADTIME);
            }
        });
//        binding.pressActivityIsregister.setOnCheckedChangeListener(new );
        binding.pressActivityFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(PressActivityActivity.this).setTitle("提示").setMessage("您确定发起这个活动吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("switchbutton状态" + binding.pressActivityIsregister.isChecked());
                        if (pressActivity.getTitle() == null || pressActivity.getTitle().length() == 0) {
                            Toast.makeText(PressActivityActivity.this, "请填写活动标题", Toast.LENGTH_SHORT).show();
                        } else if (pressActivity.getStarttime() == null || pressActivity.getStarttime().length() == 0) {
                            Toast.makeText(PressActivityActivity.this, "请选择活动开始时间", Toast.LENGTH_SHORT).show();
                        } else if (pressActivity.getEndtime() == null || pressActivity.getEndtime().length() == 0) {
                            Toast.makeText(PressActivityActivity.this, "请选择活动结束时间", Toast.LENGTH_SHORT).show();
                        }else if (pressActivity.getEnrolldeadline()==null||pressActivity.getEnrolldeadline().length()==0) {
                            Toast.makeText(PressActivityActivity.this,"请选择活动报名截止时间",Toast.LENGTH_SHORT).show();
                        }else if (pressActivity.getPrice() == null || pressActivity.getPrice().length() == 0) {
                            Toast.makeText(PressActivityActivity.this, "请填写活动费用", Toast.LENGTH_SHORT).show();
                        } else if (pressActivity.getPlace() == null || pressActivity.getPlace().length() == 0) {
                            Toast.makeText(PressActivityActivity.this, "请填写活动地址", Toast.LENGTH_SHORT).show();
                        }else if (Long.parseLong(DateUtils.data(pressActivity.getStarttime()))<Long.parseLong(DateUtils.data(DateUtils.getCurrentTime()))){
                            Toast.makeText(PressActivityActivity.this,"活动开始时间早于当前时间，请重新选择",Toast.LENGTH_SHORT).show();
                        }else if (Long.parseLong(DateUtils.data(pressActivity.getStarttime()))>Long.parseLong(DateUtils.data(pressActivity.getEndtime()))){
                            Toast.makeText(PressActivityActivity.this,"活动开始时间晚于活动结束时间，请重新选择",Toast.LENGTH_SHORT).show();
                        }else if (Long.parseLong(DateUtils.data(pressActivity.getEnrolldeadline()))>Long.parseLong(DateUtils.data(pressActivity.getEndtime()))){
                            Toast.makeText(PressActivityActivity.this,"活动报名截止时间晚于活动结束时间，请重新选择",Toast.LENGTH_SHORT).show();
                        }else if (Long.parseLong(DateUtils.data(pressActivity.getEnrolldeadline()))<Long.parseLong(DateUtils.data(DateUtils.getCurrentTime()))){
                            Toast.makeText(PressActivityActivity.this,"活动报名截止时间早于当前时间，，请重新选择",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if (binding.pressActivityIsregister.isChecked()) {
                                pressActivity.setRegister("0");
                            } else {
                                pressActivity.setRegister("-1");
                            }
                            if (imagepath == null) {
                                creatbody = new FormBody.Builder()
                                        .add("accesstoken",sharedPreferences.getString("accesstoken", ""))
                                        .add("avDetail",pressActivity.getDetail())
                                        .add("avExpectnum",pressActivity.getExpectnum())
                                        .add("avPlace",pressActivity.getPlace())
                                        .add("avPrice",pressActivity.getPrice())
                                        .add("avRegister",pressActivity.getRegister())
                                        .add("avTitle",pressActivity.getTitle())
                                        .add("avendtime",DateUtils.data(pressActivity.getEndtime()))
                                        .add("avenrolldeadline",DateUtils.data(pressActivity.getEnrolldeadline()))
                                        .add("avstarttime",DateUtils.data(pressActivity.getStarttime()))
                                        .add("uid",sharedPreferences.getString("phonenumber", "0"))
                                        .build();
                                if (NetWorkState.checkNetWorkState(PressActivityActivity.this)) {
                                    new Thread(new PressActivityRunable()).start();
                                }
                            }else {
                                imagefile = new File(imagepath);
                                if (NetWorkState.checkNetWorkState(PressActivityActivity.this)) {
                                    new Thread(new PressImageRunnable()).start();
                                }
                            }
                        }
                    }
                }).setNegativeButton("取消", null).show();
            }
        });
        binding.pressActivityActivityimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(binding.pressActivityActivityimage);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }else {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("您确定放弃发布活动吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    backPressed();
                }
            }).setNegativeButton("取消", null).show();
        }
    }

    public void backPressed() {
        super.onBackPressed();
    }


    private class PressActivityRunable implements Runnable {
        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(creaturl,creatbody);
                Message message = handler.obtainMessage();
                message.what = PRESSACTIVITY;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class PressImageRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postfile(pressimageurl,imagefile);
                Message message = handler.obtainMessage();
                message.what = PRESSIMAGE;
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
                case PRESSACTIVITY:
                    String pressactivityresult = (String) msg.obj;
                    System.out.println("pressactivityresult"+pressactivityresult);
                    if (pressactivityresult.length()!=0){
                        JSONObject jsonObject;
                        int result ;
                        try {
                            jsonObject = new JSONObject(pressactivityresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(PressActivityActivity.this, "活动发布成功", Toast.LENGTH_LONG).show();
                            } else if (result == 400) {
                                Toast.makeText(PressActivityActivity.this, "活动发布失败", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(PressActivityActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                            PressActivityActivity.this.finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(PressActivityActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case PRESSIMAGE:
                    String pressimageresult = (String) msg.obj;
                    if (pressimageresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(pressimageresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String pressimagedata = jsonObject.getString("data");
                                creatbody = new FormBody.Builder()
                                        .add("accesstoken",sharedPreferences.getString("accesstoken", ""))
                                        .add("avLogo",pressimagedata.substring(0,pressimagedata.indexOf(".")))
                                        .add("avDetail",pressActivity.getDetail())
                                        .add("avExpectnum",pressActivity.getExpectnum())
                                        .add("avPlace",pressActivity.getPlace())
                                        .add("avPrice",pressActivity.getPrice())
                                        .add("avRegister",pressActivity.getRegister())
                                        .add("avTitle",pressActivity.getTitle())
                                        .add("avendtime",DateUtils.data(pressActivity.getEndtime()))
                                        .add("avenrolldeadline",DateUtils.data(pressActivity.getEnrolldeadline()))
                                        .add("avstarttime",DateUtils.data(pressActivity.getStarttime()))
                                        .add("uid",sharedPreferences.getString("phonenumber", "0"))
                                        .build();
                                if (NetWorkState.checkNetWorkState(PressActivityActivity.this)) {
                                    new Thread(new PressActivityRunable()).start();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(PressActivityActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
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
        albums = (TextView) view.findViewById(R.id.albums);
        cancel = (LinearLayout) view.findViewById(R.id.cancel);
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                photoSaveName = String.valueOf(System.currentTimeMillis()) + ".jpeg";
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(photoSavePath, photoSaveName));
                openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, PHOTOTAKE);
            }
        });
        albums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(openAlbumIntent, PHOTOZOOM);
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
        if (resultCode != RESULT_OK) {
            return;
        }
        Uri uri = null;
        switch (requestCode) {
            case PHOTOZOOM:
                if (data == null) {
                    return;
                }
                uri = data.getData();
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);//获得或者选择图片
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);//得到一个图片
                Intent intent3 = new Intent(this, ClipActivity.class);
                intent3.putExtra("path", path);
                startActivityForResult(intent3, IMAGE_COMPLETE);
                break;
            case PHOTOTAKE:
                path = photoSavePath + photoSaveName;
                uri = Uri.fromFile(new File(path));
                Intent intent2 = new Intent(this, ClipActivity.class);
                intent2.putExtra("path", path);
                startActivityForResult(intent2, IMAGE_COMPLETE);
                break;
            case IMAGE_COMPLETE:
                final String temppath = data.getStringExtra("path");
                String string = "file://";
                System.out.println(temppath);
                imagepath = temppath;
                binding.pressActivityActivityimage.setImageURI(new String(string + temppath));//将图片置入image
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

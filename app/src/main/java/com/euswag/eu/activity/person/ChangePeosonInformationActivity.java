package com.euswag.eu.activity.person;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import com.euswag.eu.bean.PeosonInformation;
import com.euswag.eu.clipimage.ClipActivity;
import com.euswag.eu.databinding.ActivityChangePeosonInformationBinding;
import com.euswag.eu.model.NetWorkState;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class ChangePeosonInformationActivity extends AppCompatActivity {

    private PopupWindow popWindow;
    private LayoutInflater layoutInflater;
    private TextView photograph,albums;
    private LinearLayout cancel;
    public static final int PHOTOZOOM = 0;
    public static final int PHOTOTAKE = 1;
    public static final int IMAGE_COMPLETE = 2;
    private String photoSavePath;
    private String photoSaveName;
    private String path;
    private String headimagepath=null;
    private File headimagefile;

    private ActivityChangePeosonInformationBinding binding;
    private SharedPreferences sharedPreferences;
    private PeosonInformation peosonInformation;

    private OKHttpConnect okHttpConnect;
    private String requestpeoinformationurl = "https://euswag.com/eu/info/introinfo";
    private String requestpeoinformationparam1;
    private String requestpeoinformationparam2;

    private String changeinformationurl = "https://euswag.com/eu/info/changeinfo";
    private String changeinformationparamstring;

    private String headimageloadurl = "https://euswag.com/picture/user/";

    private String postheadimageurl = "https://euswag.com/eu/upload/user";

    private final int REQUEST_PEOPLE = 110;
    private final int POST_HEADIMAGE = 111;
    private final int CHANGE_INFORMATION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_peoson_information);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        peosonInformation = new PeosonInformation();
        requestpeoinformationparam1 = "?uid="+sharedPreferences.getString("phonenumber", "0");
        requestpeoinformationparam2 = "&accesstoken=" + sharedPreferences.getString("accesstoken", "00");
        if (NetWorkState.checkNetWorkState(ChangePeosonInformationActivity.this)) {
            new Thread(new RequestPeopleRunnable()).start();
        }
        File file = new File(Environment.getExternalStorageDirectory(),"SheTuan/cache");
        if (!file.exists()) {
            Toast.makeText(this,"无法使用存储器，修改头像无法正常使用",Toast.LENGTH_LONG).show();
            file.mkdirs();
        }
        photoSavePath=Environment.getExternalStorageDirectory().getPath()+"/SheTuan/cache/";
        System.out.println(photoSavePath);
        photoSaveName =System.currentTimeMillis()+ ".jpeg";
        click1();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    private void click1(){
        binding.changeInformBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePeosonInformationActivity.this.onBackPressed();
            }
        });
    }
    private void click2(){
        binding.changeInformChangeHeadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(binding.changeInformHeadimage);
            }
        });
        binding.changeInformConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (headimagepath!=null){
                    headimagefile = new File(headimagepath);
                    if (NetWorkState.checkNetWorkState(ChangePeosonInformationActivity.this)) {
                        new Thread(new PostHeadimageRunnable()).start();
                    }
                }else {
                    changeinformationparamstring = "?uid="+peosonInformation.getUid()+"&nickname="+peosonInformation.getNickname()
                            +"&userdescription="+peosonInformation.getPersonalexplaintion()+"&accesstoken=" + sharedPreferences.getString("accesstoken", "00");;
                    if (NetWorkState.checkNetWorkState(ChangePeosonInformationActivity.this)) {
                        new Thread(new ChangeInformationRunnable()).start();
                    }
                }
            }
        });
    }
    private class RequestPeopleRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(requestpeoinformationurl+requestpeoinformationparam1+requestpeoinformationparam2);
                Message message = handler.obtainMessage();
                message.what = REQUEST_PEOPLE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class PostHeadimageRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postfile(postheadimageurl,headimagefile);
                Message message = handler.obtainMessage();
                message.what = POST_HEADIMAGE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private class ChangeInformationRunnable implements Runnable{

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.getdata(changeinformationurl+changeinformationparamstring);
                Message message = handler.obtainMessage();
                message.what = CHANGE_INFORMATION;
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
                case REQUEST_PEOPLE:
                    String requestpeopleresult = (String) msg.obj;
                    if (requestpeopleresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(requestpeopleresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String requestpeopledata = jsonObject.getString("data");
                                JSONObject jsonObject1 = new JSONObject(requestpeopledata);
                                peosonInformation.setUid(Long.parseLong(sharedPreferences.getString("phonenumber", "0")));
                                System.out.println("uid:"+peosonInformation.getUid());
                                peosonInformation.setHeadimage(headimageloadurl+jsonObject1.getString("avatar")+".jpg");
                                peosonInformation.setNickname(jsonObject1.getString("nickname"));
                                peosonInformation.setAcademe(jsonObject1.getString("professionclass"));
                                if (jsonObject1.getInt("gender")==0) {
                                    peosonInformation.setGender("男");
                                }else if (jsonObject1.getInt("gender")==1){
                                    peosonInformation.setGender("女");
                                }else {
                                    peosonInformation.setGender("保密");
                                }
                                peosonInformation.setName(jsonObject1.getString("name"));
                                peosonInformation.setStudentid(jsonObject1.getString("studentid"));
                                peosonInformation.setPersonalexplaintion(jsonObject1.getString("userdescription"));
                                binding.setPeosonInformation(peosonInformation);
                                binding.changeInformHeadimage.setImageURI(peosonInformation.getHeadimage());
                                click2();
                            }else {
                                Toast.makeText(ChangePeosonInformationActivity.this,"个人信息加载失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ChangePeosonInformationActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case POST_HEADIMAGE:
                    String postheadimageresult = (String) msg.obj;
                    if (postheadimageresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(postheadimageresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                String postheadimagedata = jsonObject.getString("data");
                                changeinformationparamstring = "?uid="+peosonInformation.getUid()+"&avatar="+postheadimagedata.substring(0,postheadimagedata.indexOf("."))
                                                            +"&nickname="+peosonInformation.getNickname()+"&userdescription="+peosonInformation.getPersonalexplaintion()
                                                            +"&accesstoken=" + sharedPreferences.getString("accesstoken", "00");;
                                if (NetWorkState.checkNetWorkState(ChangePeosonInformationActivity.this)) {
                                    new Thread(new ChangeInformationRunnable()).start();
                                }
                            }else {
                                Toast.makeText(ChangePeosonInformationActivity.this,"头像上传失败",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ChangePeosonInformationActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CHANGE_INFORMATION:
                    String changeinformtionresult = (String) msg.obj;
                    if (changeinformtionresult.length()!=0){
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(changeinformtionresult);
                            result = jsonObject.getInt("status");
                            if (result == 200){
                                ChangePeosonInformationActivity.this.finish();
                            }else {
                                Toast.makeText(ChangePeosonInformationActivity.this,"修改信息失败，请重试",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(ChangePeosonInformationActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:break;
            }
        }
    };

    private void showPopupWindow(View parent){
        if (popWindow == null) {
            layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.pop_select_photo,null);
            popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT,true);
            initPop(view);
        }
        popWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        popWindow.setFocusable(true);
        popWindow.setOutsideTouchable(true);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }

    public void initPop(View view){
        photograph = (TextView) view.findViewById(R.id.photograph);
        albums = (TextView) view.findViewById(R.id.albums);
        cancel= (LinearLayout) view.findViewById(R.id.cancel);
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                popWindow.dismiss();
                photoSaveName =String.valueOf(System.currentTimeMillis()) + ".jpeg";
                Uri imageUri = null;
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(photoSavePath,photoSaveName));
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
                if (data==null) {
                    return;
                }
                uri = data.getData();
                System.out.println("相册回调uri"+uri.toString());
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri, proj, null, null,null);//获得或者选择图片
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);//得到一个图片
                System.out.println("图片路径："+path);
                Intent intent3=new Intent(this, ClipActivity.class);
                intent3.putExtra("path", path);
                startActivityForResult(intent3, IMAGE_COMPLETE);
                break;
            case PHOTOTAKE:
                path=photoSavePath+photoSaveName;
                uri = Uri.fromFile(new File(path));
                Intent intent2=new Intent(this, ClipActivity.class);
                intent2.putExtra("path", path);
                startActivityForResult(intent2, IMAGE_COMPLETE);
                break;
            case IMAGE_COMPLETE:
                final String temppath = data.getStringExtra("path");
                String string = "file://";
                System.out.println(temppath);
                headimagepath = temppath;
                binding.changeInformHeadimage.setImageURI(new String(string+temppath));
//                imageView.setImageURI();//将图片置入image
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onBackPressed() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }else{
            super.onBackPressed();
        }
    }
}

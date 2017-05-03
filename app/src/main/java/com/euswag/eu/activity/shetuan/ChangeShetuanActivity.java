package com.euswag.eu.activity.shetuan;

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
import com.euswag.eu.bean.ChangeShetuan;
import com.euswag.eu.clipimage.ClipActivity;
import com.euswag.eu.databinding.ActivityChangeShetuanBinding;
import com.euswag.eu.model.OKHttpConnect;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ChangeShetuanActivity extends AppCompatActivity {

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

    private ActivityChangeShetuanBinding binding;
    private SharedPreferences sharedPreferences;
    private ChangeShetuan changeShetuan;
    private Intent getintent;

    private OKHttpConnect okHttpConnect;
    private String changeshetuanurl = "https://euswag.com/eu/community/changecm";
    private RequestBody changeshetuanbody;

    private String postlogourl = "https://euswag.com/eu/upload/community/logo";
    private File logofile;

    private final int CHANGE = 110;
    private final int POST_LOGO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_shetuan);
        sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);
        getintent = getIntent();
        changeShetuan = new ChangeShetuan(this);
        changeShetuan.setStannouncement(getintent.getStringExtra("cmAnnouncement"));
        changeShetuan.setStintroduction(getintent.getStringExtra("cmDetail"));
        binding.setChangeShetuan(changeShetuan);
        binding.changeShetuanLogo.setImageURI(getintent.getStringExtra("cmLogo"));
        click();
        File file = new File(Environment.getExternalStorageDirectory(), "SheTuan/cache");
        if (!file.exists()) {
            Toast.makeText(ChangeShetuanActivity.this, "无法使用存储器，该功能无法正常使用", Toast.LENGTH_LONG).show();
            file.mkdirs();
        }
        photoSavePath = Environment.getExternalStorageDirectory().getPath() + "/SheTuan/cache/";
        System.out.println(photoSavePath);
        photoSaveName = System.currentTimeMillis() + ".jpeg";
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void click() {
        binding.changeShetuanLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(binding.changeShetuanLogo);
            }
        });
        binding.changeShetuanConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (changeShetuan.getStannouncement() == null || changeShetuan.getStannouncement().length() == 0) {
                    Toast.makeText(ChangeShetuanActivity.this, "请填写社团通知", Toast.LENGTH_SHORT).show();
                } else if (changeShetuan.getStintroduction() == null || changeShetuan.getStintroduction().length() == 0) {
                    Toast.makeText(ChangeShetuanActivity.this, "请填写社团简介", Toast.LENGTH_SHORT).show();
                } else {
                    if (imagepath == null) {
                        changeshetuanbody = new FormBody.Builder()
                                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                .add("cmid", String.valueOf(getintent.getIntExtra("cmid", 0)))
                                .add("cmAnnouncement", changeShetuan.getStannouncement())
                                .add("cmDetail", changeShetuan.getStintroduction())
                                .build();
                        new Thread(new ChangeShetuanRunnable()).start();
                    } else {
                        logofile = new File(imagepath);

                        new Thread(new PostLogoRunnable()).start();

                    }
                }
            }
        });
    }

    private class ChangeShetuanRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(changeshetuanurl, changeshetuanbody);
                Message message = handler.obtainMessage();
                message.what = CHANGE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class PostLogoRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postfile(postlogourl, logofile);
                Message message = handler.obtainMessage();
                message.what = POST_LOGO;
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
                case CHANGE:
                    String changeresult = (String) msg.obj;
                    if (changeresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(changeresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(ChangeShetuanActivity.this, "信息修改成功", Toast.LENGTH_SHORT).show();
                                ChangeShetuanActivity.this.finish();
                            } else {
                                Toast.makeText(ChangeShetuanActivity.this, "信息修改失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ChangeShetuanActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case POST_LOGO:
                    String postlogoresult = (String) msg.obj;
                    if (postlogoresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(postlogoresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String logodata = jsonObject.getString("data");
                                changeshetuanbody = new FormBody.Builder()
                                        .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                        .add("cmid", String.valueOf(getintent.getIntExtra("cmid", 0)))
                                        .add("cmAnnouncement", changeShetuan.getStannouncement())
                                        .add("cmDetail", changeShetuan.getStintroduction())
                                        .add("cmLogo", logodata.substring(0, logodata.indexOf(".")))
                                        .build();

                                new Thread(new ChangeShetuanRunnable()).start();

                            } else {
                                Toast.makeText(ChangeShetuanActivity.this, "社团LOGO上传失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ChangeShetuanActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
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
                binding.changeShetuanLogo.setImageURI(new String(string + temppath));//将图片置入image
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

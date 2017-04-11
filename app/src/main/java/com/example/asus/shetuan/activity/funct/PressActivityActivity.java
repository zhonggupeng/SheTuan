package com.example.asus.shetuan.activity.funct;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
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

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.ChangePeosonInformationActivity;
import com.example.asus.shetuan.bean.PressActivity;
import com.example.asus.shetuan.clipimage.cliphead.ClipActivity;
import com.example.asus.shetuan.clipimage.cliprectimage.ClipRectActivity;
import com.example.asus.shetuan.databinding.ActivityPressActivityBinding;
import com.example.asus.shetuan.dateselector.DataSelect;
import com.example.asus.shetuan.model.DateUtils;
import com.example.asus.shetuan.model.OKHttpConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;

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

    private LayoutInflater inflater = null;
    private DataSelect dataSelect = null;
    private final int STARTTIMEID = 10;
    private final int ENDTIMEID = 11;
    private final int ENROLLDEADTIME = 110;

    private OKHttpConnect okHttpConnect;
    private String resultstring;
    private String url = "https://euswag.com/eu/activity/createav";
    private String tocken = "?accesstoken=zzzz";
    private String string;
    private long uid = Long.parseLong("15061884797");

    private PressActivity pressActivity;

    public ActivityPressActivityBinding getBinding() {
        return binding;
    }

    private ActivityPressActivityBinding binding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_press_activity);
        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        pressActivity = new PressActivity(this);
        binding.setPressactivity(pressActivity);
        dataSelect = new DataSelect(this);
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
                        } else if (pressActivity.getPrice() == null || pressActivity.getPrice().length() == 0) {
                            Toast.makeText(PressActivityActivity.this, "请填写活动费用", Toast.LENGTH_SHORT).show();
                        } else if (pressActivity.getPlace() == null || pressActivity.getPlace().length() == 0) {
                            Toast.makeText(PressActivityActivity.this, "请填写活动地址", Toast.LENGTH_SHORT).show();
                        } else {
                            if (binding.pressActivityIsregister.isChecked()) {
                                pressActivity.setRegister("0");
                            } else {
                                pressActivity.setRegister("-1");
                            }
                            string = "&avDetail=" + pressActivity.getDetail() + "&avExpectnum=" + Integer.parseInt(pressActivity.getExpectnum()) + "&avPlace=" + pressActivity.getPlace() + "&avPrice=" + Double.parseDouble(pressActivity.getPrice()) + "&avRegister=" + Integer.parseInt(pressActivity.getRegister()) + "&avTitle=" + pressActivity.getTitle() + "&avendtime=" + DateUtils.data(pressActivity.getEndtime()) + "&avenrolldeadline=" + DateUtils.data(pressActivity.getEnrolldeadline()) + "&avstarttime=" + DateUtils.data(pressActivity.getStarttime()) + "&uid=" + uid;
                            System.out.println(string);
                            Thread thread = new Thread(new PressActivityRunable());
                            thread.start();
                            System.out.println("11111111");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("resultstring:" + resultstring);
                            JSONObject jsonObject = null;
                            int result = 0;
                            try {
                                jsonObject = new JSONObject(resultstring);
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
                    }
                }).setNegativeButton("取消", null).show();
            }
        });

        File file = new File(Environment.getExternalStorageDirectory(), "SheTuan/cache");
        if (!file.exists()) {
            Toast.makeText(PressActivityActivity.this, "无法使用存储器，该功能无法正常使用", Toast.LENGTH_LONG).show();
            file.mkdirs();
        }
        photoSavePath = Environment.getExternalStorageDirectory().getPath() + "/SheTuan/cache/";
        System.out.println(photoSavePath);
        photoSaveName = System.currentTimeMillis() + ".jpeg";
        binding.pressActivityActivityimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(binding.pressActivityActivityimage);
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this).setTitle("提示").setMessage("您确定放弃发布活动吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                backPressed();
            }
        }).setNegativeButton("取消", null).show();
    }

    public void backPressed() {
        super.onBackPressed();
    }


    private class PressActivityRunable implements Runnable {
        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            try {
                resultstring = okHttpConnect.getdata(url + tocken + string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
                photoSaveName = String.valueOf(System.currentTimeMillis()) + ".png";
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
                Intent intent3 = new Intent(this, ClipRectActivity.class);
                intent3.putExtra("path", path);
                startActivityForResult(intent3, IMAGE_COMPLETE);
                break;
            case PHOTOTAKE:
                path = photoSavePath + photoSaveName;
                uri = Uri.fromFile(new File(path));
                Intent intent2 = new Intent(this, ClipRectActivity.class);
                intent2.putExtra("path", path);
                startActivityForResult(intent2, IMAGE_COMPLETE);
                break;
            case IMAGE_COMPLETE:
                final String temppath = data.getStringExtra("path");
                String string = "file://";
                System.out.println(temppath);
                binding.pressActivityActivityimage.setImageURI(new String(string + temppath));//将图片置入image
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

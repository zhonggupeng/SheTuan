package com.euswag.eu.activity.person;

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

import com.euswag.eu.R;
import com.euswag.eu.activity.login.InputPhoneActivity;
import com.euswag.eu.bean.PeosonInformation;
import com.euswag.eu.clipimage.ClipActivity;
import com.euswag.eu.databinding.ActivityChangePeosonInformationBinding;
import com.euswag.eu.model.OKHttpConnect;
import com.euswag.eu.weight.EditTextWithDel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ChangePeosonInformationActivity extends AppCompatActivity {

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
    private String headimagepath = null;
    private File headimagefile;

    private ActivityChangePeosonInformationBinding binding;
    private SharedPreferences sharedPreferences;
    private PeosonInformation peosonInformation;

    private OKHttpConnect okHttpConnect;
    private String requestpeoinformationurl = "https://euswag.com/eu/info/introinfo";
    private RequestBody requestpeoinformationbody;

    private String changeinformationurl = "https://euswag.com/eu/info/changeinfo";
    private RequestBody changeinformationbody;

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

        requestpeoinformationbody = new FormBody.Builder()
                .add("uid", sharedPreferences.getString("phonenumber", ""))
                .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                .build();
        new Thread(new RequestPeopleRunnable()).start();

        File file = new File(Environment.getExternalStorageDirectory(), "SheTuan/cache");
        if (!file.exists()) {
            Toast.makeText(this, "无法使用存储器，修改头像无法正常使用", Toast.LENGTH_LONG).show();
            file.mkdirs();
        }
        photoSavePath = Environment.getExternalStorageDirectory().getPath() + "/SheTuan/cache/";
        System.out.println(photoSavePath);
        photoSaveName = System.currentTimeMillis() + ".jpeg";
        click1();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void click1() {
        binding.changeInformBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePeosonInformationActivity.this.onBackPressed();
            }
        });
        binding.changeInformChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePeosonInformationActivity.this,AccountPasswordReviseActivity.class);
                ChangePeosonInformationActivity.this.startActivity(intent);
            }
        });
        binding.changeInformExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("accesstoken","");
                editor.commit();
                Intent intent = new Intent(ChangePeosonInformationActivity.this, InputPhoneActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                ChangePeosonInformationActivity.this.startActivity(intent);
            }
        });
    }

    private void click2() {
        binding.changeInformChangeHeadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(binding.changeInformHeadimage);
            }
        });
        binding.changeInformNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditTextWithDel editTextWithDel = new EditTextWithDel(ChangePeosonInformationActivity.this);
                editTextWithDel.setBackgroundResource(R.drawable.pin_edit_sharp);
                new AlertDialog.Builder(ChangePeosonInformationActivity.this).setTitle("填写昵称")
                        .setView(editTextWithDel)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextWithDel.getText() == null || editTextWithDel.getText().length() == 0) {
                                    Toast.makeText(ChangePeosonInformationActivity.this, "请填写昵称", Toast.LENGTH_SHORT).show();
                                } else {
                                    changeinformationbody = new FormBody.Builder()
                                            .add("uid", String.valueOf(peosonInformation.getUid()))
                                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                            .add("nickname",editTextWithDel.getText().toString())
                                            .build();
                                    new Thread(new ChangeInformationRunnable()).start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        binding.changeInformSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditTextWithDel editTextWithDel = new EditTextWithDel(ChangePeosonInformationActivity.this);
                editTextWithDel.setBackgroundResource(R.drawable.pin_edit_sharp);
                new AlertDialog.Builder(ChangePeosonInformationActivity.this).setTitle("填写性别")
                        .setView(editTextWithDel)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextWithDel.getText() == null || editTextWithDel.getText().length() == 0) {
                                    Toast.makeText(ChangePeosonInformationActivity.this, "请填写性别", Toast.LENGTH_SHORT).show();
                                } else {
                                    int gender;
                                    if (editTextWithDel.getText().equals("男")){
                                        gender = 0;
                                    }else if (editTextWithDel.getText().equals("女")){
                                        gender = 1;
                                    }else {
                                        gender = 2;
                                    }
                                    changeinformationbody = new FormBody.Builder()
                                            .add("uid", String.valueOf(peosonInformation.getUid()))
                                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                            .add("gender",String.valueOf(gender))
                                            .build();
                                    new Thread(new ChangeInformationRunnable()).start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        binding.changeInformAcamde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditTextWithDel editTextWithDel = new EditTextWithDel(ChangePeosonInformationActivity.this);
                editTextWithDel.setBackgroundResource(R.drawable.pin_edit_sharp);
                new AlertDialog.Builder(ChangePeosonInformationActivity.this).setTitle("填写学院专业班级")
                        .setView(editTextWithDel)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextWithDel.getText() == null || editTextWithDel.getText().length() == 0) {
                                    Toast.makeText(ChangePeosonInformationActivity.this, "请填写学院专业班级", Toast.LENGTH_SHORT).show();
                                } else {
                                    changeinformationbody = new FormBody.Builder()
                                            .add("uid", String.valueOf(peosonInformation.getUid()))
                                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                            .add("professionclass",editTextWithDel.getText().toString())
                                            .build();
                                    new Thread(new ChangeInformationRunnable()).start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        binding.changeInformStudentid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditTextWithDel editTextWithDel = new EditTextWithDel(ChangePeosonInformationActivity.this);
                editTextWithDel.setBackgroundResource(R.drawable.pin_edit_sharp);
                new AlertDialog.Builder(ChangePeosonInformationActivity.this).setTitle("填写学号")
                        .setView(editTextWithDel)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextWithDel.getText() == null || editTextWithDel.getText().length() == 0) {
                                    Toast.makeText(ChangePeosonInformationActivity.this, "请填写学号", Toast.LENGTH_SHORT).show();
                                } else {
                                    changeinformationbody = new FormBody.Builder()
                                            .add("uid", String.valueOf(peosonInformation.getUid()))
                                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                            .add("studentid",editTextWithDel.getText().toString())
                                            .build();
                                    new Thread(new ChangeInformationRunnable()).start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        binding.changeInformName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditTextWithDel editTextWithDel = new EditTextWithDel(ChangePeosonInformationActivity.this);
                editTextWithDel.setBackgroundResource(R.drawable.pin_edit_sharp);
                new AlertDialog.Builder(ChangePeosonInformationActivity.this).setTitle("填写姓名")
                        .setView(editTextWithDel)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextWithDel.getText() == null || editTextWithDel.getText().length() == 0) {
                                    Toast.makeText(ChangePeosonInformationActivity.this, "请填写姓名", Toast.LENGTH_SHORT).show();
                                } else {
                                    changeinformationbody = new FormBody.Builder()
                                            .add("uid", String.valueOf(peosonInformation.getUid()))
                                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                            .add("name",editTextWithDel.getText().toString())
                                            .build();
                                    new Thread(new ChangeInformationRunnable()).start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        binding.changeInformEntryyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditTextWithDel editTextWithDel = new EditTextWithDel(ChangePeosonInformationActivity.this);
                editTextWithDel.setBackgroundResource(R.drawable.pin_edit_sharp);
                new AlertDialog.Builder(ChangePeosonInformationActivity.this).setTitle("填写入学年份")
                        .setView(editTextWithDel)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (editTextWithDel.getText() == null || editTextWithDel.getText().length() == 0) {
                                    Toast.makeText(ChangePeosonInformationActivity.this, "请填写入学年份", Toast.LENGTH_SHORT).show();
                                } else {
                                    changeinformationbody = new FormBody.Builder()
                                            .add("uid", String.valueOf(peosonInformation.getUid()))
                                            .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                            .add("grade",editTextWithDel.getText().toString())
                                            .build();
                                    new Thread(new ChangeInformationRunnable()).start();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

    }

    private class RequestPeopleRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(requestpeoinformationurl, requestpeoinformationbody);
                Message message = handler.obtainMessage();
                message.what = REQUEST_PEOPLE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class PostHeadimageRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postfile(postheadimageurl, headimagefile);
                Message message = handler.obtainMessage();
                message.what = POST_HEADIMAGE;
                message.obj = resultstring;
                handler.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ChangeInformationRunnable implements Runnable {

        @Override
        public void run() {
            okHttpConnect = new OKHttpConnect();
            String resultstring;
            try {
                resultstring = okHttpConnect.postdata(changeinformationurl, changeinformationbody);
                Message message = handler.obtainMessage();
                message.what = CHANGE_INFORMATION;
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
                case REQUEST_PEOPLE:
                    String requestpeopleresult = (String) msg.obj;
                    if (requestpeopleresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(requestpeopleresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String requestpeopledata = jsonObject.getString("data");
                                JSONObject jsonObject1 = new JSONObject(requestpeopledata);
                                peosonInformation.setUid(Long.parseLong(sharedPreferences.getString("phonenumber", "0")));
                                System.out.println("uid:" + peosonInformation.getUid());
                                peosonInformation.setHeadimage(headimageloadurl + jsonObject1.getString("avatar") + ".jpg");
                                peosonInformation.setNickname(jsonObject1.getString("nickname"));
                                peosonInformation.setAcademe(jsonObject1.getString("professionclass"));
                                if (jsonObject1.getInt("gender") == 0) {
                                    peosonInformation.setGender("男");
                                } else if (jsonObject1.getInt("gender") == 1) {
                                    peosonInformation.setGender("女");
                                } else {
                                    peosonInformation.setGender("保密");
                                }
                                peosonInformation.setEntryyear(String.valueOf(jsonObject1.getInt("grade")));
                                peosonInformation.setName(jsonObject1.getString("name"));
                                peosonInformation.setStudentid(jsonObject1.getString("studentid"));
                                peosonInformation.setPersonalexplaintion(jsonObject1.getString("userdescription"));
                                binding.changeInformHeadimage.setImageURI(peosonInformation.getHeadimage());
                                binding.changeInformAcamde.setText(peosonInformation.getAcademe());
                                binding.changeInformEntryyear.setText(peosonInformation.getEntryyear());
                                binding.changeInformName.setText(peosonInformation.getName());
                                binding.changeInformNickname.setText(peosonInformation.getNickname());
                                binding.changeInformSex.setText(peosonInformation.getGender());
                                binding.changeInformStudentid.setText(peosonInformation.getStudentid());
                                click2();
                            } else {
                                Toast.makeText(ChangePeosonInformationActivity.this, "个人信息加载失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ChangePeosonInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case POST_HEADIMAGE:
                    String postheadimageresult = (String) msg.obj;
                    if (postheadimageresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(postheadimageresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                String postheadimagedata = jsonObject.getString("data");
                                changeinformationbody = new FormBody.Builder()
                                        .add("uid", String.valueOf(peosonInformation.getUid()))
                                        .add("accesstoken", sharedPreferences.getString("accesstoken", ""))
                                        .add("avatar", postheadimagedata.substring(0, postheadimagedata.indexOf(".")))
                                        .build();
                                new Thread(new ChangeInformationRunnable()).start();
                            } else {
                                Toast.makeText(ChangePeosonInformationActivity.this, "头像上传失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ChangePeosonInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CHANGE_INFORMATION:
                    String changeinformtionresult = (String) msg.obj;
                    if (changeinformtionresult.length() != 0) {
                        JSONObject jsonObject;
                        int result;
                        try {
                            jsonObject = new JSONObject(changeinformtionresult);
                            result = jsonObject.getInt("status");
                            if (result == 200) {
                                Toast.makeText(ChangePeosonInformationActivity.this,"信息修改成功",Toast.LENGTH_SHORT).show();
                                new Thread(new RequestPeopleRunnable()).start();
                            } else {
                                Toast.makeText(ChangePeosonInformationActivity.this, "修改信息失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ChangePeosonInformationActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
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
                System.out.println("相册回调uri" + uri.toString());
                String[] proj = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);//获得或者选择图片
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);//得到一个图片
                System.out.println("图片路径：" + path);
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
                binding.changeInformHeadimage.setImageURI(new String(string + temppath));
//                imageView.setImageURI();//将图片置入image
                headimagefile = new File(temppath);
                new Thread(new PostHeadimageRunnable()).start();
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
        } else {
            super.onBackPressed();
        }
    }

}

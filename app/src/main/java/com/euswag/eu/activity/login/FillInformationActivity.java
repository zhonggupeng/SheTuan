package com.euswag.eu.activity.login;

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

import com.android.debug.hv.ViewServer;
import com.euswag.eu.R;
import com.euswag.eu.bean.InformationFill;
import com.euswag.eu.clipimage.ClipActivity;
import com.euswag.eu.databinding.ActivityFillInformationBinding;
import com.euswag.eu.weight.sexselector.SexSelect;

import java.io.File;

public class FillInformationActivity extends AppCompatActivity {

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
    private String headimagepath = "";

    private ActivityFillInformationBinding binding;
    private InformationFill informationFill;
    private SexSelect sexSelect;

    public ActivityFillInformationBinding getBinding() {
        return binding;
    }

    public void setBinding(ActivityFillInformationBinding binding) {
        this.binding = binding;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewServer.get(this).addWindow(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fill_information);
        informationFill = new InformationFill(FillInformationActivity.this);
        binding.setInformation(informationFill);
        Intent intent = getIntent();
        informationFill.setPhonenumber(intent.getStringExtra("phonenumber"));

        File file = new File(Environment.getExternalStorageDirectory(),"SheTuan/cache");
        if (!file.exists()) {
            Toast.makeText(FillInformationActivity.this,"无法使用存储器，你无法使用头像",Toast.LENGTH_LONG).show();
            file.mkdirs();
            binding.fillInformHeadimage.setClickable(false);
        }else {
            photoSavePath = Environment.getExternalStorageDirectory().getPath() + "/SheTuan/cache/";
            System.out.println(photoSavePath);
            photoSaveName = System.currentTimeMillis() + ".jpeg";
        }
        sexSelect = new SexSelect(this);
        click();
        //解决启动Activy时自动弹出输入法
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void click(){
        binding.fillInformHeadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(binding.fillInformHeadimage);
            }
        });
        binding.fillInformFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (informationFill.getNickname()==null||informationFill.getNickname().length()==0){
                    Toast.makeText(FillInformationActivity.this,"未填写昵称",Toast.LENGTH_SHORT).show();
                }else if (binding.fillInformSexselect.getText()==null||binding.fillInformSexselect.getText().length()==0){
                    Toast.makeText(FillInformationActivity.this,"未选择性别",Toast.LENGTH_SHORT).show();
                }else if (informationFill.getAcademe()==null||informationFill.getAcademe().length()==0){
                    Toast.makeText(FillInformationActivity.this,"未填写学院专业班级",Toast.LENGTH_SHORT).show();
                }else if (informationFill.getStudentid()==null||informationFill.getStudentid().length()==0){
                    Toast.makeText(FillInformationActivity.this,"未填写学号",Toast.LENGTH_SHORT).show();
                }else if (informationFill.getName()==null||informationFill.getName().length()==0){
                    Toast.makeText(FillInformationActivity.this,"未填写姓名",Toast.LENGTH_SHORT).show();
                }else if (binding.fillInformEntryyear.getText()==null||binding.fillInformEntryyear.getText().length()==0){
                    Toast.makeText(FillInformationActivity.this,"未填写入学年份",Toast.LENGTH_SHORT).show();
                }else {
                            Intent intent = new Intent(FillInformationActivity.this, SetPasswordActivity.class);
                            intent.putExtra("isregister", "0");
                            intent.putExtra("phonenumber", informationFill.getPhonenumber());
                            intent.putExtra("nickname", informationFill.getNickname());
                            //
                            intent.putExtra("sex", binding.fillInformSexselect.getText());
                            //
                            intent.putExtra("academe", informationFill.getAcademe());
                            intent.putExtra("studentid", informationFill.getStudentid());
                            intent.putExtra("name", informationFill.getName());
                            intent.putExtra("entryyear", binding.fillInformEntryyear.getText());
                            intent.putExtra("headimagepath", headimagepath);
                            FillInformationActivity.this.startActivity(intent);
                }
            }
        });
        binding.fillInformSexselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sexSelect.showSex();
            }
        });
    }

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
                String[] proj = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(uri, proj, null, null,null);//获得或者选择图片
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);//得到一个图片
                Intent intent3=new Intent(FillInformationActivity.this, ClipActivity.class);
                intent3.putExtra("path", path);
                startActivityForResult(intent3, IMAGE_COMPLETE);
                break;
            case PHOTOTAKE:
                path=photoSavePath+photoSaveName;
                uri = Uri.fromFile(new File(path));
                Intent intent2=new Intent(FillInformationActivity.this, ClipActivity.class);
                intent2.putExtra("path", path);
                startActivityForResult(intent2, IMAGE_COMPLETE);
                break;
            case IMAGE_COMPLETE:
                final String temppath = data.getStringExtra("path");
                String string = "file://";
                System.out.println(temppath);
                headimagepath = temppath;
                binding.fillInformHeadimage.setImageURI(new String(string+temppath));//将图片置入image
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).addWindow(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).addWindow(this);
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

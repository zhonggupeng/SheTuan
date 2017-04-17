package com.example.asus.shetuan.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.databinding.ActivityDetailsQrcodeBinding;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DetailsQRcodeActivity extends AppCompatActivity {
    private ActivityDetailsQrcodeBinding binding;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_details_qrcode);
        intent = getIntent();
        binding.detailsQrcodeTitle.setText(intent.getStringExtra("title"));
        if (intent.getStringExtra("type").equals("act")){
            binding.detailsQrcodeDescription.setText("扫一扫二维码，了解活动详情");
        }
        String qrstring = "www.euswag.com?avid="+intent.getStringExtra("id");
        Bitmap bitmap = EncodingUtils.createQRCode(qrstring,700,700, BitmapFactory.decodeResource(getResources(),0));
        binding.detailsQrcodeQriamge.setImageBitmap(bitmap);
        click();
    }
    private void click(){
        binding.detailsQrcodeBackimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailsQRcodeActivity.this.onBackPressed();
            }
        });
        binding.detailsQrcodeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap(binding.detailsQrcodeCliplinear);
            }
        });
    }
    private void saveBitmap(View v) {
        String fileName = System.currentTimeMillis() + ".png";
        Bitmap bm = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        v.draw(canvas);
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/DCIM/Camera/", fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

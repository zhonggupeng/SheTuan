package com.example.asus.shetuan.clipimage.cliprectimage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.clipimage.cliphead.ClipImageLayout;
import com.example.asus.shetuan.clipimage.cliphead.ImageTools;

import java.io.File;

public class ClipRectActivity extends Activity{
	private ClipRectImageLayout mClipImageLayout;
	private String path;
	private ProgressDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clipimage);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loadingDialog=new ProgressDialog(this);
        loadingDialog.setTitle("请稍后...");
		path=getIntent().getStringExtra("path");
		if(TextUtils.isEmpty(path)||!(new File(path).exists())){
			Toast.makeText(this, "图片加载失败",Toast.LENGTH_SHORT).show();
			return;
		}
		Bitmap bitmap=ImageTools.convertToBitmap(path, 600,600);
		if(bitmap==null){
			Toast.makeText(this, "图片加载失败",Toast.LENGTH_SHORT).show();
			return;
		}
		mClipImageLayout = (ClipRectImageLayout) findViewById(R.id.id_clipImageLayout);
		mClipImageLayout.setBitmap(bitmap);
		((Button)findViewById(R.id.id_action_clip)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadingDialog.show();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Bitmap bitmap = mClipImageLayout.clip();
						String path= Environment.getExternalStorageDirectory()+"/ClipHeadPhoto/cache/"+System.currentTimeMillis()+ ".jpeg";
						ImageTools.savePhotoToSDCard(bitmap,path);
						loadingDialog.dismiss();
						Intent intent = new Intent();
						intent.putExtra("path",path);
						setResult(RESULT_OK, intent);
						finish();
					}
				}).start();
			}
		});
	}
	
	
	
}

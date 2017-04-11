package com.example.asus.shetuan.clipimage.cliprectimage;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import com.example.asus.shetuan.clipimage.cliphead.ClipImageBorderView;
import com.example.asus.shetuan.clipimage.cliphead.ClipZoomImageView;

public class ClipRectImageLayout extends RelativeLayout
{

	private ClipRectZoomImageView mZoomImageView;
	private ClipRectImageBorderView mClipImageView;

	private int mHorizontalPadding =0;

	public ClipRectImageLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		mZoomImageView = new ClipRectZoomImageView(context);
		mClipImageView = new ClipRectImageBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		
		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		mHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources().getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}
	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;
	}

	public Bitmap clip()
	{
		return mZoomImageView.clip();
	}

	public void setBitmap(Bitmap bitmap) {
		mZoomImageView.setImageBitmap(bitmap);
	}

}

package com.example.asus.shetuan.clipimage;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class ClipImageBorderView extends View
{
//	private int mHorizontalPadding;
	private int mBorderWidth = 2;
	private Paint mPaint;

	public ClipImageBorderView(Context context)
	{
		this(context, null);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	
		mBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources()
						.getDisplayMetrics());
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		mPaint.setColor(Color.parseColor("#FFFFFF"));
		mPaint.setStrokeWidth(mBorderWidth);
		mPaint.setStyle(Style.STROKE);

		canvas.drawRect(0,getHeight()/2-getHeight()/4,getWidth(),getHeight()/2+getHeight()/4,mPaint);

	}

//	public void setHorizontalPadding(int mHorizontalPadding)
//	{
//		this.mHorizontalPadding = mHorizontalPadding;
//
//	}

}

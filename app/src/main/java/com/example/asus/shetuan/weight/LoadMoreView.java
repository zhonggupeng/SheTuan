package com.example.asus.shetuan.weight;

import android.content.Context;
import android.widget.FrameLayout;

import com.example.asus.shetuan.model.OnRetryClickListener;

/**
 * Created by ASUS on 2017/3/28.
 */

public abstract class LoadMoreView extends FrameLayout{
    public LoadMoreView(Context context) {
        super(context);
    }

    public abstract void showLoading();

    public abstract void showRetry();

    public abstract void showEnd();

    public abstract boolean isLoadMoreEnable();

    public abstract void setOnRetryClickListener(OnRetryClickListener listener);

}

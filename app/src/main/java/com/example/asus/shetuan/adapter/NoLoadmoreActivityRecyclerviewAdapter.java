package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.databinding.ViewActivityItemBinding;

/**
 * Created by ASUS on 2017/4/2.
 */

public class NoLoadmoreActivityRecyclerviewAdapter extends RecyclerviewBaseAdapter2<ActivityMsg> {
    public NoLoadmoreActivityRecyclerviewAdapter(Context context) {
        super(context, R.layout.view_activity_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ActivityMsg item, int indexOfData) {
        ((NormalHolder)holder).getBinding().setVariable(BR.activitymsg,item);
        ((ViewActivityItemBinding)((NormalHolder)holder).getBinding()).activityItemImage.setImageURI(item.getImageurl());
        ((NormalHolder)holder).getBinding().executePendingBindings();
    }
}

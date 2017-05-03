package com.euswag.eu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.euswag.eu.BR;
import com.euswag.eu.R;
import com.euswag.eu.bean.ActivityMsg;
import com.euswag.eu.databinding.ViewActivityItemBinding;


/**
 * Created by ASUS on 2017/4/2.
 */

public class ActivityRecyclerviewAdapter extends RecyclerviewBaseAdapter<ActivityMsg> {
    public ActivityRecyclerviewAdapter(Context context) {
        super(context, R.layout.view_activity_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ActivityMsg item, int indexOfData) {
        ((NormalHolder)holder).getBinding().setVariable(BR.activitymsg,item);
        ((ViewActivityItemBinding)((NormalHolder)holder).getBinding()).activityItemImage.setImageURI(item.getImageurl());
        if (item.getActprice()==0) {
            ((ViewActivityItemBinding) ((NormalHolder) holder).getBinding()).actiivtyItemPrice.setText("免费");
        }else {
            ((ViewActivityItemBinding) ((NormalHolder) holder).getBinding()).actiivtyItemPrice.setText(String.valueOf(item.getActprice()));
        }
        ((NormalHolder)holder).getBinding().executePendingBindings();
    }
}

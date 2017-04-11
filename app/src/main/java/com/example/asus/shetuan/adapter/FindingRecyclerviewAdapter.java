package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ShetuanMsg;
import com.example.asus.shetuan.databinding.ViewShetuanItemBinding;

/**
 * Created by ASUS on 2017/4/2.
 */

public class FindingRecyclerviewAdapter extends RecyclerviewBaseAdapter<ShetuanMsg> {
    public FindingRecyclerviewAdapter(Context context) {
        super(context, R.layout.view_shetuan_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ShetuanMsg item, int indexOfData) {
        ((NormalHolder)holder).getBinding().setVariable(BR.shetuanmsg,item);
        ((ViewShetuanItemBinding)((NormalHolder)holder).getBinding()).shetuanItemBackground.setImageURI(item.getBackgroundimage());
        ((ViewShetuanItemBinding)((NormalHolder)holder).getBinding()).shetuanItemLogoimage.setImageURI(item.getLogoimage());
        ((NormalHolder)holder).getBinding().executePendingBindings();
    }
}

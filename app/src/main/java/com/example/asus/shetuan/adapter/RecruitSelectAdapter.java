package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ShetuanContacts;
import com.example.asus.shetuan.databinding.ViewCheckManageItemBinding;

/**
 * Created by ASUS on 2017/4/28.
 */

public class RecruitSelectAdapter extends SelectBaseAdapter<ShetuanContacts> {
    public RecruitSelectAdapter(Context context) {
        super(context, R.layout.view_check_manage_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ShetuanContacts item, int indexOfData) {
        ((SelectHolder)holder).getBinding().setVariable(BR.shetuanContactsitem,item);
        ((SelectHolder)holder).getBinding().checkManageItemImage.setImageURI(item.getAvatar());
        ((SelectHolder)holder).getBinding().executePendingBindings();
    }
}

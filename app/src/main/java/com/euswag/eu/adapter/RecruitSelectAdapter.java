package com.euswag.eu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.euswag.eu.BR;
import com.euswag.eu.R;
import com.euswag.eu.bean.ShetuanContacts;


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

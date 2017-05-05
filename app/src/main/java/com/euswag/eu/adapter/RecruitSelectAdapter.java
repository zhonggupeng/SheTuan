package com.euswag.eu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.euswag.eu.BR;
import com.euswag.eu.R;
import com.euswag.eu.bean.ShetuanContacts;
import com.euswag.eu.model.AllCheckListener;


/**
 * Created by ASUS on 2017/4/28.
 */

public class RecruitSelectAdapter extends SelectBaseAdapter {
    public RecruitSelectAdapter(Context context, AllCheckListener allCheckListener) {
        super(context, R.layout.view_check_manage_item,allCheckListener);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ShetuanContacts item, int indexOfData) {
        ((SelectHolder)holder).getBinding().setVariable(BR.shetuanContactsitem,item);
        ((SelectHolder)holder).getBinding().checkManageItemImage.setImageURI(item.getAvatar());
        ((SelectHolder)holder).getBinding().checkManageItemCheckbox.setChecked(item.isCheck());
        ((SelectHolder)holder).getBinding().executePendingBindings();
    }
}

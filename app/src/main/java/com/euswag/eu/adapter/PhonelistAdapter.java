package com.euswag.eu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.euswag.eu.R;
import com.euswag.eu.bean.ShetuanContacts;

/**
 * Created by ASUS on 2017/5/2.
 */

public class PhonelistAdapter extends PhonelistBaseAdapter<ShetuanContacts> {
    public PhonelistAdapter(Context context) {
        super(context, R.layout.view_phonelist_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ShetuanContacts item, int indexOfData) {
        ((PhonelistHolder)holder).getBinding().phonelistItemHeadimage.setImageURI(item.getAvatar());
        ((PhonelistHolder)holder).getBinding().phonelistItemName.setText(item.getName());
        if (item.getPosition() == 3){
            ((PhonelistHolder)holder).getBinding().phonelistItemPosition.setText("社长");
        }else if (item.getPosition() == 2){
            ((PhonelistHolder)holder).getBinding().phonelistItemPosition.setText("管理员");
        }else if (item.getPosition() == 1){
            ((PhonelistHolder)holder).getBinding().phonelistItemPosition.setText("成员");
        }
    }
}

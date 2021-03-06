package com.euswag.eu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.euswag.eu.R;
import com.euswag.eu.bean.PeosonInformation;

/**
 * Created by ASUS on 2017/5/2.
 */

public class SetManagerAdapter extends SliderDeleteBaseAdapter<PeosonInformation> {
    public SetManagerAdapter(Context context) {
        super(context, R.layout.view_menber_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, PeosonInformation item, int indexOfData) {
        ((Holder)holder).getBinding().setPeosonInformation(item);
        ((Holder)holder).getBinding().viewMenberHeadimage.setImageURI(item.getHeadimage());
        ((Holder)holder).getBinding().viewMenberDelete.setText("设为管理员");
        ((Holder)holder).getBinding().executePendingBindings();
    }
}

package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.PeosonInformation;

/**
 * Created by ASUS on 2017/5/3.
 */

public class CancelManagerAdapter extends SliderDeleteBaseAdapter<PeosonInformation>  {
    public CancelManagerAdapter(Context context) {
        super(context, R.layout.view_menber_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, PeosonInformation item, int indexOfData) {
        ((Holder)holder).getBinding().setPeosonInformation(item);
        ((Holder)holder).getBinding().viewMenberHeadimage.setImageURI(item.getHeadimage());
        ((Holder)holder).getBinding().viewMenberDelete.setText("取消管理员");
        ((Holder)holder).getBinding().executePendingBindings();
    }
}

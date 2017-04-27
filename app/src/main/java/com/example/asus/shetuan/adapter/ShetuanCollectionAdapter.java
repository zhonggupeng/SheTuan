package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ShetuanMsg;
import com.example.asus.shetuan.databinding.ViewShetuanCollectionItemBinding;

/**
 * Created by ASUS on 2017/4/27.
 */

public class ShetuanCollectionAdapter extends RecyclerviewBaseAdapter2<ShetuanMsg>{
    public ShetuanCollectionAdapter(Context context) {
        super(context, R.layout.view_shetuan_collection_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ShetuanMsg item, int indexOfData) {
        ((NormalHolder)holder).getBinding().setVariable(BR.shetuanMsg,item);
        ((ViewShetuanCollectionItemBinding)((NormalHolder)holder).getBinding()).shetuanCollectionItemLogo.setImageURI(item.getLogoimage());
        ((NormalHolder)holder).getBinding().executePendingBindings();
    }
}

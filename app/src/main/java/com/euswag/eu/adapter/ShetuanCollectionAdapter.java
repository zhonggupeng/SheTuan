package com.euswag.eu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.euswag.eu.BR;
import com.euswag.eu.R;
import com.euswag.eu.bean.ShetuanMsg;
import com.euswag.eu.databinding.ViewShetuanCollectionItemBinding;


/**
 * Created by ASUS on 2017/4/27.
 */

public class ShetuanCollectionAdapter extends RecyclerviewBaseAdapter2<ShetuanMsg>{
    public ShetuanCollectionAdapter(Context context) {
        super(context, R.layout.view_shetuan_collection_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, ShetuanMsg item, int indexOfData) {
        ((NormalHolder)holder).getBinding().setVariable(BR.shetuancollectionitem,item);
        ((ViewShetuanCollectionItemBinding)((NormalHolder)holder).getBinding()).shetuanCollectionItemLogo.setImageURI(item.getLogoimage());
        ((NormalHolder)holder).getBinding().executePendingBindings();
    }
}

package com.euswag.eu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.euswag.eu.BR;
import com.euswag.eu.R;
import com.euswag.eu.bean.PeosonInformation;
import com.euswag.eu.databinding.ViewMenberItemBinding;

/**
 * Created by ASUS on 2017/5/11.
 */

public class SearchPeopleAdapter extends RecyclerviewBaseAdapter<PeosonInformation> {
    public SearchPeopleAdapter(Context context) {
        super(context, R.layout.view_menber_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, PeosonInformation item, int indexOfData) {
        ((NormalHolder)holder).getBinding().setVariable(BR.peosonInformation,item);
        ((ViewMenberItemBinding)((NormalHolder)holder).getBinding()).viewMenberHeadimage.setImageURI(item.getHeadimage());
        ((ViewMenberItemBinding)((NormalHolder)holder).getBinding()).viewMenberGotoimage.setImageResource(0);
        if (item.getVerified() == 1){
            ((ViewMenberItemBinding)((NormalHolder)holder).getBinding()).viewMenberVerified.setImageResource(R.drawable.verified);
        }else {
            ((ViewMenberItemBinding)((NormalHolder)holder).getBinding()).viewMenberVerified.setImageResource(0);
        }
        ((NormalHolder)holder).getBinding().executePendingBindings();
    }
}

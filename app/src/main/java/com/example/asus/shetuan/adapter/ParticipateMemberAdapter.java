package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.PeosonInformation;
import com.example.asus.shetuan.databinding.ViewMenberItemBinding;

/**
 * Created by ASUS on 2017/4/19.
 */

public class ParticipateMemberAdapter extends RecyclerviewBaseAdapter2<PeosonInformation> {
    public ParticipateMemberAdapter(Context context) {
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

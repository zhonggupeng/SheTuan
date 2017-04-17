package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.PeosonInformation;

/**
 * Created by ASUS on 2017/4/16.
 */

public class MemberSliderDeleteAdapter extends SliderDeleteBaseAdapter<PeosonInformation> {
    public MemberSliderDeleteAdapter(Context context) {
        super(context, R.layout.view_menber_item);
    }

    @Override
    protected void convert(RecyclerView.ViewHolder holder, PeosonInformation item, int indexOfData) {
        ((Holder)holder).getBinding().setPeosonInformation(item);
        ((Holder)holder).getBinding().viewMenberHeadimage.setImageURI(item.getHeadimage());
        if (item.getVerified()==1){
            ((Holder)holder).getBinding().viewMenberVerified.setImageResource(R.drawable.verified);
        }
        else {
            ((Holder)holder).getBinding().viewMenberVerified.setImageResource(0);
        }
        ((Holder)holder).getBinding().viewMenberUid.setText(String.valueOf(item.getUid()));
    }
}

package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.act.ActivityDetailActivity;
import com.example.asus.shetuan.activity.act.CheckActivityActivity;
import com.example.asus.shetuan.activity.shetuan.ShetuanInformationActivity;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.bean.ShetuanMsg;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/4/2.
 */

public abstract class RecyclerviewBaseAdapter2<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected Context mContext;
    protected int mLayoutResId = -1;

    protected abstract void convert(RecyclerView.ViewHolder holder, T item, int indexOfData);

    private ArrayList<T> mData = new ArrayList<>();
    public RecyclerviewBaseAdapter2(Context context, int layoutResId){
        mContext = context;
        mLayoutResId = layoutResId;
    }
    public void setmData(ArrayList<T> data){
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), mLayoutResId,parent,false);
            NormalHolder normalHolder = new NormalHolder(binding.getRoot());
            normalHolder.setBinding(binding);
            return normalHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(position<mData.size()){
            //设置点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mLayoutResId == R.layout.view_activity_item) {
                        if (((ActivityMsg) (mData.get(position))).getIsbuild() == 0) {
                            Intent intent = new Intent(mContext, ActivityDetailActivity.class);
                            intent.putExtra("datajson1", ((ActivityMsg) (mData.get(position))).getActivityDetailJsonString());
                            intent.putExtra("isparticipate", ((ActivityMsg) (mData.get(position))).getIsparticipate());
                            mContext.startActivity(intent);
                        } else {
                            Intent intent = new Intent(mContext, CheckActivityActivity.class);
                            intent.putExtra("datajson2", ((ActivityMsg) (mData.get(position))).getActivityDetailJsonString());
                            mContext.startActivity(intent);
                        }
                    }else if (mLayoutResId == R.layout.view_shetuan_collection_item){
                        Intent intent = new Intent(mContext, ShetuanInformationActivity.class);
                        intent.putExtra("datajson3",((ShetuanMsg)(mData.get(position))).getShetuanJsonString());
                        intent.putExtra("collection","1");
                        mContext.startActivity(intent);
                    }
                }
            });
            convert(holder,mData.get(position),position);
        }
        else {
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder{

        public NormalHolder(View itemView) {
            super(itemView);
        }
        private ViewDataBinding binding;

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }

        public ViewDataBinding getBinding() {
            return this.binding;
        }

    }
}

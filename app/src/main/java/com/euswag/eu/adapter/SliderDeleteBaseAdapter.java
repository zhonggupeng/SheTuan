package com.euswag.eu.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.euswag.eu.databinding.ViewMenberItemBinding;

import java.util.ArrayList;
/**
 * Created by ASUS on 2017/4/16.
 */

public abstract class SliderDeleteBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<T> mData = new ArrayList<>();
    private Context context;
    private int mLayoutResId = -1;

    protected abstract void convert(RecyclerView.ViewHolder holder, T item, int indexOfData);

    public void setmData(ArrayList<T> data){
        mData = data;
    }

    public SliderDeleteBaseAdapter(Context context, int layoutResId) {
        this.context = context;
        this.mLayoutResId = layoutResId;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewMenberItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), mLayoutResId,parent,false);
        Holder holder = new Holder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //设置点击事件
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        convert(holder,mData.get(position),position);
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Holder extends RecyclerView.ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
        private ViewMenberItemBinding binding;

        public void setBinding(ViewMenberItemBinding binding) {
            this.binding = binding;
        }

        public ViewMenberItemBinding getBinding() {
            return this.binding;
        }
    }
}


package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.shetuan.databinding.ViewCheckManageItemBinding;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/4/28.
 */

public abstract class SelectBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<T> mData = new ArrayList<>();
    private Context context;
    private int mLayoutResId = -1;

    public void setmData(ArrayList<T> data){
        mData = data;
    }
    protected abstract void convert(RecyclerView.ViewHolder holder, T item, int indexOfData);

    public SelectBaseAdapter(Context context, int layoutResId){
        this.context = context;
        this.mLayoutResId = layoutResId;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewCheckManageItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), mLayoutResId,parent,false);
        SelectHolder holder = new SelectHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //设置点击事件
        convert(holder,mData.get(position),position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public class SelectHolder extends RecyclerView.ViewHolder{

        public SelectHolder(View itemView) {
            super(itemView);
        }
        private ViewCheckManageItemBinding binding;

        public void setBinding(ViewCheckManageItemBinding binding) {
            this.binding = binding;
        }

        public ViewCheckManageItemBinding getBinding() {
            return this.binding;
        }
    }
}

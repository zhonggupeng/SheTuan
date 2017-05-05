package com.euswag.eu.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.euswag.eu.bean.ShetuanContacts;
import com.euswag.eu.databinding.ViewCheckManageItemBinding;
import com.euswag.eu.model.AllCheckListener;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/4/28.
 */

public abstract class SelectBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<ShetuanContacts> mData = new ArrayList<>();
    private Context context;
    private int mLayoutResId = -1;
    private AllCheckListener allCheckListener;

    public void setmData(ArrayList<ShetuanContacts> data){
        mData = data;
    }
    protected abstract void convert(RecyclerView.ViewHolder holder, ShetuanContacts item, int indexOfData);

    public SelectBaseAdapter(Context context, int layoutResId, AllCheckListener allCheckListener){
        this.context = context;
        this.mLayoutResId = layoutResId;
        this.allCheckListener = allCheckListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewCheckManageItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), mLayoutResId,parent,false);
        SelectHolder holder = new SelectHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //设置点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((SelectHolder)holder).getBinding().checkManageItemCheckbox.isChecked()){
                    ((SelectHolder)holder).getBinding().checkManageItemCheckbox.setChecked(false);
                    mData.get(position).setCheck(false);
                }else {
                    ((SelectHolder)holder).getBinding().checkManageItemCheckbox.setChecked(true);
                    mData.get(position).setCheck(true);
                }
                for (ShetuanContacts data : mData){
                    System.out.println(data.isCheck());
                    if (!data.isCheck()){
                        allCheckListener.onCheckedChanged(false);
                        return;
                    }
                }
                allCheckListener.onCheckedChanged(true);
            }
        });
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

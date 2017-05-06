package com.euswag.eu.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.euswag.eu.R;
import com.euswag.eu.bean.ShetuanNotification;
import com.euswag.eu.databinding.ViewShetuanNotificationItemBinding;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/5/6.
 */

public class ShetuanNotificationAdapter extends RecyclerView.Adapter<ShetuanNotificationAdapter.ShetuanHolder>{

    private ArrayList<ShetuanNotification> mData = new ArrayList<ShetuanNotification>();

    public void setmData(ArrayList<ShetuanNotification> data){
        mData = data;
    }
    private Activity activity;
    public ShetuanNotificationAdapter(Activity activity){
        this.activity = activity;
    }

    @Override
    public ShetuanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewShetuanNotificationItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_shetuan_notification_item,parent,false);
        ShetuanHolder holder = new ShetuanHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShetuanHolder holder, int position) {
        holder.getBinding().setShetuanNotification(mData.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ShetuanHolder extends RecyclerView.ViewHolder{

        public ShetuanHolder(View itemView) {
            super(itemView);
        }

        public ViewShetuanNotificationItemBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewShetuanNotificationItemBinding binding) {
            this.binding = binding;
        }

        private ViewShetuanNotificationItemBinding binding;
    }
}

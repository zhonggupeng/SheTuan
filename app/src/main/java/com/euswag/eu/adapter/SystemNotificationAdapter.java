package com.euswag.eu.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.euswag.eu.R;
import com.euswag.eu.bean.SystremNotification;
import com.euswag.eu.databinding.ViewSystemNotificationItemBinding;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/5/6.
 */

public class SystemNotificationAdapter extends RecyclerView.Adapter<SystemNotificationAdapter.SystemHolder> {
    private ArrayList<SystremNotification> mData = new ArrayList<>();

    public void setmData(ArrayList<SystremNotification> data){
        mData = data;
    }

    private Activity activity;
    public SystemNotificationAdapter(Activity activity){
        this.activity = activity;
    }

    @Override
    public SystemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewSystemNotificationItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_system_notification_item,parent,false);
        SystemHolder holder = new SystemHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(SystemHolder holder, int position) {
        holder.getBinding().setSystemNotification(mData.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class SystemHolder extends RecyclerView.ViewHolder{

        public SystemHolder(View itemView) {
            super(itemView);
        }

        public ViewSystemNotificationItemBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewSystemNotificationItemBinding binding) {
            this.binding = binding;
        }

        private ViewSystemNotificationItemBinding binding;

    }
}

package com.euswag.eu.adapter;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.euswag.eu.R;
import com.euswag.eu.bean.ActivityNotification;
import com.euswag.eu.databinding.ViewActivityNotificationItemBinding;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/5/6.
 */

public class ActivityNotificationAdapter extends RecyclerView.Adapter<ActivityNotificationAdapter.ActivityHolder>{

    private ArrayList<ActivityNotification> mData = new ArrayList<>();

    public void setmData(ArrayList<ActivityNotification> data){
        mData = data;
    }

    private Activity activity;
    public ActivityNotificationAdapter(Activity activity){
        this.activity = activity;
    }

    @Override
    public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewActivityNotificationItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_activity_notification_item,parent,false);
        ActivityHolder holder = new ActivityHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ActivityHolder holder, int position) {
        holder.getBinding().setActivityNotification(mData.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ActivityHolder extends RecyclerView.ViewHolder{

        public ActivityHolder(View itemView) {
            super(itemView);
        }

        public ViewActivityNotificationItemBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewActivityNotificationItemBinding binding) {
            this.binding = binding;
        }

        private ViewActivityNotificationItemBinding binding;
    }

}

package com.example.asus.shetuan.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.databinding.ViewActivityItemBinding;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


/**
 * Created by ASUS on 2017/2/15.
 */

public class FirstAdapter extends RecyclerView.Adapter<FirstAdapter.ViewHolder>{

    private ArrayList<ActivityMsg> mData = new ArrayList<>();
    public FirstAdapter(ArrayList<ActivityMsg> data){
        mData.addAll(data);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewActivityItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.view_activity_item,parent,false);
        ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.activitymsg,mData.get(position));
        Uri uri = Uri.parse(mData.get(position).getImageurl());
        holder.getBinding().activityItemImage.setImageURI(uri);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewActivityItemBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setBinding(ViewActivityItemBinding binding) {
            this.binding = binding;
        }

        public ViewActivityItemBinding getBinding() {
            return this.binding;
        }
    }
}

package com.example.asus.shetuan.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.shetuan.BR;
import com.example.asus.shetuan.R;
import com.example.asus.shetuan.bean.SearchRecord;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/3/22.
 */

public class SearchRecordAdapter extends RecyclerView.Adapter<SearchRecordAdapter.ViewHolder> {

    private ArrayList<SearchRecord> mData = new ArrayList<>();

    public SearchRecordAdapter(ArrayList<SearchRecord> data){
        mData.addAll(data);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.search_record_item,parent,false);
        ViewHolder holder = new ViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.getBinding().setVariable(BR.searchrecord,mData.get(position));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }

        public ViewDataBinding getBinding() {
            return this.binding;
        }
    }
}

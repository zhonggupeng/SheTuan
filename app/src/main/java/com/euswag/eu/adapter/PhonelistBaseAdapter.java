package com.euswag.eu.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.euswag.eu.bean.ShetuanContacts;
import com.euswag.eu.databinding.ViewPhonelistItemBinding;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/5/2.
 */

public abstract class PhonelistBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<T> mData = new ArrayList<>();
    private Context context;
    private int mLayoutResId = -1;

    public void setmData(ArrayList<T> data){
        mData = data;
    }
    protected abstract void convert(RecyclerView.ViewHolder holder, T item, int indexOfData);

    public PhonelistBaseAdapter(Context context,int layoutResId){
        this.context = context;
        this.mLayoutResId = layoutResId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewPhonelistItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), mLayoutResId,parent,false);
        PhonelistHolder holder = new PhonelistHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        convert(holder,mData.get(position),position);
        ((PhonelistHolder)holder).getBinding().phonelistItemCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ((ShetuanContacts)mData.get(position)).getUid()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent);
            }
        });
        ((PhonelistHolder)holder).getBinding().phonelistItemMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class PhonelistHolder extends RecyclerView.ViewHolder{

        public PhonelistHolder(View itemView) {
            super(itemView);
        }
        private ViewPhonelistItemBinding binding;

        public void setBinding(ViewPhonelistItemBinding binding) {
            this.binding = binding;
        }

        public ViewPhonelistItemBinding getBinding() {
            return this.binding;
        }
    }
}

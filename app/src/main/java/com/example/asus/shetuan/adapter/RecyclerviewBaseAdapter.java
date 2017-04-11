package com.example.asus.shetuan.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.asus.shetuan.R;
import com.example.asus.shetuan.activity.ActivityDetailActivity;
import com.example.asus.shetuan.activity.CheckActivityActivity;
import com.example.asus.shetuan.activity.ShetuanInformationActivity;
import com.example.asus.shetuan.bean.ActivityMsg;
import com.example.asus.shetuan.bean.ShetuanMsg;
import com.example.asus.shetuan.databinding.ViewActivityFooterHolderBinding;
import com.example.asus.shetuan.databinding.ViewActivityItemBinding;

import java.util.ArrayList;

/**
 * Created by ASUS on 2017/4/2.
 */

public abstract class RecyclerviewBaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    protected Context mContext;
    protected int mLayoutResId = -1;
    private final int NORMALLAYOUT = 0;
    private final int FOOTERLAYOUT = 1;
    private final int NORMAL = 110;
    private final int LOADING = 111;
    private final int THEEND = 100;
    private final int LOADERROR =101;
    private int status;

    protected abstract void convert(RecyclerView.ViewHolder holder, T item, int indexOfData);

    private ArrayList<T> mData = new ArrayList<>();
    public FooterHolder footerHolder;
    public RecyclerviewBaseAdapter(Context context, int layoutResId){
        mContext = context;
        mLayoutResId = layoutResId;
    }
    public void setmData(ArrayList<T> data){
        mData = data;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return status;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==NORMALLAYOUT){
            ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), mLayoutResId,parent,false);
            NormalHolder normalHolder = new NormalHolder(binding.getRoot());
            normalHolder.setBinding(binding);
            return normalHolder;
        }
        else {
            ViewActivityFooterHolderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.view_activity_footer_holder,parent,false);
            footerHolder = new FooterHolder(binding.getRoot());
            footerHolder.setBinding(binding);
            return footerHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(position<mData.size()){
            //设置点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mLayoutResId == R.layout.view_activity_item){
                        if (((ActivityMsg)(mData.get(position))).getIsbuild()==0){
                            Intent intent = new Intent(mContext, ActivityDetailActivity.class);
                            intent.putExtra("datajson1",((ActivityMsg)(mData.get(position))).getActivityDetailJsonString());
                            mContext.startActivity(intent);
                        }
                        else {
                            Intent intent = new Intent(mContext, CheckActivityActivity.class);
                            intent.putExtra("datajson2",((ActivityMsg)(mData.get(position))).getActivityDetailJsonString());
                            mContext.startActivity(intent);
                        }
                    }
                    else if (mLayoutResId == R.layout.view_shetuan_item){
                        //不知道要不要设立创建的和加入的
                        Intent intent = new Intent(mContext, ShetuanInformationActivity.class);
                        intent.putExtra("datajson3",((ShetuanMsg)(mData.get(position))).getShetuanJsonString());
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
        return mData.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==mData.size())
            return FOOTERLAYOUT;
        else
            return NORMALLAYOUT;
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
    public class FooterHolder extends RecyclerView.ViewHolder{

        public FooterHolder(View itemView) {
            super(itemView);
        }
        private ViewActivityFooterHolderBinding binding;

        public void setBinding(ViewActivityFooterHolderBinding binding) {
            this.binding = binding;
        }

        public ViewActivityFooterHolderBinding getBinding() {
            return this.binding;
        }

        //根据传过来的status控制哪个状态可见
        public void setData(int status){
            switch (status){
                case NORMAL:
                    setAllGone();
                    break;
                case LOADING:
                    setAllGone();
                    binding.loadViewstub.setVisibility(View.VISIBLE);
                    break;
                case THEEND:
                    setAllGone();
                    binding.endViewstub.setVisibility(View.VISIBLE);
                    break;
                case LOADERROR:
                    setAllGone();
                    binding.loadErrorViewstub.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        public void setAllGone(){
            binding.loadViewstub.setVisibility(View.GONE);
            binding.endViewstub.setVisibility(View.GONE);
            binding.loadErrorViewstub.setVisibility(View.GONE);
        }
    }
}

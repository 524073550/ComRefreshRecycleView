package com.zhuke.comrefreshrecycleview.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Apple on 2016/9/28.
 * 包装的适配器：区分头、脚、正常的数据(返回值是0)
 */
public class XWrapAdapter extends RecyclerView.Adapter{

    //头布局
    private View mHeaderView;
    //脚布局
    private View mFooterView;
    //正常的适配器
    private RecyclerView.Adapter mAdapter;

    public XWrapAdapter(View mHeaderView, View mFooterView, RecyclerView.Adapter mAdapter) {
        this.mHeaderView = mHeaderView;
        this.mFooterView = mFooterView;
        this.mAdapter = mAdapter;
    }

    //处理条目的类型
    @Override
    public int getItemViewType(int position) {
        //头
        if(position == 0){
            return RecyclerView.INVALID_TYPE;//-1
        }
        //正常的布局
        int adjPoistion = position - 1;
        int adapterCount = mAdapter.getItemCount();
        if(adjPoistion < adapterCount){
            return mAdapter.getItemViewType(adjPoistion);
        }
        //脚
        return RecyclerView.INVALID_TYPE - 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == RecyclerView.INVALID_TYPE){
            //头
            return new HeaderViewHolder(mHeaderView);
        }else if(viewType == RecyclerView.INVALID_TYPE - 1){
            //脚
            return new FooterViewHolder(mFooterView);
        }
        //正常
        return mAdapter.onCreateViewHolder(parent,viewType);
    }

    //绑定数据
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //头
        if(position == 0){
            return;
        }
        //正常布局
        int adjPosition = position - 1;
        int adapterCount = mAdapter.getItemCount();
        if(adjPosition < adapterCount){
            mAdapter.onBindViewHolder(holder,adjPosition);
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 2;
    }

    //头的ViewHolder
    static class HeaderViewHolder extends RecyclerView.ViewHolder{

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }
    //脚的ViewHolder
    static class FooterViewHolder extends RecyclerView.ViewHolder{

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }



}

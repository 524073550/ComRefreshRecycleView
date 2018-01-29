package com.zhuke.comrefreshrecycleview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;


/*
 * Created by Apple on 2016/9/28.
 * 上拉下拉加载数据的RecyclerView
 */
public class RefreshRecyclerView extends RecyclerView {


    private View mHead;
    private LinearLayoutManager mManager;

    public RefreshRecyclerView(Context context) {
        this(context, null);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        initHeadView();
    }

    private void initHeadView() {
        mHead = LayoutInflater.from(getContext()).inflate(R.layout.normal_head, null, false);
        mHead.measure(0,0);
        int headMeasuredHeight = mHead.getMeasuredHeight();
        mHead.setPadding(0,-headMeasuredHeight,0,0);
    }

    private LoadMoreWrapper mLoadMoreWrapper;//包装adapter
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;
    @Override
    public void setAdapter(Adapter adapter) {
        adapter = new LoadMoreWrapper(adapter,mHead);
        mLoadMoreWrapper = (LoadMoreWrapper) adapter;
        super.setAdapter(adapter);
    }

    //用来标记是否正在向上滑动
    private boolean isSlidingUpward = false;


    public void setLoadMoreState(int state){
        mLoadMoreWrapper.setLoadState(state);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        isSlidingUpward = t -  oldt> 0;;
    }

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        mManager = (LinearLayoutManager) getLayoutManager();
        // 当不滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示的itemPosition
            int lastItemPosition = mManager.findLastCompletelyVisibleItemPosition();
            int itemCount = mManager.getItemCount();

            // 判断是否滑动到了最后一个item，并且是向上滑动
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                //加载更多
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        mManager = (LinearLayoutManager) layout;
    }

    //加载更多的接口
    public interface OnLoadMoreListener{
        void onLoadMore();
    }
    private OnLoadMoreListener mOnLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener){
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}

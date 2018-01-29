package com.zhuke.comrefreshrecycleview.NoHead;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by 15653 on 2018/1/29.
 */

public class NoHeadRecycleView extends RecyclerView {
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;
    public NoHeadRecycleView(Context context) {
        this(context,null);
    }

    public NoHeadRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NoHeadRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        adapter = new XrWrapperAdapter(adapter);
        super.setAdapter(adapter);
    }

    public void setLoadMoreState(int state){
        ((XrWrapperAdapter) getAdapter()).setLoadState(state);
    }
    //用来标记是否正在向上滑动
    private boolean isSlidingUpward = false;

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
        // 当不滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示的itemPosition
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();

            // 判断是否滑动到了最后一个item，并且是向上滑动
            if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                //加载更多
                if (mOnLoadMoreListener != null) {
                    mOnLoadMoreListener.onLoadMore();
                }
            }
        }
    }


    @Override
    public void onScrolled( int dx, int dy) {
        super.onScrolled( dx, dy);
        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        isSlidingUpward = dy > 0;
    }

    private OnLoadMoreListener mOnLoadMoreListener;
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener){
        this.mOnLoadMoreListener = onLoadMoreListener;
    }
    /**
     * 加载更多回调
     */
   public interface OnLoadMoreListener {
       void onLoadMore();
    }
}

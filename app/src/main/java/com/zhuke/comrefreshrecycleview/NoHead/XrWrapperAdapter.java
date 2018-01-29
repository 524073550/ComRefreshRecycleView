package com.zhuke.comrefreshrecycleview.NoHead;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhuke.comrefreshrecycleview.R;

/**
 * Created by 15653 on 2018/1/23.
 */

public class XrWrapperAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_NORMAL = 1;
    private final int TYPE_FOOT = 2;
    private final int TYPE_HEAD = 3;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;
    private RecyclerView.Adapter mAdapter;
    public XrWrapperAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOT) {
            View footLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_foot, parent, false);
            return new XrWrapperAdapter.FootHolder(footLayout);
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_FOOT) {
            XrWrapperAdapter.FootHolder footHolder = (XrWrapperAdapter.FootHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footHolder.pbLoading.setVisibility(View.VISIBLE);
                    footHolder.tvLoading.setVisibility(View.VISIBLE);
                    footHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    footHolder.pbLoading.setVisibility(View.GONE);
                    footHolder.tvLoading.setVisibility(View.GONE);
                    footHolder.llEnd.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        } else {
            mAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mAdapter.getItemCount()) {
            return TYPE_FOOT;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount() + 1;
    }


    public class FootHolder extends RecyclerView.ViewHolder {
        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        public FootHolder(View itemView) {
            super(itemView);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);

        }
    }

    public class HeadHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public HeadHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.head);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 如果当前是footer的位置，那么该item占据2个单元格，正常情况下占据1个单元格
                    return getItemViewType(position) == TYPE_FOOT ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }
}

package com.zhuke.comrefreshrecycleview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 15653 on 2018/1/23.
 */

public class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<T> mStrings;


    public BaseAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mStrings = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View normalLayout = LayoutInflater.from(mContext).inflate(R.layout.adapter_normal, parent, false);
        return new NormalHolder(normalLayout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        NormalHolder normalHolder = ((NormalHolder) holder);
        normalHolder.mTextView.setText(((String) mStrings.get(position)));
    }


    @Override
    public int getItemCount() {
        return mStrings.size() == 0 ? 0 : mStrings.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {

        private final TextView mTextView;

        public NormalHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.text_view);
        }
    }
}

package com.zhuke.comrefreshrecycleview.NoHead;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.zhuke.comrefreshrecycleview.BaseAdapter;
import com.zhuke.comrefreshrecycleview.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 15653 on 2018/1/29.
 */

public class NoHeadActivity extends AppCompatActivity {

    @BindView(R.id.no_head)
    NoHeadRecycleView mNoHead;
    @BindView(R.id.normal_srl)
    SwipeRefreshLayout mNormalSrl;
    private BaseAdapter<String> loadMoreAdapter;
    private List<String> dataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_head_reclcleview);
        ButterKnife.bind(this);
        init();
    }
    private boolean isLoadMore;
    private void init() {
        // 模拟获取数据
        getData();
        mNormalSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoadMore){
                    getData();
                    loadMoreAdapter.upData(dataList);
                }

            }
        });
        loadMoreAdapter = new BaseAdapter<String>(this, dataList);
//        mWrapper = new LoadMoreWrapper(loadMoreAdapter);
        mNoHead.setLayoutManager(new LinearLayoutManager(this));
        mNoHead.setAdapter(loadMoreAdapter);
        mNoHead.setOnLoadMoreListener(new NoHeadRecycleView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mNoHead.setLoadMoreState(mNoHead.LOADING);
                isLoadMore = true;
                if (dataList.size() < 52) {
                    // 模拟获取网络数据，延时1s
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getData();
                                    mNoHead.setLoadMoreState(mNoHead.LOADING_COMPLETE);
                                    isLoadMore = false;
                                }
                            });
                        }
                    }, 1000);
                } else {
                    // 显示加载到底的提示
                    mNoHead.setLoadMoreState(mNoHead.LOADING_END);
                    isLoadMore = false;
                }
            }
        });
    }

    private void getData() {
        char letter = 'A';
        for (int i = 0; i < 26; i++) {
            dataList.add(String.valueOf(letter));
            letter++;
        }
    }
}
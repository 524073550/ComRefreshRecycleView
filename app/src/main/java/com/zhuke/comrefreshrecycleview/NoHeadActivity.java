package com.zhuke.comrefreshrecycleview;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
    private BaseAdapter<String> loadMoreAdapter;
    private List<String> dataList = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.no_head_reclcleview);
        ButterKnife.bind(this);
        init();
    }

  private void init() {
        // 模拟获取数据
        getData();

        loadMoreAdapter = new BaseAdapter<String>(this,dataList);
//        mWrapper = new LoadMoreWrapper(loadMoreAdapter);
      mNoHead.setLayoutManager(new LinearLayoutManager(this));
      mNoHead.setAdapter(loadMoreAdapter);
      mNoHead.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mRecyclerView.setLoadMoreState(mRecyclerView.LOADING);

                if (dataList.size() < 52) {
                    // 模拟获取网络数据，延时1s
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    getData();
                                    mRecyclerView.setLoadMoreState(mRecyclerView.LOADING_COMPLETE);
                                }
                            });
                        }
                    }, 1000);
                } else {
                    // 显示加载到底的提示
                    mRecyclerView.setLoadMoreState(mRecyclerView.LOADING_END);
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
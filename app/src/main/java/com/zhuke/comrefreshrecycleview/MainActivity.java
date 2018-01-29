package com.zhuke.comrefreshrecycleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private RefreshRecyclerView mRecyclerView;
    private List<String> dataList = new ArrayList<>();
    private BaseAdapter<String> loadMoreAdapter;
    private LoadMoreWrapper mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mRecyclerView = (RefreshRecyclerView) findViewById(R.id.recycle);
        // 模拟获取数据
        getData();

        loadMoreAdapter = new BaseAdapter<String>(this,dataList);
//        mWrapper = new LoadMoreWrapper(loadMoreAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(loadMoreAdapter);
        mRecyclerView.setOnLoadMoreListener(new RefreshRecyclerView.OnLoadMoreListener() {
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

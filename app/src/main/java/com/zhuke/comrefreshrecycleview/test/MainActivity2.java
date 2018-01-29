/*
package com.zhuke.comrefreshrecycleview.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.zhuke.comrefreshrecycleview.BaseAdapter;
import com.zhuke.comrefreshrecycleview.LoadMoreWrapper;
import com.zhuke.comrefreshrecycleview.R;
import com.zhuke.comrefreshrecycleview.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity2 extends AppCompatActivity {

    private XRefreshRecyclerView mRecyclerView;
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
        mRecyclerView = (XRefreshRecyclerView) findViewById(R.id.recycle);
        // 模拟获取数据
        getData();

        loadMoreAdapter = new BaseAdapter<String>(this,dataList);
//        mWrapper = new LoadMoreWrapper(loadMoreAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(loadMoreAdapter);
        BannerView bannerView = new BannerView(this);
//        bannerView.setImageUrls();
        bannerView.setAutoPlay(true);
        mRecyclerView.addSwitchImageView(bannerView);
        mRecyclerView.setOnRefreshListener(new XRefreshRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();

            }
        });
        mRecyclerView.setOnLoadMoreListener(new XRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mRecyclerView.hideHeaderView(false);

                            }
                        });
                    }
                }, 1000);
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
*/

package com.zhuke.comrefreshrecycleview.NoHead;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.zhuke.comrefreshrecycleview.BaseAdapter;
import com.zhuke.comrefreshrecycleview.LoadMoreWrapper;
import com.zhuke.comrefreshrecycleview.R;
import com.zhuke.comrefreshrecycleview.RefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.normal)
    Button mNormal;
    private RefreshRecyclerView mRecyclerView;
    private List<String> dataList = new ArrayList<>();
    private BaseAdapter<String> loadMoreAdapter;
    private LoadMoreWrapper mWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.normal)
    public void onViewClicked() {
        startActivity(new Intent(this,NoHeadActivity.class));
    }
}

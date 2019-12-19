package com.winnie.views.loadmorerecyclerview.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.winnie.views.loadmorerecyclerview.R;
import com.winnie.views.loadmorerecyclerview.adapter.LoadMoreInnerAdapter;
import com.winnie.views.loadmorerecyclerview.model.RecyclerActivityData;
import com.winnie.views.loadmorerecyclerview.view.LoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : winnie
 * @date : 2019/12/19
 * @desc 使用包装的Adapter实现上拉加载,用户的Adapter继承自RecyclerView.Adapter即可
 */
public class LoadMoreActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    LoadMoreRecyclerView mRecyclerView;

    private LoadMoreInnerAdapter mAdapter;
    private int loadMoreCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_more1);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        List<RecyclerActivityData> dataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataList.add(new RecyclerActivityData("Activity" + i));
        }
        mAdapter = new LoadMoreInnerAdapter(dataList);
        mAdapter.setOnItemClickListener((pos, data) -> {
            if (data.getActivity() != null) {
                startActivity(new Intent(LoadMoreActivity.this, data.getActivity()));
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setTotalNum(30);
        mRecyclerView.setOnLoadMoreListener(view -> loadMoreData());
    }

    private void loadMoreData() {
        mRecyclerView.postDelayed(() -> {
            for (int i = 0; i < 10; i++) {
                loadMoreCount++;
                mAdapter.addData(new RecyclerActivityData("AddData" + loadMoreCount));
            }

            mAdapter.notifyDataSetChanged();
        }, 1000);
    }
}

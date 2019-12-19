package com.winnie.views.loadmorerecyclerview;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winnie.views.loadmorerecyclerview.activity.LoadMoreActivity;
import com.winnie.views.loadmorerecyclerview.activity.SimpleLoadMoreActivity;
import com.winnie.views.loadmorerecyclerview.adapter.SimpleLoadMoreAdapter;
import com.winnie.views.loadmorerecyclerview.constant.AdapterConstant;
import com.winnie.views.loadmorerecyclerview.model.RecyclerActivityData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author winnie
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private SimpleLoadMoreAdapter mAdapter;

    private int loadMoreCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        List<RecyclerActivityData> dataList = new ArrayList<>();
        dataList.add(new RecyclerActivityData("抽象Adapter的上拉加载", SimpleLoadMoreActivity.class));
        dataList.add(new RecyclerActivityData("包装Adapter的上拉加载", LoadMoreActivity.class));
        for (int i = 0; i < 20; i++) {
            dataList.add(new RecyclerActivityData("Activity" + i));
        }
        mAdapter = new SimpleLoadMoreAdapter(dataList, 100);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //用来标记是否正在向上滑动
            private boolean isSlidingUpward = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当不滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的itemPosition
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = manager.getItemCount();

                    // 判断是否滑动到了最后一个item，并且是向上滑动
                    if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                        mAdapter.setLoadState(AdapterConstant.LOADING);
                        //加载更多
                        mRecyclerView.postDelayed(() -> loadMoreData(), 1000);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
                isSlidingUpward = dy > 0;
            }
        });

        mAdapter.setOnItemClickListener((pos, data) -> {
            if(data.getActivity()!= null){
                startActivity(new Intent(MainActivity.this, data.getActivity()));
            }
        });
    }

    private void loadMoreData() {
        loadMoreCount++;

        mAdapter.addData(new RecyclerActivityData("AddData" + loadMoreCount));
        mAdapter.notifyDataSetChanged();
        mAdapter.setLoadState(AdapterConstant.LOADING_COMPLETE);
    }
}

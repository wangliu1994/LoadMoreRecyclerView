package com.winnie.views.loadmorerecyclerview.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winnie.views.loadmorerecyclerview.adapter.BaseLoadMoreAdapter;
import com.winnie.views.loadmorerecyclerview.constant.AdapterConstant;

/**
 * @author : winnie
 * @date : 2019/12/18
 * @desc 下拉加载的RecyclerView
 */
public class SimpleLoadMoreRecyclerView extends RecyclerView {
    private OnLoadMoreListener mOnLoadMoreListener;

    public SimpleLoadMoreRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public SimpleLoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SimpleLoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        if(isInEditMode()){
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        setLayoutManager(manager);
        addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                       loadMore();
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
    }

    /**
     * 上拉加载
     */
    private void loadMore(){
        if (getLoadMoreAdapter().getLoadState() != AdapterConstant.LOADING_COMPLETE) {
            return;
        }

        getLoadMoreAdapter().setLoadState(AdapterConstant.LOADING);
        if(mOnLoadMoreListener != null){
            mOnLoadMoreListener.onLoadMore(this);
        }
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if(!(adapter instanceof BaseLoadMoreAdapter)){
            throw new RuntimeException("必须使用BaseLoadMoreAdapter");
        }
        super.setAdapter(adapter);
    }

    public BaseLoadMoreAdapter getLoadMoreAdapter() {
        Adapter adapter = super.getAdapter();
        if(!(adapter instanceof BaseLoadMoreAdapter)){
            throw new RuntimeException("必须使用BaseLoadMoreAdapter");
        }
        return (BaseLoadMoreAdapter) adapter;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * 上拉加载监听
     */
    public interface OnLoadMoreListener {

        /**
         * 触发上拉加载
         * @param recyclerView view
         */
        void onLoadMore(SimpleLoadMoreRecyclerView recyclerView);
    }
}

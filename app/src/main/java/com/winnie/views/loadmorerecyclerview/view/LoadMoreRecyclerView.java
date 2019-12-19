package com.winnie.views.loadmorerecyclerview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.winnie.views.loadmorerecyclerview.R;
import com.winnie.views.loadmorerecyclerview.constant.AdapterConstant;

/**
 * @author : winnie
 * @date : 2019/12/18
 * @desc 使用包装的Adapter实现上拉加载,用户的Adapter继承自RecyclerView.Adapter即可
 */
public class LoadMoreRecyclerView extends RecyclerView {
    private final AdapterDataObserver mDataObserver = new DataObserver();

    private OnLoadMoreListener mOnLoadMoreListener;

    private LoadMoreAdapterWrapper mWrapperAdapter;

    public LoadMoreRecyclerView(@NonNull Context context) {
        super(context);
        initView();
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadMoreRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        setLayoutManager(manager);
        addOnScrollListener(new OnScrollListener() {
            //用来标记是否正在向上滑动
            private boolean isSlidingUpward = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 当不滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的itemPosition
                    int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                    int itemCount = mWrapperAdapter == null? 0: mWrapperAdapter.getItemCount();

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
    private void loadMore() {
        if (mWrapperAdapter.getLoadState() != AdapterConstant.LOADING_COMPLETE) {
            return;
        }
        mWrapperAdapter.setLoadState(AdapterConstant.LOADING);
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore(this);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapperAdapter = new LoadMoreAdapterWrapper(adapter);
        super.setAdapter(mWrapperAdapter);
//        adapter.registerAdapterDataObserver(this.mDataObserver);
//        this.mDataObserver.onChanged();
    }

    @Nullable
    @Override
    public Adapter getAdapter() {
        if (mWrapperAdapter == null) {
            return null;
        }
        return mWrapperAdapter.getInnerAdapter();
    }

    public void setTotalNum(int totalNum){
        mWrapperAdapter.setTotalNum(totalNum);
    }

    /**
     * 加载完成
     */
    public void loadingComplete() {
        if (mWrapperAdapter == null) {
            return;
        }
        mWrapperAdapter.setLoadState(AdapterConstant.LOADING_COMPLETE);
    }

    /**
     * 加载到底,没有更多
     */
    public void loadingEnd() {
        if (mWrapperAdapter == null) {
            return;
        }
        mWrapperAdapter.setLoadState(AdapterConstant.LOADING_END);
    }

    /**
     * 只有一页数据，没有更多
     */
    public void loadingNoMore() {
        if (mWrapperAdapter == null) {
            return;
        }
        mWrapperAdapter.setLoadState(AdapterConstant.LOADING_NO_MORE);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    /**
     * 数据监听
     */
    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
//            mInnerAdapter.addMoreData();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
        }
    }


    /**
     * 对用户设置的Adapter进行包装
     */
    private class LoadMoreAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        /**
         * 真正用户设置的Adapter
         */
        private RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;

        /**
         * 当前加载状态，默认为加载完成
         */
        private int mLoadState = AdapterConstant.LOADING_NO_MORE;

        /**
         * 数据总条数
         */
        private int mTotalNum;

        private LoadMoreAdapterWrapper(Adapter<RecyclerView.ViewHolder> adapter) {
            this.mInnerAdapter = adapter;
        }

        private Adapter getInnerAdapter() {
            return mInnerAdapter;
        }

        @Override
        public int getItemViewType(int position) {
            // 最后一个item设置为FooterView
            if (position + 1 == getItemCount()) {
                return AdapterConstant.TYPE_FOOTER;
            } else {
                return AdapterConstant.TYPE_ITEM;
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //进行判断显示类型，来创建返回不同的View
            if (viewType == AdapterConstant.TYPE_FOOTER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_refresh_footer, parent, false);
                return new FootViewHolder(view);
            }
            return mInnerAdapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof FootViewHolder) {
                FootViewHolder footViewHolder = (FootViewHolder) holder;
                switch (mLoadState) {
                    case AdapterConstant.LOADING:
                        footViewHolder.tvLoadingState.setText("正在加载");
                        footViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case AdapterConstant.LOADING_COMPLETE:
                        footViewHolder.tvLoadingState.setText("加载完成");
                        footViewHolder.mProgressBar.setVisibility(View.GONE);
                        break;
                    case AdapterConstant.LOADING_END:
                        footViewHolder.tvLoadingState.setText("加载到底");
                        footViewHolder.mProgressBar.setVisibility(View.GONE);
                        break;
                    case AdapterConstant.LOADING_NO_MORE:
                    default:
                        footViewHolder.tvLoadingState.setVisibility(View.GONE);
                        footViewHolder.mProgressBar.setVisibility(View.GONE);
                        break;
                }
            } else {
                mInnerAdapter.onBindViewHolder(holder, position);
            }
        }

        @Override
        public int getItemCount() {
            if (mInnerAdapter == null) {
                return 0;
            }
            return mInnerAdapter.getItemCount() + 1;
        }

        /**
         * 设置上拉加载状态
         *
         * @param loadState 0.正在加载 1.加载完成 2.加载到底
         */
        private void setLoadState(int loadState) {
            this.mLoadState = loadState;
            notifyDataSetChanged();
        }

        private int getLoadState() {
            return mLoadState;
        }

        private void setTotalNum(int totalNum) {
            mTotalNum = totalNum;
            if (mInnerAdapter.getItemCount() >= mTotalNum) {
                setLoadState(AdapterConstant.LOADING_NO_MORE);
            } else {
                setLoadState(AdapterConstant.LOADING_COMPLETE);
            }
            notifyDataSetChanged();
        }

        private void addData(){
            mTotalNum++;
            if (mInnerAdapter.getItemCount() >= mTotalNum) {
                setLoadState(AdapterConstant.LOADING_NO_MORE);
            } else {
                setLoadState(AdapterConstant.LOADING_COMPLETE);
            }
            notifyDataSetChanged();
        }

        /**
         * 上拉加载footer布局
         */
        private class FootViewHolder extends RecyclerView.ViewHolder {
            TextView tvLoadingState;
            ProgressBar mProgressBar;

            FootViewHolder(View itemView) {
                super(itemView);
                tvLoadingState = itemView.findViewById(R.id.tv_loading_state);
                mProgressBar = itemView.findViewById(R.id.progress_bar);
            }
        }
    }

    /**
     * 上拉加载监听
     */
    public interface OnLoadMoreListener {

        /**
         * 触发上拉加载
         *
         * @param recyclerView view
         */
        void onLoadMore(LoadMoreRecyclerView recyclerView);
    }
}

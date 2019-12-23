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
import com.winnie.views.loadmorerecyclerview.constant.LoadMoreState;

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
        if (mWrapperAdapter.getLoadState() != LoadMoreState.LOADING_COMPLETE) {
            return;
        }
        mWrapperAdapter.setLoadState(LoadMoreState.LOADING);
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.onLoadMore(this);
        }
    }

    /**
     * 设置列表总数，用于判断是否还有未加载出来的数据
     * @param totalNum 总数
     */
    public void setTotalNum(int totalNum){
        mWrapperAdapter.setTotalNum(totalNum);
    }

    /**
     * 上拉加载监听
     * @param onLoadMoreListener 监听
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        mOnLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mWrapperAdapter = new LoadMoreAdapterWrapper(adapter);
        super.setAdapter(mWrapperAdapter);
        adapter.registerAdapterDataObserver(this.mDataObserver);
        this.mDataObserver.onChanged();
    }

    @Nullable
    @Override
    public Adapter getAdapter() {
        if (mWrapperAdapter == null) {
            return null;
        }
        return mWrapperAdapter.getInnerAdapter();
    }



    /**
     * 数据变动监听，innerAdapter通过DataObserver通知wrapperAdapter
     */
    private class DataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mWrapperAdapter.notifyDataSetChanged();
            mWrapperAdapter.dataNumChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            mWrapperAdapter.notifyItemRangeInserted(positionStart, itemCount);
            mWrapperAdapter.dataNumChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            mWrapperAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            mWrapperAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            mWrapperAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            mWrapperAdapter.dataNumChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            mWrapperAdapter.notifyItemMoved(fromPosition, toPosition);
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
        private LoadMoreState mLoadState = LoadMoreState.LOADING_NO_MORE;

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
                    case LOADING:
                        footViewHolder.tvLoadingState.setText("正在加载");
                        footViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                        break;
                    case LOADING_COMPLETE:
                        footViewHolder.tvLoadingState.setText("加载完成");
                        footViewHolder.mProgressBar.setVisibility(View.GONE);
                        break;
                    case LOADING_END:
                        footViewHolder.tvLoadingState.setText("加载到底");
                        footViewHolder.mProgressBar.setVisibility(View.GONE);
                        break;
                    case LOADING_NO_MORE:
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
        private void setLoadState(LoadMoreState loadState) {
            this.mLoadState = loadState;
            notifyDataSetChanged();
        }

        private LoadMoreState getLoadState() {
            return mLoadState;
        }

        /**
         * 设置列表总数，用于判断是否还有未加载出来的数据
         * @param totalNum 总数
         */
        private void setTotalNum(int totalNum) {
            mTotalNum = totalNum;
            if (mInnerAdapter.getItemCount() >= mTotalNum) {
                setLoadState(LoadMoreState.LOADING_NO_MORE);
            } else {
                setLoadState(LoadMoreState.LOADING_COMPLETE);
            }
            notifyDataSetChanged();
        }

        /**
         * 列表数量变化，可能会影响mTotalNum，需要做处理
         */
        private void dataNumChanged() {
            if (mInnerAdapter.getItemCount() >= mTotalNum) {
                setLoadState(LoadMoreState.LOADING_END);
            } else {
                setLoadState(LoadMoreState.LOADING_COMPLETE);
            }
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

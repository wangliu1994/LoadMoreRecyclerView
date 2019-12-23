package com.winnie.views.loadmorerecyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winnie.views.loadmorerecyclerview.R;
import com.winnie.views.loadmorerecyclerview.constant.AdapterConstant;
import com.winnie.views.loadmorerecyclerview.constant.LoadMoreState;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : winnie
 * @date : 2019/12/19
 * @desc 搜索Adapter，实现的上拉加载更多逻辑
 */
public abstract class BaseLoadMoreAdapter<K, Y extends
        RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 当前加载状态，默认为加载完成
     */
    private LoadMoreState mLoadState = LoadMoreState.LOADING_COMPLETE;

    /**
     * 当前的数量
     */
    private int mCurrentNum;

    /**
     * 数据总条数
     */
    private int mTotalNum;

    public BaseLoadMoreAdapter(int currentNum, int totalNum) {
        this.mCurrentNum = currentNum;
        this.mTotalNum = totalNum;
        if (mCurrentNum >= mTotalNum + 1) {
            setLoadState(LoadMoreState.LOADING_NO_MORE);
        } else {
            setLoadState(LoadMoreState.LOADING_COMPLETE);
        }
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
        } else if (viewType == AdapterConstant.TYPE_ITEM) {
            return createItemViewHolder(parent, viewType);
        } else {
            return createItemViewHolder(parent, viewType);
        }
    }

    /**
     * 创建列表项
     *
     * @param parent   xxx
     * @param viewType xxx
     * @return xxx
     */
    protected abstract Y createItemViewHolder(ViewGroup parent, int viewType);

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
            bindItemViewHolder(holder, position);
        }
    }

    /**
     * 创建列表项
     *
     * @param holder   xxx
     * @param position xxx
     */
    public abstract void bindItemViewHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * 获取列表数据总数
     * @return 列表数据总数
     */
    public abstract int getDataListCount();

    @Override
    public int getItemCount() {
        return getDataListCount() + 1;
    }

    /**
     * 列表数量变化，可能会影响mTotalNum，需要做处理
     */
    public void dataNumChanged() {
        if (getDataListCount() == mCurrentNum) {
            return;
        }
        mCurrentNum = getDataListCount();
        if (getItemCount() > mTotalNum) {
            setLoadState(LoadMoreState.LOADING_END);
        } else {
            setLoadState(LoadMoreState.LOADING_COMPLETE);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(LoadMoreState loadState) {
        this.mLoadState = loadState;
        notifyDataSetChanged();
    }

    public LoadMoreState getLoadState() {
        return mLoadState;
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {
        TextView tvLoadingState;
        ProgressBar mProgressBar;

        FootViewHolder(View itemView) {
            super(itemView);
            tvLoadingState = itemView.findViewById(R.id.tv_loading_state);
            mProgressBar = itemView.findViewById(R.id.progress_bar);
        }
    }
}

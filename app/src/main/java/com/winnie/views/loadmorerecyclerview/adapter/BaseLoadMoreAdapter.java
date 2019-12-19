package com.winnie.views.loadmorerecyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.winnie.views.loadmorerecyclerview.R;
import com.winnie.views.loadmorerecyclerview.constant.AdapterConstant;

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
    private int mLoadState = AdapterConstant.LOADING_COMPLETE;

    protected Context mContext;
    protected List<K> mData;

    /**
     * 数据总条数
     */
    protected int mTotalNum;

    public BaseLoadMoreAdapter(Context context, List<K> data, int totalNum) {
        mContext = context;
        mData = data;
        if (mData == null) {
            mData = new ArrayList<>();
        }
        this.mTotalNum = totalNum;
        if (mData.size() >= mTotalNum) {
            setLoadState(AdapterConstant.LOADING_NO_MORE);
        } else {
            setLoadState(AdapterConstant.LOADING_COMPLETE);
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
        }else {
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
            K itemData = mData.get(position);
            holder.itemView.setOnClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(itemData, position);
                }
            });
            holder.itemView.setOnLongClickListener(v -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(holder.itemView, itemData, position);
                    return true;
                }
                return false;
            });
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

    @Override
    public int getItemCount() {
        if (mData == null || mData.isEmpty()) {
            return 0;
        }
        return mData.size() + 1;
    }

    public <E extends K> void addMoreData(List<E> data, int totalNum) {
        if (data != null && !data.isEmpty()) {
            mData.addAll(data);
        }
        this.mTotalNum = totalNum;
        if (mData.size() >= mTotalNum) {
            setLoadState(AdapterConstant.LOADING_END);
        } else {
            setLoadState(AdapterConstant.LOADING_COMPLETE);
        }
        notifyDataSetChanged();
    }

    public <E extends K> void removeData(E data) {
        if (data != null) {
            mData.remove(data);
        }
        mTotalNum = mTotalNum - 1;
        notifyDataSetChanged();
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.mLoadState = loadState;
        notifyDataSetChanged();
    }

    public int getLoadState() {
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


    protected OnItemClickListener<K> mOnItemClickListener;

    public interface OnItemClickListener<T> {
        /**
         * item被点击
         *
         * @param itemData item的数据
         * @param pos      item的位置
         */
        void onItemClick(T itemData, int pos);

        /**
         * item被长按点击
         *
         * @param parent   xxx
         * @param itemData item的数据
         * @param pos      item的位置
         */
        void onItemLongClick(View parent, T itemData, int pos);
    }
}

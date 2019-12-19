package com.winnie.views.loadmorerecyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.winnie.views.loadmorerecyclerview.R;
import com.winnie.views.loadmorerecyclerview.model.RecyclerActivityData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author : winnie
 * @date : 2019/12/18
 * @desc
 */
public class SimpleLoadMoreAdapter extends BaseLoadMoreAdapter<RecyclerActivityData, RecyclerView.ViewHolder> {
    private List<RecyclerActivityData> mDataList;
    private OnItemClickListener mOnItemClickListener;

    public SimpleLoadMoreAdapter(Context context, List<RecyclerActivityData> data, int totalNum) {
        super(context, data, totalNum);
        mDataList = data;
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
    }

    @Override
    protected RecyclerView.ViewHolder createItemViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SimpleLoadMoreAdapter.ViewHolder) {
            SimpleLoadMoreAdapter.ViewHolder viewHolder = (ViewHolder) holder;
            final RecyclerActivityData data = mDataList.get(position);
            viewHolder.mTextView.setText(data.getText());
            viewHolder.itemView.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position, data);
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void addData(RecyclerActivityData data){
        mDataList.add(data);
        mData = mDataList;
    }

    public void setDataList(List<RecyclerActivityData> dataList) {
        mDataList = dataList;
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        mData = mDataList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view)
        TextView mTextView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnItemClickListener {

        /**
         * xxx
         *
         * @param pos  xxx
         * @param data xxx
         */
        void onItemClick(int pos, RecyclerActivityData data);
    }
}

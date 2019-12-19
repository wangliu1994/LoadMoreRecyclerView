package com.winnie.views.loadmorerecyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
 * @desc 使用包装的Adapter实现上拉加载,用户的Adapter继承自RecyclerView.Adapter即可
 */
public class LoadMoreInnerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<RecyclerActivityData> mDataList;
    private OnItemClickListener mOnItemClickListener;

    public LoadMoreInnerAdapter(List<RecyclerActivityData> data) {
        mDataList = data;
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LoadMoreInnerAdapter.ViewHolder) {
            LoadMoreInnerAdapter.ViewHolder viewHolder = (ViewHolder) holder;
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
    }

    public void setDataList(List<RecyclerActivityData> dataList) {
        mDataList = dataList;
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
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

package com.winnie.views.loadmorerecyclerview.constant;

/**
 * @author : winnie
 * @date : 2019/12/23
 * @desc
 */
public enum LoadMoreState {
    /**
     * 正在加载
     */
    LOADING(1),
    /**
     * 加载完成
     */
    LOADING_COMPLETE(2),
    /**
     * 加载到底,没有更多
     */
    LOADING_END(3),
    /**
     * 只有一页数据，没有更多
     */
    LOADING_NO_MORE(4);


    private int mIntValue;

    LoadMoreState(int state) {
        this.mIntValue = state;
    }

    int getIntValue() {
        return mIntValue;
    }
}

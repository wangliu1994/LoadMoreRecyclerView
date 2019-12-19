package com.winnie.views.loadmorerecyclerview.constant;

/**
 * @author : winnie
 * @date : 2019/2/1
 * @desc
 */
public class AdapterConstant {
    private AdapterConstant() {
    }

    /**
     * 普通布局
     */
    public static final int TYPE_ITEM = 1;

    /**
     * 脚布局（上拉加载布局）
     */
    public static final int TYPE_FOOTER = 99;

    /**
     * 正在加载
     */
    public static final int LOADING = 1;
    /**
     * 加载完成
     */
    public static final int LOADING_COMPLETE = 2;
    /**
     * 加载到底,没有更多
     */
    public static final int LOADING_END = 3;
    /**
     * 只有一页数据，没有更多
     */
    public static final int LOADING_NO_MORE = 4;
}

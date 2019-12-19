package com.winnie.views.loadmorerecyclerview.model;

/**
 * @author : winnie
 * @date : 2019/12/18
 * @desc
 */
public class RecyclerActivityData {
    private String mText;
    private Class mActivity;

    public RecyclerActivityData(String text) {
        mText = text;
    }

    public RecyclerActivityData(String text, Class activity) {
        mText = text;
        mActivity = activity;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public Class getActivity() {
        return mActivity;
    }

    public void setActivity(Class activity) {
        mActivity = activity;
    }
}

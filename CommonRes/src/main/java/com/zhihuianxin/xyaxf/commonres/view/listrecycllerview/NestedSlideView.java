package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

/**
 * Created by Stm1992 on 2017/12/24.
 */

/**
 * 刷新视图上下左右的下拉视图需要继承SlideView
 */
public class NestedSlideView extends NestedScrollView implements SlideView {

    private CrossSlideLayout.SlideManager slideManager;
    protected int mOffset = 0;

    public NestedSlideView(@NonNull Context context) {
        super(context);
    }

    public NestedSlideView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public NestedSlideView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 当SlideView被添加到视图中
     *
     * @param maybeMaxOffset
     */
    public void onAttachToParent(int maybeMaxOffset) {

    }

    /**
     * 每次拉动时调用
     */
    public void onSlideStart() {
    }

    /**
     * 拉动过程中调用
     *
     * @param offset
     * @param offsetChanged
     */
    public void onSlideOffsetChanged(int offset, int offsetChanged, boolean isPerform) {
        mOffset = offset;
    }

    /**
     * 拉动结束时调用
     */
    public void onSlideStop() {
    }

    /**
     * 返回当前的视图的距离
     *
     * @return
     */
    public int getOffset() {
        return mOffset;
    }

    /**
     * 返回最大滑动距离，返回-1则不做限制
     *
     * @return
     */
    public int getMaxOffset() {
        return ((CrossSlideLayout.LayoutParams) getLayoutParams()).getMaxOffset();
    }

    /**
     * 返回最小offset，返回不能小于0
     *
     * @return
     */
    public int getMinOffset() {
        return ((CrossSlideLayout.LayoutParams) getLayoutParams()).getMinOffset();
    }


    public CrossSlideLayout.SlideManager getSlideManager() {
        return slideManager;
    }

    @Override
    public void setSlideManager(CrossSlideLayout.SlideManager slideManager) {
        this.slideManager = slideManager;
    }
}

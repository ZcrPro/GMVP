package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;

/**
 * Created by Administrator on 2017/12/25.
 */

public interface SlideView {
    /**
     * 当SlideView被添加到视图中
     *
     * @param maybeMaxOffset
     */
    void onAttachToParent(int maybeMaxOffset);

    /**
     * 每次拉动时调用
     */
    void onSlideStart();

    /**
     * 拉动过程中调用
     *
     * @param offset
     * @param offsetChanged
     */
    void onSlideOffsetChanged(int offset, int offsetChanged, boolean isPerform);

    /**
     * 拉动结束时调用
     */
    void onSlideStop();

    /**
     * 返回当前的视图的距离
     *
     * @return
     */
    int getOffset();

    /**
     * 返回最大滑动距离，返回-1则不做限制
     *
     * @return
     */
    int getMaxOffset();

    /**
     * 返回最小offset，返回不能小于0
     *
     * @return
     */
    int getMinOffset();


    CrossSlideLayout.SlideManager getSlideManager();

    void setSlideManager(CrossSlideLayout.SlideManager slideManager);
}

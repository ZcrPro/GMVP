package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview.rvadapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class RViewItem<T> {
    /**
     * get {@link android.support.v7.widget.RecyclerView} item layout id；
     *
     * @return
     */
    abstract public int getItemLayout();

    /**
     * 生成视图view对象
     *
     * @param context
     * @param parent
     * @param layoutId
     * @return
     */
    protected View generateItemView(Context context, ViewGroup parent, int layoutId) {
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    /**
     * 生成{@link RViewHolder}對象
     * @param context
     * @param parent
     * @return
     */
    protected RViewHolder generateDefaultRViewHolder(Context context, ViewGroup parent) {
        return new RViewHolder(generateItemView(context,parent,getItemLayout()),this);
    }

    /**
     * 根据数据和位置，判断当前数据是否和该布局匹配
     *
     * @param entity   数据
     * @param position 位置
     * @return
     */
    abstract public boolean isItemView(Object entity, int position);

    /**
     * @param holder
     * @param entity
     * @param position
     * @param bundle
     */
    abstract public void bindViewHolder(RViewHolder holder, T entity, int position, Bundle bundle);

    /**
     * 在{@link RViewAdapter}创建了{@link RViewHolder}实例以后回调
     *
     * @param holder
     * @param bundle
     */
    public void onViewHolderCreated(@NonNull RViewHolder holder, Bundle bundle) {

    }

    /**
     * 即当适配器创建的view（即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用。
     *
     * @param holder
     * @param bundle
     */
    public void onViewAttachedToWindow(@NonNull RViewHolder holder, Bundle bundle) {

    }

    /**
     * 即当列表项出现到可视界面的时候调用。
     *
     * @param holder
     * @param bundle
     */
    public void onViewDetachedFromWindow(@NonNull RViewHolder holder, Bundle bundle) {

    }

    /**
     * 当Item被回收的时候调用
     *
     * @param holder
     */
    public void onViewRecycled(@NonNull RViewHolder holder) {

    }
}

package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview.rvadapter;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.ViewGroup;

public class RViewItemManager<T> {
    private SparseArray<RViewItem<T>> mRViewItems;
    private Bundle mBundle;

    public Bundle getBundle() {
        if (mBundle != null)
            mBundle = new Bundle();
        return mBundle;
    }

    public RViewItemManager() {
        mRViewItems = new SparseArray<>();
    }

    /**
     * 添加item的布局类型，{@link RViewItem}
     *
     * @param item
     */
    public void add(RViewItem<T> item) {
        if (item != null && mRViewItems.indexOfValue(item) == -1)
            mRViewItems.put(mRViewItems.size(), item);
    }

    /**
     * 删除所有的{@link RViewItem}实例
     */
    public void clear() {
        mRViewItems.clear();
    }

    /**
     * 根据数据源和位置获取所对应的itemViewType
     *
     * @param entity
     * @param position
     * @return
     */
    public int getItemViewType(T entity, int position) {
        for (int i = 0; i < mRViewItems.size(); i++) {
            if (mRViewItems.valueAt(i).isItemView(entity, position))
                return mRViewItems.keyAt(i);
        }
        throw new RuntimeException("Not found RViewItem to handle the entity:" + entity.getClass().getName());
    }

    /**
     * 通过布局创建{@link RViewHolder}对象
     *
     * @param parent
     * @param viewType
     * @return
     */
    public RViewHolder createViewHolder(ViewGroup parent, int viewType) {
        RViewItem<T> rViewItem = getRViewItem(viewType);
        //生成布局View对象
        RViewHolder viewHolder = rViewItem.generateDefaultRViewHolder(parent.getContext(),parent);
        //通知生命周期
        rViewItem.onViewHolderCreated(viewHolder, getBundle());
        return viewHolder;
    }

    /**
     * @param holder
     * @param entity
     * @param position
     */
    public void bindViewHolder(RViewHolder holder, T entity, int position) {
        RViewItem<T> rViewItem = getRViewItem(getItemViewType(entity, position));
        rViewItem.bindViewHolder(holder, entity, position, getBundle());
    }


    /**
     * 根据viewType获取{@link RViewItem}对象
     *
     * @param viewType
     * @return
     */
    public RViewItem<T> getRViewItem(int viewType) {
        return mRViewItems.get(viewType);
    }

    /**
     * 即当适配器创建的view（即列表项view）被窗口分离（即滑动离开了当前窗口界面）就会被调用。
     *
     * @param holder
     */
    public void onViewAttachedToWindow(RViewHolder holder) {
        holder.getRViewItem().onViewAttachedToWindow(holder, getBundle());
    }

    /**
     * 即当列表项出现到可视界面的时候调用。
     *
     * @param holder
     */
    public void onViewDetachedFromWindow(RViewHolder holder) {
        holder.getRViewItem().onViewDetachedFromWindow(holder, getBundle());

    }

    /**
     * 当Item被回收的时候调用
     *
     * @param holder
     */
    public void onViewRecycled(RViewHolder holder) {
        holder.getRViewItem().onViewRecycled(holder);
    }


}

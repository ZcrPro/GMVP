package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview.rvadapter;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.zhihuianxin.xyaxf.commonres.view.listrecycllerview.ListRecyclerView;

import java.util.ArrayList;
import java.util.List;



public class RViewAdapter<T> extends ListRecyclerView.BaseAdapter<RViewHolder> {
    /**
     * 数据源
     */
    private List<T> data = new ArrayList<>();
    private RViewItemManager<T> mRViewItemManager = new RViewItemManager<>();

    public RViewAdapter() {
    }

    public RViewAdapter(RViewItem<T>... items) {
        if (items != null && items.length > 0) {
            for (int i = 0; i < items.length; i++)
                mRViewItemManager.add(items[i]);
        }
    }


    /**
     * 添加布局样式item
     *
     * @param item
     */
    public void addRViewItemStyle(RViewItem<T> item) {
        mRViewItemManager.add(item);
    }

    /**
     * 获取根据索引获取数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return data.get(position);
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setData(List<T> data) {
        if (data != null) {
            this.data.addAll(data);
        }
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public List<T> getData() {
        return data;
    }

    /**
     * 获取adapter自定义参数,自定义可以在{@link RViewItem}中使用，改变布局的状态
     *
     * @return
     */
    public final Bundle getBundle() {
        return mRViewItemManager.getBundle();
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return mRViewItemManager.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder holder, int position) {
        mRViewItemManager.bindViewHolder(holder, getItem(position), position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mRViewItemManager.getItemViewType(getItem(position), position);
    }


    @CallSuper
    @Override
    public void onViewAttachedToWindow(@NonNull RViewHolder holder) {
        mRViewItemManager.onViewAttachedToWindow(holder);
    }

    @CallSuper
    @Override
    public void onViewDetachedFromWindow(@NonNull RViewHolder holder) {
        mRViewItemManager.onViewDetachedFromWindow(holder);
    }

    @CallSuper
    @Override
    public void onViewRecycled(@NonNull RViewHolder holder) {
        mRViewItemManager.onViewRecycled(holder);
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        if (mRViewItemManager != null)
            mRViewItemManager.clear();
    }


}

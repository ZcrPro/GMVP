package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/4/14.
 */
public abstract class BaseRecyclerAdapter<T, VH extends RecyclerView.ViewHolder>
        extends ListRecyclerView.BaseAdapter<VH> {
    private List<T> data = new ArrayList<>();
    private OnActionClickedListener onActionClickedListener;

    public void setOnActionClickedListener(OnActionClickedListener onActionClickedListener) {
        this.onActionClickedListener = onActionClickedListener;
    }

    public void notifyActionClickEvent(RecyclerView.ViewHolder viewHolder, View view) {
        if (onActionClickedListener != null) {
            int position = viewHolder.getAdapterPosition();
            onActionClickedListener.onActionClicked(this, view, position);
        }
    }

    public void setData(List<T> data) {
        this.data.clear();
        if (data != null) {
            this.data.addAll(data);
        }
    }

    public void addData(List<T> data) {
        if (data != null) {
            this.data.addAll(data);
        }
    }

    public void addData(T data) {
        if (data != null) {
            this.data.add(data);
        }
    }

    public List<T> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

}

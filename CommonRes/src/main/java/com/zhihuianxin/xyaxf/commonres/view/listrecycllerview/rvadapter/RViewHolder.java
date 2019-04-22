package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview.rvadapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

public class RViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private RViewItem mRViewItem;

    RViewHolder(View itemView, @NonNull RViewItem viewItem) {
        super(itemView);
        this.mRViewItem = viewItem;
    }

    public RViewItem getRViewItem() {
        return mRViewItem;
    }

    /**
     * 根据ID获取控件
     *
     * @param id
     * @return
     */
    public <T extends View> T getView(int id) {
        if (mViews == null) {
            mViews = new SparseArray<>();
        }
        View view;

        if ((view = mViews.get(id)) == null) {
            view = itemView.findViewById(id);
            if (view == null)
                return null;
            else
                mViews.put(id, view);
        }
        return (T) view;
    }
}

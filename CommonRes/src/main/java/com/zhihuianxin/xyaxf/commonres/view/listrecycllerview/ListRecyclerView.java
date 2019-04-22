package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 2016/02/07：Created by shimao
 * 2016/07/14：添加对GridLayoutManager,StaggeredGridLayoutManager两种布局的支持
 * 删除setEmptyView功能
 */
public class ListRecyclerView extends RecyclerView {
    public ListRecyclerView(Context context) {
        super(context);
        initial(context);

    }

    public ListRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial(context);
    }

    public ListRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initial(context);
    }

    /**
     * A class that represents a fixed subjectView in a list, for example a header at the top
     * or a footer at the bottom.
     */
    public class FixedViewInfo {
        /**
         * The subjectView to add to the list
         */
        public View view;
        /**
         * The data backing the subjectView. This is returned from {@link ListAdapter#getItem(int)}.
         */
        public Object data;
        /**
         * <code>true</code> if the fixed subjectView should be selectable in the list
         */
        public boolean isSelectable;
    }

    /**
     * fixed subjectView info list;
     */
    private List<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();
    private List<FixedViewInfo> mFooterViewInfos = new ArrayList<>();

    private BaseAdapter mAdapter;
    /**
     * item click listener;
     */
    private OnItemClickListener onItemClickListener;
    /**
     * item long click listener
     */
    private OnItemLongClickListener onItemLongClickListener;
    /**
     * divider drawer
     */
    private DividerItemDecoration dividerItemDecoration;
    /**
     * the boolean of draw divider between fixed views
     */
    private boolean drawFixedViewDivider = true;

    /**
     * list item background selector
     */
    private int listSelector = -1;
    /**
     * flag of itemTouchHelper .when itemTouchHelper is working ,this should be true;
     */
    protected boolean isTouchHelperWork = false;
    private int startloadingMoreLeftCount = 10;

    private void initial(Context context) {
        setLayoutManager(new LinearLayoutManager(context) {
            @Override
            public LayoutParams generateDefaultLayoutParams() {
                return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }

        });
        setHasFixedSize(true);
        Drawable d = new ColorDrawable(0xffe0e0e0);
        setDivider(d, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1, getResources()
                .getDisplayMetrics()));
    }


    /**
     * 设置分割线；
     *
     * @param divider
     * @param heightPix 分割线size
     */
    public void setDivider(Drawable divider, int heightPix) {
        if (dividerItemDecoration != null) {
            removeItemDecoration(dividerItemDecoration);
        }
        if (divider != null) {
            dividerItemDecoration = new DividerItemDecoration(divider, ((LinearLayoutManager) getLayoutManager())
                    .getOrientation());
            dividerItemDecoration.dividerHeight = heightPix;
            addItemDecoration(dividerItemDecoration);
        } else
            removeItemDecoration(dividerItemDecoration);
    }

    /**
     * TouchHelper 在进行操作的时候,设置为true,
     *
     * @param isTouchHelperWork
     */
    public void setTouchHelperWork(boolean isTouchHelperWork) {
        this.isTouchHelperWork = isTouchHelperWork;

    }

    /**
     * @return 返回有多少个固定头布局
     */
    public int getHeaderViewCount() {
        return mHeaderViewInfos.size();
    }

    /**
     * @return 返回有多少个固定尾布局
     */
    public int getFooterViewCount() {
        return mFooterViewInfos.size();
    }

    /**
     * 判断固定布局信息是否已经纯在
     */
    private boolean isFixedViewExists(List<FixedViewInfo> infos, View v) {
        for (int i = 0; i < infos.size(); i++) {
            if (infos.get(i).view == v) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a fixed subjectView to appear at the top of the list. If this method is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     *
     * @param v
     * @param data
     * @param isSelectable
     */
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        if (isFixedViewExists(mHeaderViewInfos, v))
            return;
        final FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mHeaderViewInfos.add(info);
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderFooterAdapter)) {
                mAdapter = new HeaderFooterAdapter(mAdapter);
            }
            mAdapter.notifyItemInserted(mHeaderViewInfos.size() - 1);
            // In the case of re-adding a header subjectView, or adding one later on,
            // we need to notify the observer.
//            if (mDataSetObserver != null) {
//                mDataSetObserver.onChanged();
//            }
        }
    }

    public void addHeaderView(View v) {
        addHeaderView(v, null, true);
    }

    /**
     * remove fixed subjectView from header fixed info list
     *
     * @param view
     */
    public void removeHeaderView(View view) {
        if (view != null && mHeaderViewInfos.size() > 0) {
            for (int i = 0; i < mHeaderViewInfos.size(); i++) {
                if (view == mHeaderViewInfos.get(i).view) {
                    mHeaderViewInfos.remove(i);
                    mAdapter.notifyItemRemoved(i);
//                    if (mDataSetObserver != null)
//                        mDataSetObserver.onChanged();
                    break;
                }
            }
        }

    }

    /**
     * Add a fixed subjectView to appear at the bottom of the list. If this method is
     * called more than once, the views will appear in the order they were
     * added. Views added using this call can take focus if they want.
     *
     * @param v
     * @param data
     * @param isSelectable
     */
    public void addFooterView(View v, Object data, boolean isSelectable) {
        if (isFixedViewExists(mFooterViewInfos, v))
            return;
        final FixedViewInfo info = new FixedViewInfo();
        info.view = v;
        info.data = data;
        info.isSelectable = isSelectable;
        mFooterViewInfos.add(info);
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderFooterAdapter)) {
                mAdapter = new HeaderFooterAdapter(mAdapter);
            }
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
            // In the case of re-adding a header subjectView, or adding one later on,
            // we need to notify the observer.
//            if (mDataSetObserver != null) {
//                mDataSetObserver.onChanged();
//            }
        }
    }

    public void addFooterView(View v) {
        addFooterView(v, null, true);
    }

    /**
     * remove fixed subjectView from footer fixed subjectView list;
     *
     * @param view
     */
    public void removeFooterView(View view) {
        if (view != null && mFooterViewInfos.size() > 0) {
            for (int i = 0; i < mFooterViewInfos.size(); i++) {
                if (view == mFooterViewInfos.get(i).view) {
                    int index = mAdapter.getItemCount() - mFooterViewInfos.size() + i;
                    mFooterViewInfos.remove(i);

                    mAdapter.notifyItemRemoved(index);
//                    if (mDataSetObserver != null)
//                        mDataSetObserver.onChanged();
                    break;
                }
            }
        }
    }

    /**
     * please use {@link BaseAdapter} instead of {@link Adapter}
     *
     * @param adapter
     */
    @Deprecated
    @Override
    public void setAdapter(Adapter adapter) {
        //please use BaseAdapter instead;
//        super.setAdapter(adapter);
    }

    /**
     * please use {@link BaseAdapter} instead of {@link Adapter}
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        if (adapter == null) {
            if (mAdapter instanceof HeaderFooterAdapter) {
                ((HeaderFooterAdapter) mAdapter).baseAdapter.unregisterAdapterDataObserver(mDataSetObserver);
                mAdapter = null;
            }
            super.setAdapter(null);
            return;
        }
        if (mAdapter != null && (mAdapter instanceof HeaderFooterAdapter)) {
            ((HeaderFooterAdapter) mAdapter).baseAdapter.unregisterAdapterDataObserver(mDataSetObserver);
        }
        mAdapter = new HeaderFooterAdapter(adapter);
        super.setAdapter(mAdapter);
    }

    /**
     * get current baseAdapter
     *
     * @return
     */
    public BaseAdapter getListAdapter() {
        return mAdapter;
    }


    /**
     * Adapter通知转换器
     */
    private AdapterDataObserver mDataSetObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
            onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (mAdapter != null)
                mAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (mAdapter != null)
                mAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (mAdapter != null)
                mAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (mAdapter != null)
                mAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (mAdapter != null)
                mAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    public int getSpanSizeLookup(int position) {
        LayoutManager manager = getLayoutManager();
        int type = mAdapter.getItemViewType(position);
        if (manager instanceof GridLayoutManager) {
            if (type == HeaderFooterAdapter.headerViewType || type == HeaderFooterAdapter.footerViewType) {
                return ((GridLayoutManager) manager).getSpanCount();
            } else
                return 1;
        }
        return 1;
    }

    /**
     * 包裹在用户设置的Adapter外层的Adapter,利用多布局吧fixedView添加到list中进行显示
     */
    class HeaderFooterAdapter extends BaseAdapter {
        private BaseAdapter baseAdapter;


        private static final int headerViewType = -100;
        private static final int footerViewType = -101;

        HeaderFooterAdapter(BaseAdapter baseAdapter) {
            this.baseAdapter = baseAdapter;
            this.baseAdapter.registerAdapterDataObserver(mDataSetObserver);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = (GridLayoutManager) manager;
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        int type = getItemViewType(position);
                        if (type == headerViewType || type == footerViewType) {
                            return gridManager.getSpanCount();
                        } else
                            return 1;
                    }

                });
            }
            baseAdapter.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            baseAdapter.onDetachedFromRecyclerView(recyclerView);
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            int type = getItemViewType(holder.getLayoutPosition());
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (type == headerViewType || type == footerViewType)) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
            baseAdapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            baseAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(ViewHolder holder) {
            baseAdapter.onViewRecycled(holder);
        }

        @Override
        public boolean onFailedToRecycleView(ViewHolder holder) {
            return baseAdapter.onFailedToRecycleView(holder);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ViewHolder holder;
            if (footerViewType != viewType && viewType != headerViewType) {
                holder = baseAdapter.onCreateViewHolder(parent, viewType);
            } else {
                FrameLayout container = new FrameLayout(getContext());
                holder = new FixedViewHolder(container);
            }
            holder.itemView.setOnClickListener(new HolderClickListener(holder));
            holder.itemView.setOnLongClickListener(new HolderClickListener(holder));
            if (listSelector != -1) {
                holder.itemView.setBackgroundResource(listSelector);
            }
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int footerStartIndex = mHeaderViewInfos.size() + baseAdapter.getItemCount();

            if (position >= mHeaderViewInfos.size() && position < footerStartIndex) {
                baseAdapter.onBindViewHolder(holder, position - mHeaderViewInfos.size());
            } else {
                FrameLayout container = ((FrameLayout) holder.itemView);

                FixedViewInfo info = position < mHeaderViewInfos.size() ? mHeaderViewInfos.get(position) :
                        mFooterViewInfos.get(position - footerStartIndex);

                if (container.indexOfChild(info.view) == -1) {
                    if (info.view.getParent() != null) {
                        ((ViewGroup) info.view.getParent()).removeView(info.view);
                    }
                    container.removeAllViews();
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    container.addView(info.view, lp);
                }
                ((FixedViewHolder) holder).info = info;
            }


        }

        /**
         * HolderClickListener,点击事件包裹类，
         */
        class HolderClickListener implements View.OnClickListener, OnLongClickListener {
            ViewHolder holder;

            HolderClickListener(ViewHolder holder) {
                this.holder = holder;
            }

            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    if ((holder instanceof FixedViewHolder) && !((FixedViewHolder) holder).info.isSelectable) {
                        return;
                    }
                    int pos = holder.getAdapterPosition();
                    onItemClickListener.onItemClick(mAdapter, v, pos, getItemId(pos));
                }

            }

            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null) {
                    if ((holder instanceof FixedViewHolder) && !((FixedViewHolder) holder).info.isSelectable) {
                        return true;
                    }
                    int pos = holder.getAdapterPosition();
                    onItemLongClickListener.onItemLongClick(mAdapter, v, pos, getItemId(pos));
                    return true;
                }
                return false;
            }
        }

        class FixedViewHolder extends ViewHolder {
            FixedViewInfo info;

            public FixedViewHolder(View itemView) {
                super(itemView);
            }
        }

        @Override
        public long getItemId(int position) {
            if (position >= mHeaderViewInfos.size() && position < baseAdapter.getItemCount() + mHeaderViewInfos.size
                    ()) {
                return baseAdapter.getItemId(position - mHeaderViewInfos.size());
            } else
                return -1;
        }


        @Override
        public int getItemViewType(int position) {
            if (position < mHeaderViewInfos.size()) {
                return headerViewType;
            } else if (position >= mHeaderViewInfos.size() + baseAdapter.getItemCount()) {
                return footerViewType;
            } else
                return baseAdapter.getItemViewType(position - mHeaderViewInfos.size());
        }

        @Override
        public int getItemCount() {
            return baseAdapter.getItemCount() + mHeaderViewInfos.size() + mFooterViewInfos.size();
        }

        @Override
        public Object getItem(int position) {

            if (position < mHeaderViewInfos.size()) {
                return mHeaderViewInfos.get(position).data;
            } else if (position >= mHeaderViewInfos.size() + baseAdapter.getItemCount()) {
                return mFooterViewInfos.get(position - (mHeaderViewInfos.size() + baseAdapter.getItemCount())).data;
            } else
                return baseAdapter.getItem(position - mHeaderViewInfos.size());

        }
    }


    /**
     * listview 分割线
     */
    class DividerItemDecoration extends ItemDecoration {
        private Drawable mDivider;
        private int mOrientation;
        private int dividerHeight;

        public DividerItemDecoration(Drawable drawable, int orientation) {
            mDivider = drawable;
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != LinearLayoutManager.HORIZONTAL && orientation != LinearLayoutManager.VERTICAL) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, State state) {
            if (mAdapter != null) {
                if (mOrientation == LinearLayoutManager.VERTICAL) {
                    drawVertical(c, parent);
                } else {
                    drawHorizontal(c, parent);
                }
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();

            for (int i = 0; i < childCount; i++) {

                final View child = parent.getChildAt(i);
                final LayoutParams params = (LayoutParams) child
                        .getLayoutParams();
                final int position = params.getViewLayoutPosition();
                if (position >= mHeaderViewInfos.size() && position < mAdapter.getItemCount() - mFooterViewInfos.size
                        () && drawFixedViewDivider) {
                    final int top = child.getBottom() + params.bottomMargin;
                    final int bottom = top + dividerHeight;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {

            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final LayoutParams params = (LayoutParams) child
                        .getLayoutParams();
                final int position = params.getViewLayoutPosition();
                if (position >= mHeaderViewInfos.size() && position < mAdapter.getItemCount() - mFooterViewInfos.size
                        () && drawFixedViewDivider) {
                    final int left = child.getRight() + params.rightMargin;
                    final int right = left + dividerHeight;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }

            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
            if (mAdapter == null)
                return;
            int position = getLayoutManager().getPosition(view);
            if ((position >= mHeaderViewInfos.size() && position < mAdapter.getItemCount() - mFooterViewInfos.size())
                    || drawFixedViewDivider) {
                if (mOrientation == LinearLayoutManager.VERTICAL) {
                    outRect.set(0, 0, 0, dividerHeight);
                } else {
                    outRect.set(0, 0, dividerHeight, 0);
                }
            } else {
                outRect.set(0, 0, 0, 0);
            }

        }

    }


    /**
     * 设置item的点击事件
     *
     * @param onItemClickListener {@link OnItemClickListener}
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置item的长按点击事件
     *
     * @param onItemLongClickListener {@link OnItemLongClickListener}
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 设置是否在固定视图中也画出分割线
     *
     * @param drawFixedViewDivider
     */
    public void drawFixedViewDivider(boolean drawFixedViewDivider) {
        this.drawFixedViewDivider = drawFixedViewDivider;
        postInvalidate();
    }

    /**
     * set list item background selector
     *
     * @param resId drawable resid
     */
    public void setListSelector(@DrawableRes int resId) {
        this.listSelector = resId;
    }


    /**
     * @return 返回当前显示在列表中第一个item的index
     */
    public int getFirstVisibleItemIndex() {
        View view = getChildAt(0);
        if (getAdapter() != null && view != null)
            return getLayoutManager().getPosition(view);
        return -1;
    }

    /**
     * @return 返回当前显示在列表中最後一个item的index
     */
    public int getLastVisibleItemIndex() {
        View view = getChildAt(getChildCount() - 1);
        if (getAdapter() != null && view != null)
            return getLayoutManager().getPosition(view);
        return -1;
    }

    /**
     * {@link ListRecyclerView} adapter
     */
    public static abstract class BaseAdapter<VH extends ViewHolder> extends Adapter<VH> {
        public abstract Object getItem(int position);

    }

    /**
     * {@link ListRecyclerView} 的OnItemClickListener
     */
    public interface OnItemClickListener {
        void onItemClick(BaseAdapter adapter, View view, int position, long id);
    }

    /**
     * {@link ListRecyclerView} OnItemLongClickListener
     */
    public interface OnItemLongClickListener {
        void onItemLongClick(BaseAdapter adapter, View view, int position, long id);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);

    }
}

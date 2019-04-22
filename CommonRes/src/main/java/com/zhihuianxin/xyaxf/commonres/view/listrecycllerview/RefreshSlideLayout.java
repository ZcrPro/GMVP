package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by Administrator on 2016/8/12.
 */
public class RefreshSlideLayout extends CrossSlideLayout {
    public RefreshSlideLayout(Context context) {
        this(context, null);
    }

    public RefreshSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RefreshSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial();
    }

    public RefreshSlideLayout(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initial();
    }

    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_RELEASE_LOAD = 2;
    public static final int STATUS_LOADING = 3;
    public static final int STATUS_COMPLETE = 4;
    private int mScrollingLoadMoreLeftCount = 5;
    private RefreshSlideManager manager;
    private OnPullLoadingListener onPullLoadingListener;
    HeaderSlideView headerSlideView;
    FooterSlideView footerSlideView;

    private void initial() {
        setSlideManager(manager = new RefreshSlideManager());
        headerSlideView = new HeaderSlideView(getContext());
        setSlideView(headerSlideView, Direction.TOP, SlideManager.CONTENT_ALIGN);
        footerSlideView = new FooterSlideView(getContext());
        setSlideView(footerSlideView, Direction.BOTTOM, SlideManager.CONTENT_ALIGN);
    }

    /**
     * 设置下拉刷新是否可用
     * @param enable
     */
    public void setRefreshEnabled(boolean enable) {
        setSlideEnable(Direction.TOP,enable);
    }
    /**
     * 设置上拉加载更多是否可用
     * @param enable
     */
    public void setLoadMoreEnabled(boolean enable) {
        setSlideEnable(Direction.BOTTOM,enable);
    }
    /**
     * 设置下拉滑动到剩余指定数量数据时候，自动开始加载更多数据。
     * 这个设置只对{@link Direction#CENTER}的ListRecyclerView起作用
     *
     * @param leftCount
     */
    public void setScrollingLoadMore(int leftCount) {
        if (getTarget() != null && getTarget() instanceof ListRecyclerView) {
            mScrollingLoadMoreLeftCount = leftCount;
//            Logger.i("mScrollingLoadMoreLeftCount=" + mScrollingLoadMoreLeftCount);
            ListRecyclerView recyclerView = (ListRecyclerView) getTarget();
            recyclerView.removeOnScrollListener(mScrollingLoadMoreListener);
            if (mScrollingLoadMoreLeftCount > 0) {
                recyclerView.addOnScrollListener(mScrollingLoadMoreListener);
            }

        }
    }

    private RecyclerView.OnScrollListener mScrollingLoadMoreListener =
            new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//            Logger.i("dx=" + dx + "  dy=" + dy);
            if (manager.status == STATUS_NORMAL && mScrollingLoadMoreLeftCount > 0) {
                ListRecyclerView listRecyclerView = (ListRecyclerView) recyclerView;
                int leftCount = listRecyclerView.getListAdapter().getItemCount() - listRecyclerView
                        .getLastVisibleItemIndex();
                Log.i("1",
                        "leftCount=" + leftCount + "  " + listRecyclerView.getListAdapter().getItemCount());
                if (leftCount <= mScrollingLoadMoreLeftCount && manager.status == STATUS_NORMAL) {
//                    Logger.i("leftCount=---------------" + leftCount);
//                    setStopConsumeTouchEvent(true);
                    manager.loadingDirection = Direction.BOTTOM;
                    manager.notifyPullStatusChanged(null, STATUS_LOADING);
                }
            }
        }
    };

    public void setOnPullLoadingListener(OnPullLoadingListener onPullLoadingListener) {
        this.onPullLoadingListener = onPullLoadingListener;
    }

    /**
     * 模拟刷新
     */
    public void performRefresh() {
        manager.performRefresh(100);
    }

    /**
     * 通知视图刷新完毕,否则视图会一直处于刷新加载中给的状态
     */
    public void setLoadingComplete() {
        manager.setLoadingComplete();
    }


    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (manager.status == STATUS_LOADING && manager.isSlideViewResolved()) {
            SlideRefreshView view = manager.getCurrSlideViewHolder().view;
            if (view.getOffset() != 0) {
                return true;
            } else
                return dispatchNestedPreFling(velocityX, velocityY);
        } else
            return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean
            consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(View target) {
        super.onStopNestedScroll(target);
    }

    private int consumeHeaderScrollY(SlideRefreshView view, int dy) {
        //
        dy = dy + getScrollY() >= 0 ? -getScrollY() : dy;
        dy = dy + getScrollY() <= -view.getReleaseLoadOffset() ? -view.getReleaseLoadOffset() -
                getScrollY() : dy;
        return dy;
    }

    private int consumeFooterScrollY(SlideRefreshView view, int dy) {
        dy = dy + getScrollY() <= 0 ? -getScrollY() : dy;
        dy = dy + getScrollY() >= view.getReleaseLoadOffset() ? view.getReleaseLoadOffset() -
                getScrollY() : dy;
        return dy;
    }

    public HeaderSlideView getHeaderSlideView() {
        return headerSlideView;
    }

    public FooterSlideView getFooterSlideView() {
        return footerSlideView;
    }

    /**
     *
     */
    public class RefreshSlideManager extends CrossSlideLayout.SlideManager<RefreshSlideLayout
            .SlideRefreshView> {

        private int status = STATUS_NORMAL;
        private int loadingDirection = -1;

        @Override
        public void onSlideOffsetChanged(SlideRefreshView view, int gravity, View targetView,
                                         boolean isPerform) {
//            Logger.i("offset=" + view.getOffset() + " status=" + status);
            super.onSlideOffsetChanged(view, gravity, targetView, isPerform);

            if (status != STATUS_LOADING) {
                int offset = view.getOffset();
                if (offset < view.getReleaseLoadOffset()) {
                    notifyPullStatusChanged(view, STATUS_NORMAL);
                } else if (offset < view.getMaxOffset()) {
                    notifyPullStatusChanged(view, STATUS_RELEASE_LOAD);
                }
                if (offset <= 0 && !isPerform && !isSliding()) {
                    releaseSlideViewLock();
                }
            } else if (loadingDirection == gravity) {
                view.onPullStatusChanged(STATUS_LOADING);
            }

        }

        @Override
        public void onSlideStop(SlideRefreshView view, int gravity, int offset, float
                offsetVelocity) {
            if (status == STATUS_RELEASE_LOAD || status == STATUS_LOADING) {
                smoothChangeOffsetTo(view.getReleaseLoadOffset());
//                setStopConsumeTouchEvent(false);
                notifyPullStatusChanged(view, STATUS_LOADING);
            } else if (status == STATUS_NORMAL) {
                smoothChangeOffsetTo(0);

            }
        }

        private void notifyPullStatusChanged(SlideRefreshView view, int status) {
            if (status == this.status) {
                return;
            }
            this.status = status;
            if (view != null)
                view.onPullStatusChanged(status);
            if (status == STATUS_LOADING && onPullLoadingListener != null) {
                if (getCurrSlideViewHolder() != null)
                    loadingDirection = getCurrSlideViewHolder().slideDirection;
                onPullLoadingListener.onLoading(loadingDirection);
            }
        }


        public void performRefresh(int delay) {
            if (status == STATUS_NORMAL) {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setCurrSlideViewHolder(Direction.TOP);
                        smoothChangeOffsetTo(getCurrSlideViewHolder().view.getReleaseLoadOffset());
                    }
                }, delay);
            }
        }

        public void setLoadingComplete() {
            if (status != STATUS_LOADING) {
                return;
            }
            if (manager.isSlideViewResolved()) {
                notifyPullStatusChanged(getCurrSlideViewHolder().view, STATUS_COMPLETE);
//                setStopConsumeTouchEvent(true);
                smoothChangeOffsetTo(0);
            } else {
                loadingDirection = -1;
                status = STATUS_NORMAL;
            }

        }

        @Override
        public boolean onSlideEventAccept(int direction) {
            if (status == STATUS_LOADING || status == STATUS_COMPLETE) {
                return direction == loadingDirection;
            }
            return true;
        }

        @Override
        public boolean onSmoothChangeOffsetComplete(int offset) {
            if (offset == 0 && status == STATUS_COMPLETE) {
                loadingDirection = -1;
                Log.i("1", "onSmoothChangeOffsetComplete");
//                setStopConsumeTouchEvent(false);
                notifyPullStatusChanged(getCurrSlideViewHolder().view, STATUS_NORMAL);
            }
            if (offset == getCurrSlideViewHolder().view.getReleaseLoadOffset() && status ==
                    STATUS_RELEASE_LOAD) {
                notifyPullStatusChanged(getCurrSlideViewHolder().view, STATUS_LOADING);
            }
            return super.onSmoothChangeOffsetComplete(offset);
        }


    }

    /**
     *
     */
    public abstract static class SlideRefreshView extends FrameSlideView {

        public SlideRefreshView(Context context) {
            super(context);
        }

        public SlideRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        abstract public void onPullStatusChanged(int status);

        abstract public int getReleaseLoadOffset();

    }

    /**
     * 刷新回调
     */
    public interface OnPullLoadingListener {
        void onLoading(int type);
    }
}

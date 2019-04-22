package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;

import com.zhihuianxin.xyaxf.commonres.R;


/**
 * 属性动画要求至少3.0得系统
 * Created by Administrator on 2016/6/3.
 */
public class CrossSlideLayout extends FrameLayout implements NestedScrollingParent,
        NestedScrollingChild {
    private String TAG = "RefreshLayout";


    /**
     * 四个方向上都可以
     *
     * @param context
     */
    public CrossSlideLayout(Context context) {
        this(context, null);
    }

    public CrossSlideLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CrossSlideLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initial(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CrossSlideLayout(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initial(context);
    }

    private NestedScrollingParentHelper nestedScrollingParentHelper;
    private NestedScrollingChildHelper nestedScrollingChildHelper;
    private int lastX, lastY;
    private SlideManager slideManager;
    private View mTarget;
    private int mMinimumFlingVelocity;
    private int mMaximumFlingVelocity;
    private VelocityTracker mVelocityTracker;
    private boolean stopConsumeTouchEvent = false;

    private void initial(Context context) {
        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        nestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        setNestedScrollingEnabled(true);
    }

    public void setSlideManager(@NonNull SlideManager slideManager) {
        if (this.slideManager != null) {
            slideManager.copyState(this.slideManager);
        }
        this.slideManager = slideManager;
        this.slideManager.mLayoutContainer = this;
        requestLayout();
    }

    public SlideManager getSlideManager() {
        return slideManager;
    }

    public void setTarget(@NonNull View mTarget) {
        if (indexOfChild(mTarget) == -1)
            return;
        this.mTarget = mTarget;
    }

    public View getTarget() {
        return mTarget;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        checkSlideManager();
        View view;
        CrossSlideLayout.LayoutParams lp;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);
            lp = (LayoutParams) view.getLayoutParams();
            if (lp.slideDirection != -1) {
                if (lp.slideDirection == Direction.CENTER && view.getVisibility() == View.VISIBLE) {
                    mTarget = view;
                } else if (view instanceof SlideView) {

                    inflateSlideView((SlideView) view, lp.slideDirection, SlideManager
                            .CONTENT_ALIGN);
                }
            }
        }
        getSlideManager().requestLayoutSlideView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        if (lp.slideDirection == Direction.CENTER) {
            setTarget(child);
        }
    }

    public void refreshTargetView() {
        View view;
        CrossSlideLayout.LayoutParams lp;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);
            lp = (LayoutParams) view.getLayoutParams();
            if (lp.slideDirection != -1) {
                if (lp.slideDirection == Direction.CENTER && view.getVisibility() == View.VISIBLE) {
                    mTarget = view;
                }
            }
        }
    }

    /*check slideManager null */
    synchronized void checkSlideManager() {
        if (slideManager == null) {
            slideManager = new SlideManager();
            slideManager.mLayoutContainer = this;
//            throw new NullPointerException("the CrossSlideLayout is based on SlideManager and " +
//                    "it`s null");
        }
    }

    /**
     * 添加一个PullView
     *
     * @param view
     * @param gravity
     * @param contentType
     */
    public void setSlideView(@NonNull SlideView view, int gravity, int contentType) {
        setSlideView(view, gravity, contentType, null);
    }

    /**
     * 添加一个PullView
     *
     * @param view
     * @param direction
     * @param contentType
     * @param range       手势范围
     */
    public void setSlideView(@NonNull SlideView view, int direction, int contentType, Rect range) {
//        if (indexOfChild((View) view) != -1) {
//            return;
//        }
        checkSlideManager();
        if (indexOfChild((View) view) == -1) {
            LayoutParams lp;
            int h = view.getMaxOffset() < 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : view
                    .getMaxOffset();
            if (direction == Direction.TOP || direction == Direction.BOTTOM) {
                lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h, direction);
            } else {
                lp = new LayoutParams(h, ViewGroup.LayoutParams.MATCH_PARENT, direction);
            }
            addView((View) view, lp);

        }
        SlideManager.SlideViewHolder holder = slideManager.getSlideView(direction);
        if (holder != null) {
            removeView((View) holder.view);
        }

        slideManager.setSlideView(view, direction, contentType, range);
        slideManager.requestLayoutSlideView();
    }

    private void inflateSlideView(@NonNull SlideView view, int diection, int contentType) {
        LayoutParams lp = (LayoutParams) ((View) view).getLayoutParams();
        int h = view.getMaxOffset() < 0 ? ViewGroup.LayoutParams.WRAP_CONTENT : view.getMaxOffset();
        if (diection == Direction.TOP || diection == Direction.BOTTOM) {
            lp.height = h;
            lp.width = ViewPager.LayoutParams.MATCH_PARENT;
        } else {
            lp.width = h;
            lp.height = ViewPager.LayoutParams.MATCH_PARENT;
        }
        slideManager.setSlideView(view, diection, contentType, null);
    }

    /**
     * 设置某个方向上是否能滑动
     *
     * @param gravity 方向
     * @param enable
     */
    public void setSlideEnable(int gravity, boolean enable) {
        checkSlideManager();
        slideManager.setSlideEnable(gravity, enable);
    }

    //****************************************************************************


    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        nestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return nestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return nestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        nestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return nestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int
            dyUnconsumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return nestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return nestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return nestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
        startNestedScroll(nestedScrollAxes);
    }

    @Override
    public void onStopNestedScroll(View target) {
        stopNestedScroll();
        nestedScrollingParentHelper.onStopNestedScroll(target);
        checkSlideManager();
        if (stopConsumeTouchEvent)
            return;
        slideManager.onSlideStopInternal(computeCurrentVelocity());
    }

    private int[] slideConsume = new int[]{0, 0};
    private int[] offsetInWindow = new int[2];


    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int
            dyUnconsumed) {
        checkSlideManager();
        slideConsume[0] = 0;
        slideConsume[1] = 0;
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
//        Log.i("1", getClass().getSimpleName() + " onNestedScroll  dxConsumed=" + dxConsumed + "
// dyConsumed=" +
//                dyConsumed + " dxUnconsumed=" +
//                dxUnconsumed + " dyUnconsumed=" + dyUnconsumed + " offsetInWindow[0]=" +
//                offsetInWindow[0] + " " +
//                "offsetInWindow[1]=" + offsetInWindow[1]);
        //在onNestedScroll中处理判断slideview的原因是想让嵌套滑动的触发View能滑动到最边界。
        if (!canParentSlideViewScroll() && slideManager.analysisSlideView(dxUnconsumed +
                offsetInWindow[0], dyUnconsumed + offsetInWindow[1]) && !stopConsumeTouchEvent) {
            slideManager.onNestedScrollConsume(dxUnconsumed + offsetInWindow[0], dyUnconsumed +
                            offsetInWindow[1],
                    slideConsume);//消耗掉本次滑动事件
        }
    }

    private boolean canParentSlideViewScroll() {
        if (getParent() instanceof CrossSlideLayout) {
            return ((CrossSlideLayout) getParent()).canParentSlideViewScroll();
        } else if (getSlideManager() != null && getSlideManager().isSlideViewResolved()) {
            SlideView slideView = getSlideManager().currSlideViewHolder.view;
            if (slideView.getMaxOffset() >= 0) {
                return slideView.getOffset() > slideView.getMinOffset() && slideView.getOffset()
                        < slideView.getMaxOffset();
            } else {
                return slideView.getOffset() > slideView.getMinOffset();
            }

        } else
            return false;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        checkSlideManager();
        slideConsume[0] = 0;
        slideConsume[1] = 0;
//        Log.i("1", getClass().getSimpleName() + " onNestedPreScroll  dx=" + dx + " dy=" + dy +
// " resolved=" +
//                slideManager.isSlideViewResolved() + " consumed[0]=" + consumed[0] + "
//       consumed[1] = "
//                + consumed[0] +
//                " offsetInWindow[0]=" + offsetInWindow[0] + " offsetInWindow[1]=" +
//                offsetInWindow[1]);
        if (slideManager.isSlideViewResolved() && !stopConsumeTouchEvent) {
            slideManager.onNestedScrollConsume(dx - consumed[0], dy - consumed[1], slideConsume);
            consumed[0] = consumed[0] + slideConsume[0];
            consumed[1] = consumed[1] + slideConsume[1];
//            dispatchNestedPreScroll(dx - slideConsume[0], dy - slideConsume[1], consumed,
// offsetInWindow);
        } else {
            dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
        }

    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        boolean consume = dispatchNestedPreFling(velocityX, velocityY);
        if (consume)
            return true;
        if (slideManager != null && slideManager.isSlideViewResolved()) {
            SlideManager.SlideViewHolder holder = slideManager.currSlideViewHolder;
            switch (holder.slideDirection) {
                case Direction.TOP:
                    if (velocityY < 0 && holder.view.getOffset() >= holder.view.getMaxOffset()) {
                        return false;
                    }
                    break;
                case Direction.BOTTOM:
                    if (velocityY > 0 && holder.view.getOffset() >= holder.view.getMaxOffset()) {
                        return false;
                    }
                    break;
                case Direction.LEFT:
                    if (velocityX < 0 && holder.view.getOffset() >= holder.view.getMaxOffset()) {
                        return false;
                    }
                    break;
                case Direction.RIGHT:
                    if (velocityX > 0 && holder.view.getOffset() >= holder.view.getMaxOffset()) {
                        return false;
                    }
                    break;
            }
            return true;
        }
        return false;

    }

    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();//获得VelocityTracker类实例
        }
        mVelocityTracker.addMovement(ev);//将事件加入到VelocityTracker类实例中
        checkSlideManager();
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            slideManager.setMotionDownLoc((int) ev.getX(0), (int) ev.getY(0));
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchInTarget(float x, float y) {
        if (mTarget != null) {
            LayoutParams targetLp = (LayoutParams) mTarget.getLayoutParams();

            return x < mTarget.getWidth() - getScrollX() - targetLp.rightMargin
                    && x > -getScrollX() + targetLp.leftMargin
                    && y < mTarget.getHeight() - getScrollY() - targetLp.bottomMargin
                    && y > -getScrollY() + targetLp.topMargin;
        } else return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if ((Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            checkSlideManager();

            Log.i("1", mTarget.getWidth() + " " + mTarget.getHeight() + " scrollY=" + getScrollX() + " scrollY=" +
                    getScrollY());
            if (stopConsumeTouchEvent)
                return false;
            if (!isTouchInTarget(event.getX(), event.getY())) {
                return false;
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getX();
                    lastY = (int) event.getY();
                    slideManager.setMotionDownLoc(lastX, lastY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getX() - lastX;
                    int dy = (int) event.getY() - lastY;
                    if (slideManager.analysisSlideView(-dx, -dy) && !stopConsumeTouchEvent) {
//                        slideManager.onNestedScrollConsume(-dx, -dy, null);
                        lastX = (int) event.getX();
                        lastY = (int) event.getY();
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        checkSlideManager();
        if (stopConsumeTouchEvent)
            return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                lastX = (int) event.getX();
                lastY = (int) event.getY();
                slideManager.setMotionDownLoc(lastX, lastY);
                break;
            case MotionEvent.ACTION_MOVE:

                int dx = (int) event.getX() - lastX;
                int dy = (int) event.getY() - lastY;
                if (slideManager.analysisSlideView(-dx, -dy) && !stopConsumeTouchEvent) {
                    slideManager.onNestedScrollConsume(-dx, -dy, null);
                }
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                slideManager.onSlideStopInternal(computeCurrentVelocity());
                break;

        }

        return true;
    }

    /**
     * 计算当前的滑动速度
     *
     * @return
     */
    private float computeCurrentVelocity() {
        float velocity = 0f;
        if (mVelocityTracker != null) {
            mVelocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
//            Log.i("test", "velocityTraker" + mVelocityTracker.getXVelocity() + " " +
//                    mVelocityTracker.getYVelocity());
            if (slideManager != null && slideManager.isSlideViewResolved()) {
                int gravity = slideManager.getCurrSlideViewHolder().slideDirection;
                velocity = gravity == Direction.BOTTOM || gravity == Direction.TOP ?
                        mVelocityTracker.getYVelocity()
                        : mVelocityTracker.getXVelocity();
                velocity = Math.abs(velocity) > mMinimumFlingVelocity ? velocity : 0f;
            }
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
        return velocity;
    }


    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if ((Build.VERSION.SDK_INT < 21 && mTarget instanceof AbsListView)
                || (mTarget != null && !ViewCompat.isNestedScrollingEnabled(mTarget))) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    public void setStopConsumeTouchEvent(boolean stopConsume) {
        this.stopConsumeTouchEvent = stopConsume;
    }

    /**
     * ---------------------------------------------------------------------------------------------
     */
    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    public CrossSlideLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        } else if (lp instanceof FrameLayout.LayoutParams) {
            return new LayoutParams((FrameLayout.LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    /**
     * 布局类
     */
    public static class LayoutParams extends FrameLayout.LayoutParams {

        private int slideDirection = -1;
        private int maxOffset = -1;
        private int minOffset = 0;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull MarginLayoutParams source) {
            super(source);
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(@NonNull FrameLayout.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.CrossSlideLayout);
            slideDirection = a.getInt(R.styleable.CrossSlideLayout_slideDirection, -1);
            maxOffset = a.getDimensionPixelSize(R.styleable.CrossSlideLayout_maxOffset, -1);
            minOffset = a.getDimensionPixelSize(R.styleable.CrossSlideLayout_minOffset, 0);
            setSlideDirection(slideDirection);
            a.recycle();
            if (minOffset < 0) {
                throw new IllegalArgumentException("SlideView min offset can not be less than 0");
            }
            if (maxOffset > 0 && maxOffset < minOffset) {
                throw new IllegalStateException("The SlideView minOffset is more than maxOffset");
            }
        }

        public LayoutParams(int width, int height, int slideDirection) {
            super(width, height);
            setSlideDirection(slideDirection);
        }

        public void setMaxOffset(int maxOffset) {
            this.maxOffset = maxOffset;
        }

        public void setMinOffset(int minOffset) {
            this.minOffset = minOffset;
        }

        public int getMaxOffset() {
            return maxOffset;
        }

        public int getMinOffset() {
            return minOffset;
        }

        public void setSlideDirection(int slideDirection) {
            this.slideDirection = slideDirection;
            switch (slideDirection) {
                case Direction.LEFT:
                    this.gravity = Gravity.LEFT;
                    break;
                case Direction.BOTTOM:
                    this.gravity = Gravity.BOTTOM;
                    break;
                case Direction.TOP:
                    this.gravity = Gravity.TOP;
                    break;
                case Direction.RIGHT:
                    this.gravity = Gravity.RIGHT;
                    break;
            }
        }

    }

    /**
     * SlideManager
     * 核心控制器
     */
    public static class SlideManager<T extends SlideView> {


        public static final int CONTENT_ALIGN = 0x07;
        public static final int CONTENT_BELOW = 0x08;

        private SlideViewHolder currSlideViewHolder;

        private CrossSlideLayout mLayoutContainer;
        private SparseArray<SlideViewHolder> slideViewHolders;
        private int downX, downY;
        private int animationDuration = 300;
        ValueAnimator changeOffsetAnimator;


        /**
         * 设置滑动动画的时间
         *
         * @param animationDuration
         */
        public void setAnimationDuration(int animationDuration) {
            if (animationDuration <= 0) {
                return;
            }
            this.animationDuration = animationDuration;
        }

        public void setMotionDownLoc(int downX, int downY) {
            this.downX = downX;
            this.downY = downY;
//            Log.i("", "setMotionDownLoc " + downX + " --" + downY);
        }

        public class SlideViewHolder {
            protected boolean requestLayout = true;
            public T view;
            protected int contentType;
            public int slideDirection;
            protected Rect range;
            protected boolean isEnable = true;
        }

        public SlideManager() {
            slideViewHolders = new SparseArray<>();
        }

        public void copyState(SlideManager slideManager) {
            if (slideManager != null) {
                this.slideViewHolders = slideManager.slideViewHolders.clone();
                this.currSlideViewHolder = slideManager.currSlideViewHolder;
            }
        }

        /**
         * 添加一个SlideView
         *
         * @param view
         * @param direction
         * @param contentType
         */
        @SuppressWarnings("unchecked")
        protected void setSlideView(@NonNull SlideView view, int direction, int contentType, Rect
                range) {
            if (contentType != CONTENT_ALIGN && contentType != CONTENT_BELOW) {
                throw new IllegalArgumentException("contentType is not support");
            }
            if (direction != Direction.LEFT && direction != Direction.TOP && direction !=
                    Direction.RIGHT &&
                    direction != Direction.BOTTOM) {
                throw new IllegalArgumentException("slideDirection is not support");
            }
            if (view.getMinOffset() < 0) {
                throw new IllegalArgumentException("SlideView min offset can not be less than 0");
            }
            if (view.getMaxOffset() > 0 && view.getMaxOffset() < view.getMinOffset()) {
                throw new IllegalStateException("The SlideView minOffset is more than maxOffset");
            }
            view.setSlideManager(this);
            SlideViewHolder holder = null;
            for (int i = 0; i < slideViewHolders.size(); i++) {
                if (slideViewHolders.get(slideViewHolders.keyAt(i)).view == view) {
                    holder = slideViewHolders.get(slideViewHolders.keyAt(i));
                    break;
                }
            }
            if (holder == null) {
                holder = new SlideViewHolder();
            }
            holder.view = (T) view;
            holder.slideDirection = direction;
            holder.contentType = contentType;
            holder.range = range;
            slideViewHolders.put(direction, holder);
        }

        /**
         * 设置触发在某个方向上拉动行为的范围
         *
         * @param range
         * @param gravity
         */
        public void setTouchRange(Rect range, int gravity) {
            if (slideViewHolders.indexOfKey(gravity) != -1)
                slideViewHolders.get(gravity).range = range;
        }

        public SlideViewHolder getSlideView(int gravity) {
            return slideViewHolders.get(gravity);
        }

        public void requestLayoutSlideView() {
            if (mLayoutContainer.mTarget == null)
                return;
            if (changeOffsetAnimator != null && changeOffsetAnimator.isRunning()) {
                changeOffsetAnimator.end();
                changeOffsetAnimator = null;
            }
            SlideViewHolder holderLeft = getSlideView(Direction.LEFT);
            SlideViewHolder holderRight = getSlideView(Direction.RIGHT);
            SlideViewHolder holderTop = getSlideView(Direction.TOP);
            SlideViewHolder holderBottom = getSlideView(Direction.BOTTOM);
            LayoutParams targetLp = (LayoutParams) mLayoutContainer.mTarget.getLayoutParams();
            if (holderLeft != null) {
                int left = resolveContentType(Direction.LEFT, holderLeft);
                holderLeft.view.onAttachToParent(left);
                LayoutParams lp = (LayoutParams) ((View) holderLeft.view).getLayoutParams();
                if (lp.maxOffset < 0) {
                    lp.setMaxOffset(left);
                }
                lp.setMargins(-holderTop.view.getMaxOffset() + holderLeft.view.getMinOffset(), 0, 0, 0);
                if (holderLeft.contentType == CONTENT_ALIGN)
                    targetLp.leftMargin = holderLeft.view.getMinOffset();
                Log.i(mLayoutContainer.TAG, "requestLayoutPullView left=" + left);
            }
            if (holderRight != null) {
                int right = resolveContentType(Direction.RIGHT, holderRight);
                holderRight.view.onAttachToParent(right);
                LayoutParams lp = (LayoutParams) ((View) holderRight.view).getLayoutParams();
                if (lp.maxOffset < 0) {
                    lp.setMaxOffset(right);
                }
                lp.setMargins(0, 0, -holderTop.view.getMaxOffset() + holderRight.view.getMinOffset(), 0);
                if (holderRight.contentType == CONTENT_ALIGN)
                    targetLp.rightMargin = holderRight.view.getMinOffset();
                Log.i(mLayoutContainer.TAG, "requestLayoutPullView right=" + right);
            }
            if (holderTop != null) {
                int top = resolveContentType(Direction.TOP, holderTop);
                holderTop.view.onAttachToParent(top);
                LayoutParams lp = (LayoutParams) ((View) holderTop.view).getLayoutParams();
                if (lp.maxOffset < 0) {
                    lp.setMaxOffset(top);
                }
                lp.setMargins(0, -holderTop.view.getMaxOffset() + holderTop.view.getMinOffset(), 0, 0);
                if (holderTop.contentType == CONTENT_ALIGN)
                    targetLp.topMargin = holderTop.view.getMinOffset();
                Log.i(mLayoutContainer.TAG, "requestLayoutPullView top=" + top);
            }
            if (holderBottom != null) {
                int bottom = resolveContentType(Direction.BOTTOM, holderBottom);
                holderBottom.view.onAttachToParent(bottom);
                LayoutParams lp = (LayoutParams) ((View) holderBottom.view).getLayoutParams();
                if (lp.maxOffset < 0) {
                    lp.setMaxOffset(bottom);
                }
                lp.setMargins(0, 0, 0, -holderTop.view.getMaxOffset() + holderBottom.view.getMinOffset());
                if (holderBottom.contentType == CONTENT_ALIGN)
                    targetLp.bottomMargin = holderBottom.view.getMinOffset();
                Log.i(mLayoutContainer.TAG, "requestLayoutPullView bottom=" + bottom);
            }
            Log.i("", "" + targetLp.topMargin + " " + targetLp.leftMargin + " " + targetLp
                    .rightMargin + " " + targetLp
                    .bottomMargin);
//            mLayoutContainer.mTarget.setLayoutParams(targetLp);
        }

        private int resolveContentType(int gravity, SlideViewHolder holder) {
            boolean vertical = gravity == Direction.TOP || gravity == Direction.BOTTOM;
            if (holder.contentType == CONTENT_ALIGN) {
                int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                ((View) holder.view).measure(w, h);
                return (vertical ? ((View) holder.view).getMeasuredHeight() : ((View) holder
                        .view).getMeasuredWidth());
            } else {
                LayoutParams lp = (LayoutParams) ((View) holder.view).getLayoutParams();
                if (vertical) {
                    lp.height = holder.view.getMinOffset();
                } else {
                    lp.width = holder.view.getMinOffset();
                }
                return 0;
            }

        }

        /**
         * @return true 可以进行拉动，false 不能进行拉动，或被玩家锁定
         */
        public boolean isSlideViewResolved() {
//            return true;
            return currSlideViewHolder != null;
        }

        /**
         * 返回当前Touch事件的开始坐标是否在指定的范围内
         *
         * @param rect
         * @return
         */
        private boolean isDownTouchInRange(Rect rect) {
            if (rect == null)
                return true;
            else {
                return rect.contains(downX, downY);
            }
        }

        /**
         * 分析滑动视图得方向
         *
         * @param dx
         * @param dy
         * @return
         */
        private boolean analysisSlideView(int dx, int dy) {

            if (isSlideViewResolved())
                return true;
            if (Math.abs(dx) > Math.abs(dy)) {  //横向滑动
                SlideViewHolder holderLeft = getSlideView(Direction.LEFT);
                SlideViewHolder holderRight = getSlideView(Direction.RIGHT);
                if (holderLeft != null && holderLeft.isEnable && dx < 0 && isDownTouchInRange
                        (holderLeft.range)) {
                    if (onSlideEventAccept(holderLeft.slideDirection))
                        currSlideViewHolder = holderLeft;
                } else if (holderRight != null && holderRight.isEnable && dx > 0 &&
                        isDownTouchInRange(holderRight
                                .range)) {
                    if (onSlideEventAccept(holderRight.slideDirection))
                        currSlideViewHolder = holderRight;
                }
            } else {//纵向滑动
                SlideViewHolder holderTop = getSlideView(Direction.TOP);
                SlideViewHolder holderBottom = getSlideView(Direction.BOTTOM);
                if (holderTop != null && holderTop.isEnable && dy < 0 && isDownTouchInRange(holderTop.range)) {
                    if (onSlideEventAccept(holderTop.slideDirection))
                        currSlideViewHolder = holderTop;
                } else if (holderBottom != null && holderBottom.isEnable && dy > 0 && isDownTouchInRange(holderBottom
                        .range)) {
                    if (onSlideEventAccept(holderBottom.slideDirection))
                        currSlideViewHolder = holderBottom;
                }
            }
            if (currSlideViewHolder != null)
                currSlideViewHolder.view.onSlideStart();
            return isSlideViewResolved();
        }

        /**
         * 开始消耗嵌套滑动未消化得滑动
         *
         * @param dxUnconsumed
         * @param dyUnconsumed
         * @param consume
         */
        private void onNestedScrollConsume(int dxUnconsumed, int dyUnconsumed, int[] consume) {
            if (changeOffsetAnimator != null) {
                changeOffsetAnimator.cancel();
                changeOffsetAnimator = null;
            }
            if (isSlideViewResolved()) {
                int offset = currSlideViewHolder.view.getOffset();

                int offsetChanged = 0;
                switch (currSlideViewHolder.slideDirection) {
                    case Direction.TOP:
                        offsetChanged = -dyUnconsumed;
                        offsetChanged = onSlidingPreChangedInternal(currSlideViewHolder.view,
                                offset, offsetChanged);
                        if (consume != null) {
                            consume[1] = -offsetChanged;
                        }

                        break;
                    case Direction.BOTTOM:
                        offsetChanged = dyUnconsumed;
                        offsetChanged = onSlidingPreChangedInternal(currSlideViewHolder.view,
                                offset, offsetChanged);
                        if (consume != null) {
                            consume[1] = offsetChanged;
                        }

                        break;
                    case Direction.LEFT:
                        offsetChanged = -dxUnconsumed;
                        offsetChanged = onSlidingPreChangedInternal(currSlideViewHolder.view,
                                offset, offsetChanged);
                        if (consume != null) {
                            consume[0] = -offsetChanged;
                        }
                        break;
                    case Direction.RIGHT:
                        offsetChanged = dxUnconsumed;
                        offsetChanged = onSlidingPreChangedInternal(currSlideViewHolder.view,
                                offset, offsetChanged);
                        if (consume != null) {
                            consume[0] = offsetChanged;
                        }
                        break;
                }
                if (offsetChanged != 0)
                    onSlideOffsetChangedInternal(offset, offsetChanged, false);
            }
        }


        /**
         * 滑动过程中回调给pullview。
         *
         * @param offset
         * @param offsetChanged
         * @param isPerform
         */
        private void onSlideOffsetChangedInternal(int offset, int offsetChanged, boolean
                isPerform) {
            if (!isSlideViewResolved())
                return;
            //回调
            currSlideViewHolder.view.onSlideOffsetChanged(offset + offsetChanged, offsetChanged,
                    isPerform);
            int _offset = currSlideViewHolder.view.getOffset() - getCurrSlideViewHolder().view
                    .getMinOffset();
            //变化模式-对齐contentview
            if (currSlideViewHolder.contentType == CONTENT_ALIGN) {
                switch (currSlideViewHolder.slideDirection) {
                    case Direction.TOP:
                        mLayoutContainer.scrollTo(0, -_offset);
                        break;
                    case Direction.BOTTOM:
                        mLayoutContainer.scrollTo(0, _offset);
                        break;
                    case Direction.LEFT:
                        mLayoutContainer.scrollTo(-_offset, 0);
                        break;
                    case Direction.RIGHT:
                        mLayoutContainer.scrollTo(_offset, 0);
                        break;
                }
            } else {//变化模式-高于contentview
                LayoutParams lp = (LayoutParams) ((View) currSlideViewHolder.view)
                        .getLayoutParams();
                if (currSlideViewHolder.slideDirection == Direction.LEFT || currSlideViewHolder
                        .contentType == Direction.RIGHT) {
                    lp.width = _offset;
                } else {
                    lp.height = _offset;
                }
                ((View) currSlideViewHolder.view).setLayoutParams(lp);
            }
            onSlideOffsetChanged(currSlideViewHolder.view, currSlideViewHolder.slideDirection,
                    mLayoutContainer.mTarget, isPerform);

        }

        /**
         * 开发者可重写该方法,实现拉动过程
         */
        public void onSlideOffsetChanged(T view, int slideDirection, View targetView, boolean
                isPerform) {
            //DO NOTHING
            //添加该代码就可以在主视图滑动完毕以后才滑动slideview
            if (!isPerform && currSlideViewHolder.view.getOffset() == currSlideViewHolder.view
                    .getMinOffset()) {
                releaseSlideViewLock();
            }
        }

        /**
         * 调用OnSlideOffsetChanged之前处理拉动距离,使拉动在正确的范围之类
         *
         * @param view          current pull subjectView
         * @param offset        current offset
         * @param offsetChanged if doing nothing,please return offsetChanged
         * @return 返回offsetChanged。如果返回为0，则意味着没有变化，不会调用OnSlideOffsetChanged
         */
        private int onSlidingPreChangedInternal(T view, int offset, int offsetChanged) {
            //make sure the offset is always in offset range;
            offsetChanged = onSlidingPreChanged(view, offset, offsetChanged);
            int minOffset = view.getMinOffset();
            int maxOffset = view.getMaxOffset();
            minOffset = minOffset < 0 ? 0 : minOffset;
            offsetChanged = offset + offsetChanged < minOffset ? minOffset - offset : offsetChanged;
            if (maxOffset >= 0) {
                if (minOffset > maxOffset) {
                    throw new IllegalStateException("The SlideView minOffset is more than " +
                            "maxOffset");
                }
                offsetChanged = offset + offsetChanged <= maxOffset ? offsetChanged : maxOffset -
                        offset;
            }
            return offsetChanged;
        }

        /**
         * 调用OnSlideOffsetChanged之前处理拉动距离
         * can override by programmer
         *
         * @param view
         * @param offset
         * @param offsetChanged
         * @return
         */
        public int onSlidingPreChanged(T view, int offset, int offsetChanged) {
            return offsetChanged;
        }

        /**
         * 是否接受该方向得滑动事件
         *
         * @return true接受，false不接受
         */
        public boolean onSlideEventAccept(int diection) {
            return true;
        }

        private void onSlideStopInternal(float offsetVelocity) {
            if (isSlideViewResolved()) {
                onSlideStop(currSlideViewHolder.view, currSlideViewHolder.slideDirection,
                        currSlideViewHolder.view.getOffset(), offsetVelocity);
            }
        }

        /**
         * 停止拉动的时候回调
         *
         * @param gravity
         * @param offset
         */
        public void onSlideStop(T view, int gravity, int offset, float offsetVelocity) {
            if (offsetVelocity < -500) {
                smoothChangeOffsetTo(view.getMinOffset());
            } else if (offsetVelocity > 500) {
                smoothChangeOffsetTo(view.getMaxOffset());
            } else {
                smoothChangeOffsetTo(Math.abs(offset - view.getMinOffset()) < Math.abs(offset -
                        view.getMaxOffset()) ? view.getMinOffset() : view.getMaxOffset());
            }
        }

        public void releaseSlideViewLock() {
            if (changeOffsetAnimator != null) {
                changeOffsetAnimator.cancel();
                changeOffsetAnimator = null;
            }
            if (currSlideViewHolder != null) {
                currSlideViewHolder.view.onSlideStop();
            }
            currSlideViewHolder = null;
//            Logger.i("--------------------->release");
        }

        public SlideViewHolder getCurrSlideViewHolder() {
            return currSlideViewHolder;
        }

        /**
         * 指定当前拉动的方向
         *
         * @param direction {@link Direction}
         */
        public void setCurrSlideViewHolder(int direction) {
            currSlideViewHolder = getSlideView(direction);
            if (!currSlideViewHolder.isEnable)
                currSlideViewHolder = null;
        }

        /**
         * 改变当前Offset，如果没有指定当前可以拉动的view，则不能滑动
         *
         * @param offset
         */
        public final void changeOffsetTo(int offset) {
            if (isSlideViewResolved()) {
                onSlideOffsetChangedInternal(offset, 0, true);
            }

        }

        public boolean isSliding() {
            if (changeOffsetAnimator != null) {
                return changeOffsetAnimator.isRunning();
            } else {
                return false;
            }
        }

        /**
         * 平滑的改变当前Offset，如果没有指定当前可以拉动的view，则不能滑动
         *
         * @param targetOffset
         */
        public final void smoothChangeOffsetTo(int targetOffset) {
            if (targetOffset < 0) {
                return;
            }
            if (changeOffsetAnimator != null) {
                changeOffsetAnimator.cancel();
                changeOffsetAnimator = null;
            }
            if (!isSlideViewResolved()) {
                return;
            }
            Log.i("1", "smoothChangeOffsetTo");
            changeOffsetAnimator = ValueAnimator.ofInt(currSlideViewHolder.view.getOffset(),
                    targetOffset);
            currSlideViewHolder.view.onSlideStart();
            changeOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int offset = (int) animation.getAnimatedValue();
                    onSlideOffsetChangedInternal(offset, 0, true);
                }
            });
            changeOffsetAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    if (currSlideViewHolder != null) {
                        if (onSmoothChangeOffsetComplete(currSlideViewHolder.view.getOffset())) {
                            releaseSlideViewLock();
                        } else {
                            currSlideViewHolder.view.onSlideStop();
                        }
                    }

                    changeOffsetAnimator = null;
                }
            });
            changeOffsetAnimator.setDuration(animationDuration);
            changeOffsetAnimator.start();
        }

        /**
         * 会在每次调用 smoothChangeOffsetTo 动画结束后回调到此方法
         *
         * @param offset
         * @return true will release Pull View Lock,false will not
         */
        public boolean onSmoothChangeOffsetComplete(int offset) {
//            Log.i("", "onSmoothChangeOffsetComplete offset=" + offset);
            return offset == getCurrSlideViewHolder().view.getMinOffset();
        }

        public void setSlideEnable(int direction, boolean enable) {
            SlideViewHolder holder = slideViewHolders.get(direction);
            if (holder != null) {
                holder.isEnable = enable;
            }
        }

    }


}

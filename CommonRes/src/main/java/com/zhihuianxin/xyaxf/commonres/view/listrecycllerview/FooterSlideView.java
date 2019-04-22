package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.commonres.R;


/**
 * Created by Administrator on 2016/7/26.
 */
public class FooterSlideView extends RefreshSlideLayout.SlideRefreshView {
    private View view;
    private int releaseLoadOffset = 40;
    private int maxPullOffset = 70;
    private TextView info;
    private ProgressBar progressBar;

    public FooterSlideView(Context context) {
        super(context);
        releaseLoadOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, releaseLoadOffset,
                getResources().getDisplayMetrics());
        maxPullOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxPullOffset, getResources()
                .getDisplayMetrics());
        view = LayoutInflater.from(context).inflate(R.layout.pull_refresh_list_recycler_footer_layout, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(view, lp);
        info = (TextView) findViewById(R.id.list_recycler_view_tv_loading_info);
        progressBar = (ProgressBar) findViewById(R.id.list_recycler_view_progressBar);

        setBackgroundColor(0xfff7f7f7);
        onPullStatusChanged(RefreshSlideLayout.STATUS_NORMAL);
    }

    @Override
    public void onPullStatusChanged(int status) {
        switch (status) {
            case RefreshSlideLayout.STATUS_NORMAL:
                info.setText(R.string.refresh_slide_loading_normal);
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case RefreshSlideLayout.STATUS_RELEASE_LOAD:
                info.setText(R.string.refresh_slide_loading_release_load);

                break;
            case RefreshSlideLayout.STATUS_LOADING:
                info.setText(R.string.refresh_slide_loading_loading);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case RefreshSlideLayout.STATUS_COMPLETE:
                info.setText(R.string.refresh_slide_loading_complete);
                progressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public int getReleaseLoadOffset() {
        return releaseLoadOffset;
    }

    @Override
    public int getMaxOffset() {
        return maxPullOffset;
    }


    @Override
    public void onSlideOffsetChanged(int offset, int offsetChanged, boolean isPerform) {
        if (!isPerform) {
            float f = 1f - (float) this.mOffset / (float) getMaxOffset();
            f = f < 0.2f ? 0.2f : f;
//            offsetChanged *= f;
            offsetChanged= Math.round(offsetChanged*f);
            this.mOffset+=offsetChanged;
        } else
            this.mOffset = offset;
    }


}

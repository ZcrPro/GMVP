package com.zhihuianxin.xyaxf.commonres.view.listrecycllerview;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.commonres.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2016/7/26.
 */
public class HeaderSlideView extends RefreshSlideLayout.SlideRefreshView {
    private View view;
    private int releaseLoadOffset = 50;
    private int maxPullOffset = 80;
    private TextView info;
    private TextView time;
    private ProgressBar progressBar;

    public HeaderSlideView(Context context) {
        super(context);
        releaseLoadOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, releaseLoadOffset,
                getResources().getDisplayMetrics());
        maxPullOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxPullOffset, getResources()
                .getDisplayMetrics());
        view = LayoutInflater.from(context).inflate(R.layout.pull_refresh_list_recycler_header_layout, null);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
                .LayoutParams.MATCH_PARENT);
        addView(view, lp);
        info = (TextView) findViewById(R.id.list_recycler_view_tv_loading_info);
        time = (TextView) findViewById(R.id.list_recycler_view_tv_loading_last_update_time);
        progressBar = (ProgressBar) findViewById(R.id.list_recycler_view_progressBar);
        setBackgroundColor(0xfff7f7f7);//0xfff7f7f7
        onPullStatusChanged(RefreshSlideLayout.STATUS_NORMAL);
    }

    private void setUpdateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        time.setText(getResources().getString(R.string.refresh_slide_refresh_last_update, sdf.format(new Date())));
    }

    @Override
    public void onPullStatusChanged(int status) {
        Log.i("", "onPullStatusChanged status=" + status);
        switch (status) {
            case RefreshSlideLayout.STATUS_NORMAL:
                info.setText(R.string.refresh_slide_refresh_normal);
                time.setVisibility(View.VISIBLE);
                setUpdateTime();
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case RefreshSlideLayout.STATUS_RELEASE_LOAD:
                info.setText(R.string.refresh_slide_refresh_release_load);

                break;
            case RefreshSlideLayout.STATUS_LOADING:
                info.setText(R.string.refresh_slide_refresh_loading);
                time.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case RefreshSlideLayout.STATUS_COMPLETE:
                info.setText(R.string.refresh_slide_refresh_complete);
                time.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onAttachToParent(int maybeMaxOffset) {
        super.onAttachToParent(maybeMaxOffset);
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
            offsetChanged = Math.round(offsetChanged * f);
            this.mOffset += offsetChanged;
        } else
            this.mOffset = offset;

    }


}

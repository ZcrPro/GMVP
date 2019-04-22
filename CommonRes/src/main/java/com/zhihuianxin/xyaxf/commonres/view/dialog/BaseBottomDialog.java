package com.zhihuianxin.xyaxf.commonres.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.zhihuianxin.xyaxf.commonres.R;

public class BaseBottomDialog extends BaseDialog {
    public BaseBottomDialog(Context context) {
        super(context);
        setContentAnimations(R.style.BaseDialogBottomSlideAnim);

    }

    @Override
    protected FrameLayout.LayoutParams generateDefaultContentLayoutParams() {
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
    }
}

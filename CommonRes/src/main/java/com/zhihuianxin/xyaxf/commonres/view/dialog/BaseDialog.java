package com.zhihuianxin.xyaxf.commonres.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.zhihuianxin.xyaxf.commonres.R;

public abstract class BaseDialog extends Dialog {
    FrameLayout dialogBackground;
    Animation dialogBgShowAnim, dialogBgDismissAnim;
    Animation contentShowAnim, contentDismissAnim;
    private View dialogContentView;

    public BaseDialog(Context context) {
        super(context, R.style.AppBaseDialogStyle_NoAnim);
        getWindow().setWindowAnimations(R.style.AppBaseDialogStyle);
        dialogBgShowAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.base_dialog_bg_fade_in_anim);
        dialogBgDismissAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.base_dialog_bg_fade_out_anim);
        dialogBgDismissAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dialogBgShowAnim=null;
                dialogBgDismissAnim=null;
                contentDismissAnim=null;
                contentShowAnim=null;
                BaseDialog.super.dismiss();//结束动画结束的时候调用dialog的dismiss
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dialogBackground = new FrameLayout(context);
        dialogBackground.setBackgroundColor(ContextCompat.getColor(context,
                R.color.axf_base_dialog_background));
        dialogBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        WindowManager.LayoutParams layoutParams=getWindow().getAttributes();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        layoutParams.verticalMargin=0;
        layoutParams.height=height;
        layoutParams.width=width;
        getWindow().setAttributes(layoutParams);
        super.setContentView(dialogBackground, new ViewGroup.LayoutParams(width, height));
    }

    /**
     * 生成默认的内容布局样式
     *
     * @return
     */
    protected FrameLayout.LayoutParams generateDefaultContentLayoutParams() {
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
    }

    @Override
    public void show() {
        super.show();
        dialogBackground.clearAnimation();
        if (contentShowAnim == null)
            contentShowAnim = AnimationUtils.loadAnimation(getContext(), R.anim.base_dialog_content_scale_in_anim);
        dialogBgShowAnim.setDuration(contentShowAnim.getDuration());
        dialogBackground.startAnimation(dialogBgShowAnim);
        dialogContentView.startAnimation(contentShowAnim);
    }

    @Override
    public void dismiss() {
        dialogBackground.clearAnimation();
        if (contentDismissAnim == null)
            contentDismissAnim = AnimationUtils.loadAnimation(getContext(), R.anim.base_dialog_content_scale_out_anim);
        dialogBgDismissAnim.setDuration(contentDismissAnim.getDuration());
        dialogBackground.startAnimation(dialogBgDismissAnim);
        dialogContentView.startAnimation(contentDismissAnim);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view, generateDefaultContentLayoutParams());
    }

    @Override
    public void setContentView(int layoutResID) {
        View view = LayoutInflater.from(getContext()).inflate(layoutResID, null);
        setContentView(view, generateDefaultContentLayoutParams());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        dialogContentView = view;
        dialogContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        if (params == null)
            params = generateDefaultContentLayoutParams();
        addContentView(view, params);
    }

    /**
     * 设置对话框背景颜色
     *
     * @param color
     */
    public void setDialogBackgroundColor(@ColorInt int color) {
        this.dialogBackground.setBackgroundColor(color);
    }

    /**
     * 设置内容试图的动画
     * @param style
     */
    public void setContentAnimations(@StyleRes int style) {
        TypedArray ta = getContext().obtainStyledAttributes(style,
                new int[]{android.R.attr.windowEnterAnimation, android.R.attr.windowExitAnimation});
        if (ta != null) {
            contentShowAnim = AnimationUtils.loadAnimation(getContext(),
                    ta.getResourceId(0, R.anim.base_dialog_content_scale_in_anim));
            contentDismissAnim = AnimationUtils.loadAnimation(getContext(),
                    ta.getResourceId(1, R.anim.base_dialog_content_scale_out_anim));
            ta.recycle();
        }
    }
//    @Override
//    public void addContentView(View view, ViewGroup.LayoutParams params) {
//        dialogContainer.addView(view,params);
//    }

}

package com.zhihuianxin.xyaxf.commonres.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhihuianxin.xyaxf.commonres.R;

public class AppMessageDialog extends BaseDialog {
    ImageView icon;
    TextView message;
    TextView btnNegative;
    TextView btnNeutral;
    TextView btnPositive;

    public AppMessageDialog(Context context) {
        super(context);
        setContentView(R.layout.app_message_dialog);
        icon = findViewById(R.id.app_message_dialog_icon);
        message = findViewById(R.id.app_message_dialog_content);
        btnNeutral = findViewById(R.id.app_message_dialog_btn_neutral);
        btnPositive = findViewById(R.id.app_message_dialog_btn_positive);
        btnNegative = findViewById(R.id.app_message_dialog_btn_negative);

        btnNeutral.setVisibility(View.GONE);
        btnPositive.setVisibility(View.GONE);
        btnNegative.setVisibility(View.GONE);
        ((View) btnNegative.getParent()).setVisibility(View.GONE);
    }

    /**
     * 设置信息
     *
     * @param message
     * @return
     */
    public AppMessageDialog setMessage(String message) {
        this.message.setText(message);
        return this;
    }

    /**
     * 设置 PositiveButton 的显示文字和点击事件，蓝色
     *
     * @param name
     * @param clickListener
     * @return
     */
    public AppMessageDialog setPositiveButton(String name, DialogInterface.OnClickListener clickListener) {
        setButton(btnPositive, name, clickListener, DialogInterface.BUTTON_POSITIVE);
        return this;
    }

    /**
     * 设置 NegativeButton 的显示文字和点击事件，灰色
     *
     * @param name
     * @param clickListener
     * @return
     */
    public AppMessageDialog setNegativeButton(String name, DialogInterface.OnClickListener clickListener) {
        setButton(btnNegative, name, clickListener, DialogInterface.BUTTON_NEGATIVE);
        return this;
    }

    /**
     * 设置 NeutralButton 的显示文字和点击事件，绿色
     *
     * @param name
     * @param clickListener
     * @return
     */
    public AppMessageDialog setNeutralButton(String name, DialogInterface.OnClickListener clickListener) {
        setButton(btnNeutral, name, clickListener, DialogInterface.BUTTON_NEUTRAL);
        return this;
    }

    private void setButton(TextView btn, String name, DialogInterface.OnClickListener listener, int witch) {
        if (!TextUtils.isEmpty(name)) {
            btn.setText(name);
            btn.setOnClickListener(new DialogOnClickInvoker(listener, this, witch));
            btn.setVisibility(View.VISIBLE);
        } else {
            btn.setOnClickListener(null);
            btn.setVisibility(View.GONE);

        }
        int visibleCount = 0;
        ViewGroup viewGroup = (ViewGroup) btn.getParent();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i).getVisibility() == View.VISIBLE) {
                visibleCount++;
            }
        }
        if (visibleCount == 0)
            viewGroup.setVisibility(View.GONE);
        else {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
            lp.leftMargin = getContext().getResources().getDimensionPixelSize(visibleCount == 1 ?
                    R.dimen.widget_size_40 : R.dimen.widget_size_10);
            lp.rightMargin = lp.leftMargin;
            viewGroup.setLayoutParams(lp);
            viewGroup.setVisibility(View.VISIBLE);
        }
    }

    private static class DialogOnClickInvoker implements View.OnClickListener {
        DialogInterface.OnClickListener mOnClickListener;
        private Dialog mDialog;
        private int buttonType;


        public DialogOnClickInvoker(OnClickListener onClickListener, Dialog dialog, int buttonType) {
            mOnClickListener = onClickListener;
            mDialog = dialog;
            this.buttonType = buttonType;
        }

        @Override
        public void onClick(View v) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(mDialog, buttonType);
            } else {
                mDialog.dismiss();
            }

        }
    }
}

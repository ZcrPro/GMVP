package com.zhihuianxin.xyaxf.commonres.view.dialog;

import android.app.Dialog;
import android.content.Context;

import com.zhihuianxin.xyaxf.commonres.R;

public class AppLoadingDialog extends Dialog {
    public AppLoadingDialog( Context context) {
        super(context,R.style.AppBaseDialogStyle);
        setContentView(R.layout.app_loading_dialog);
    }
}

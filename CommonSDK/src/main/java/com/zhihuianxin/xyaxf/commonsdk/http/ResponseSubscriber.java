package com.zhihuianxin.xyaxf.commonsdk.http;

import com.jess.arms.mvp.IView;
import com.zhihuianxin.xyaxf.commonsdk.converter.ResponseResult;
import com.zhihuianxin.xyaxf.commonsdk.http.BaseResponse;

import org.xml.sax.ErrorHandler;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber;
import me.jessyan.rxerrorhandler.handler.ErrorHandlerFactory;

public abstract class ResponseSubscriber<T extends ResponseResult> extends ErrorHandleSubscriber<T> {

    IView mView;

    public ResponseSubscriber(RxErrorHandler rxErrorHandler) {
        super(rxErrorHandler);
    }

    public ResponseSubscriber(RxErrorHandler rxErrorHandler, IView view) {
        super(rxErrorHandler);
        mView = view;
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
        if (mView != null)
            mView.showLoading();
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable t) {
        super.onError(t);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        if (mView != null)
            mView.hideLoading();
    }
}

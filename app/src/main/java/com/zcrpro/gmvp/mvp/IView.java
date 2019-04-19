/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zcrpro.gmvp.mvp;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.zcrpro.gmvp.utils.ArmsUtils;

import static com.zcrpro.gmvp.utils.Preconditions.checkNotNull;

/**
 * ================================================
 * 框架要求框架中的每个 View 都需要实现此类, 以满足规范
 *
 * 为了满足部分人的诉求以及向下兼容, {@link IView} 中的部分方法使用 JAVA 1.8 的默认方法实现, 这样实现类可以按实际需求选择是否实现某些方法
 * 不实现则使用默认方法中的逻辑, 不清楚默认方法的请自行学习
 *
 * Created by zcrpro on 2019-04-19
 * <a href="mailto:zcrpro@gmail.com">Contact me</a>
 * <a href="https://github.com/ZcrPro/GMVP">Follow me</a>
 * ================================================
 */
public interface IView {

    /**
     * 显示加载
     */
    default void showLoading() {

    }

    /**
     * 隐藏加载
     */
    default void hideLoading() {

    }

    /**
     * 显示信息
     *
     * @param message 消息内容, 不能为 {@code null}
     */
    void showMessage(@NonNull String message);

    /**
     * 跳转 {@link Activity}
     *
     * @param intent {@code intent} 不能为 {@code null}
     */
    default void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    /**
     * 杀死自己
     */
    default void killMyself() {

    }
}

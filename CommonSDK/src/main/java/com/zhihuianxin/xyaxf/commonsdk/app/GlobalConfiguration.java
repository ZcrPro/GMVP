/*
 * Copyright 2018 JessYan
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
package com.zhihuianxin.xyaxf.commonsdk.app;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.di.module.ClientModule;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.http.log.RequestInterceptor;
import com.jess.arms.integration.ConfigModule;
import com.zhihuianxin.xyaxf.commonsdk.BuildConfig;
import com.zhihuianxin.xyaxf.commonsdk.converter.AXFConverterFactory;
import com.zhihuianxin.xyaxf.commonsdk.http.SSLSocketClient;
import com.zhihuianxin.xyaxf.commonsdk.imgaEngine.Strategy.CommonGlideImageLoaderStrategy;
import com.zhihuianxin.xyaxf.commonsdk.mvp.model.api.Api;

import java.util.List;

import butterknife.ButterKnife;

import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import timber.log.Timber;


/**
 * ================================================
 * CommonSDK 的 GlobalConfiguration 含有有每个组件都可公用的配置信息, 每个组件的 AndroidManifest 都应该声明此 ConfigModule
 *
 * @see <a href="https://github.com/JessYanCoding/ArmsComponent/wiki#3.3">ConfigModule wiki 官方文档</a>
 * Created by JessYan on 30/03/2018 17:16
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * ================================================
 */
public class GlobalConfiguration implements ConfigModule {

    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        if (!BuildConfig.LOG_DEBUG) //Release 时,让框架不再打印 Http 请求和响应的信息
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE);
        builder.baseurl(Api.APP_DOMAIN)
                .imageLoaderStrategy(new CommonGlideImageLoaderStrategy())
                .globalHttpHandler(new GlobalHttpHandlerImpl(context))
                .responseErrorListener(new ResponseErrorListenerImpl())
                .retrofitConfiguration(new ClientModule.RetrofitConfiguration() {
                    @Override
                    public void configRetrofit(Context context, Retrofit.Builder builder) {
                        builder.addConverterFactory(AXFConverterFactory.create(context.getApplicationContext()));
                    }
                })
                .okhttpConfiguration(new ClientModule.OkhttpConfiguration() {
                    @Override
                    public void configOkhttp(Context context, OkHttpClient.Builder builder) {
                        builder.sslSocketFactory(SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getTrustManager());
                        builder.hostnameVerifier(SSLSocketClient.getHostnameVerifier());
                        //让 Retrofit 同时支持多个 BaseUrl 以及动态改变 BaseUrl. 详细使用请方法查看 https://github.com/JessYanCoding/RetrofitUrlManager
                        RetrofitUrlManager.getInstance().with(builder);
                    }
                })
                .rxCacheConfiguration((context1, rxCacheBuilder) -> {//这里可以自己自定义配置RxCache的参数
                    rxCacheBuilder.useExpiredDataIfLoaderNotAvailable(true);
                    return null;
                });
    }

    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {
        // AppDelegate.Lifecycle 的所有方法都会在基类Application对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        lifecycles.add(new AppLifecyclesImpl());
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        lifecycles.add(new ActivityLifecycleCallbacksImpl());
    }

    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
        lifecycles.add(new FragmentManager.FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                // 在配置变化的时候将这个 Fragment 保存下来,在 Activity 由于配置变化重建是重复利用已经创建的Fragment。
                // https://developer.android.com/reference/android/app/Fragment.html?hl=zh-cn#setRetainInstance(boolean)
                // 在 Activity 中绑定少量的 Fragment 建议这样做,如果需要绑定较多的 Fragment 不建议设置此参数,如 ViewPager 需要展示较多 Fragment
                f.setRetainInstance(true);
            }
        });
    }
}

/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bingoogolapple.scaffolding.demo.rxjava.util;

import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.scaffolding.net.ApiException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 下午12:29
 * 描述:
 */
public class RxUtil {
    private RxUtil() {
    }

    /**
     * 主线程、生命周期绑定、错误重试
     *
     * @param lifecycleProvider
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> mainThreadLifecycleRetry(LifecycleProvider lifecycleProvider) {
        return observable -> observable.observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
                    private int mRetryCount;

                    @Override
                    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {
                        return throwableObservable.flatMap(throwable -> {
                            if (throwable instanceof IOException || throwable instanceof SocketException) { // 网络异常重试3次
                                mRetryCount++;
                                if (mRetryCount > 3) {
                                    Logger.d("错误超过3次");
                                    return Observable.error(throwable);
                                } else {
                                    Logger.d("错误" + mRetryCount + "次");
                                    return Observable.timer(mRetryCount * 1000, TimeUnit.MILLISECONDS);
                                }
                            } else if (throwable instanceof ApiException) {
                                if (((ApiException) throwable).getCode() == 401) {
                                    return UploadManager.getInstance().getImageTokenObservable();
                                }
                                return Observable.error(throwable);
                            } else { // 未知异常直接返回发送 error 的 Observable
                                Logger.d("未知异常");
                                throwable.printStackTrace();
                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .compose(lifecycleProvider.bindToLifecycle());
    }

    public static <T> Observable<T> runInUIThreadDelay(@NonNull T t, long delayMillis, LifecycleProvider lifecycleProvider) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).compose(lifecycleProvider.bindToLifecycle());
    }

    public static Observable<Object> runInUIThreadDelay(long delayMillis, LifecycleProvider lifecycleProvider) {
        return runInUIThreadDelay(new Object(), delayMillis, lifecycleProvider);
    }
}
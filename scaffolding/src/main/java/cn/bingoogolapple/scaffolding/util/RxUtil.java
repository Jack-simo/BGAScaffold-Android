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

package cn.bingoogolapple.scaffolding.util;

import android.support.annotation.NonNull;

import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 下午12:29
 * 描述:
 */
public class RxUtil {
    private RxUtil() {
    }

    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> ObservableTransformer<T, T> applySchedulersBindToLifecycle(LifecycleProvider lifecycleProvider) {
        if (lifecycleProvider == null) {
            return observable -> observable.compose(RxUtil.applySchedulers());
        } else {
            return observable -> observable.compose(RxUtil.applySchedulers()).compose(lifecycleProvider.bindToLifecycle());
        }
    }

    public static <T> ObservableTransformer<NetResult<T>, T> applySchedulersAndFlatMapResult() {
        return observable -> observable.compose(RxUtil.applySchedulers()).flatMap(new Function<NetResult<T>, Observable<T>>() {
            @Override
            public Observable<T> apply(NetResult<T> result) {
                return handleCode(result);
            }
        });
    }

    public static <T> ObservableTransformer<NetResult<T>, T> applySchedulersBindToLifecycleAndFlatMapResult(LifecycleProvider lifecycleProvider) {
        return observable -> observable.compose(RxUtil.applySchedulersBindToLifecycle(lifecycleProvider)).flatMap(new Function<NetResult<T>, Observable<T>>() {
            @Override
            public Observable<T> apply(NetResult<T> result) {
                return handleCode(result);
            }
        });
    }

    private static <T> Observable<T> handleCode(NetResult<T> result) {
        if (result.code == 0) {
            return Observable.just(result.data);
        } else {
            return Observable.error(new ApiException(result.msg, result.code));
        }
    }

    public static <T> Observable<T> runInUIThreadDelay(@NonNull T t, long delayMillis, LifecycleProvider lifecycleProvider) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).compose(lifecycleProvider.bindToLifecycle());
    }

    public static Observable<Object> runInUIThreadDelay(long delayMillis, LifecycleProvider lifecycleProvider) {
        return runInUIThreadDelay(new Object(), delayMillis, lifecycleProvider);
    }
}
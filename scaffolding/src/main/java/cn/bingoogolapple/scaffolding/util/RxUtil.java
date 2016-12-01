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

import android.content.Context;

import com.trello.rxlifecycle.LifecycleProvider;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 下午12:29
 * 描述:
 */
public class RxUtil {
    private RxUtil() {
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable.Transformer<T, T> applySchedulersBindToLifecycle(LifecycleProvider lifecycleProvider) {
        if (lifecycleProvider == null) {
            return observable -> observable.compose(RxUtil.applySchedulers());
        } else {
            return observable -> observable.compose(RxUtil.applySchedulers()).compose(lifecycleProvider.bindToLifecycle());
        }
    }

    public static <T> Observable.Transformer<NetResult<T>, T> applySchedulersAndFlatMapResult() {
        return observable -> observable.compose(RxUtil.applySchedulers()).flatMap(new Func1<NetResult<T>, Observable<T>>() {
            @Override
            public Observable<T> call(NetResult<T> result) {
                return handleCode(result);
            }
        });
    }

    public static <T> Observable.Transformer<NetResult<T>, T> applySchedulersBindToLifecycleAndFlatMapResult(LifecycleProvider lifecycleProvider) {
        return observable -> observable.compose(RxUtil.applySchedulersBindToLifecycle(lifecycleProvider)).flatMap(new Func1<NetResult<T>, Observable<T>>() {
            @Override
            public Observable<T> call(NetResult<T> result) {
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

    public static <T> Observable<T> runInUIThread(T t) {
        return Observable.just(t).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> runInUIThreadDelay(T t, long delayMillis) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> runInUIThreadDelay(T t, long delayMillis, LifecycleProvider lifecycleProvider) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).compose(lifecycleProvider.bindToLifecycle());
    }

    public static Observable<Void> runInUIThread() {
        return runInUIThread(null);
    }

    public static Observable<Void> runInUIThreadDelay(long delayMillis) {
        return runInUIThreadDelay(null, delayMillis);
    }

    public static Observable<Void> runInUIThreadDelay(long delayMillis, LifecycleProvider lifecycleProvider) {
        return runInUIThreadDelay(null, delayMillis, lifecycleProvider);
    }

    public static <T> Observable<T> runInIoThread(T t) {
        return Observable.just(t).observeOn(Schedulers.io());
    }

    public static Observable<Void> runInIoThread() {
        return runInIoThread(null);
    }

    public static <T> Observable<T> runInIoThreadDelay(T t, long delayMillis) {
        return Observable.just(t).delaySubscription(delayMillis, TimeUnit.MILLISECONDS, Schedulers.io());
    }

    public static Observable<Void> runInIoThreadDelay(long delayMillis) {
        return runInIoThreadDelay(null, delayMillis);
    }

    public static <T> Observable<T> load(final Context context, final String cacheKey, final long expireTime, Observable<T> fromNetworkObservable, boolean forceRefresh) {
        Observable<T> fromCacheObservable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                T cache = (T) CacheManager.readObject(context, cacheKey, expireTime);
                if (cache != null) {
                    subscriber.onNext(cache);
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        fromNetworkObservable = fromNetworkObservable.map(new Func1<T, T>() {
            @Override
            public T call(T result) {
                CacheManager.saveObject(context, (Serializable) result, cacheKey);
                return result;
            }
        });
        if (forceRefresh) {
            return fromNetworkObservable;
        } else {
            return Observable.concat(fromCacheObservable, fromNetworkObservable).first();
        }

    }
}
package cn.bingoogolapple.basenote.util;

import android.content.Context;

import java.io.Serializable;

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

    public static <T> Observable.Transformer<NetResult<T>, T> flatMapResultAndApplySchedulers() {
        return new Observable.Transformer<NetResult<T>, T>() {
            @Override
            public Observable<T> call(Observable<NetResult<T>> observable) {
                return observable.flatMap(new Func1<NetResult<T>, Observable<T>>() {
                    @Override
                    public Observable<T> call(NetResult<T> result) {
                        if (result.code == 0) {
                            return Observable.just(result.data);
                        } else {
                            return Observable.error(new ServerException(result.msg));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable<T> load(final Context context, final String cacheKey, final long expireTime, Observable<T> fromNetworkObservable, boolean forceRefresh) {
        Observable<T> fromCacheObservable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                T cache = (T) CacheManager.readObject(context, cacheKey, expireTime);
                if (cache != null) {
                    subscriber.onNext(cache);
                } else {
                    subscriber.onCompleted();
                }
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
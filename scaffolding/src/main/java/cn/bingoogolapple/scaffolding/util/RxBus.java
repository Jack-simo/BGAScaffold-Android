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

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/2 上午12:23
 * 描述:使用 RxJava 来实现 EventBus
 */
public class RxBus {
    private Subject<Object, Object> mBus;
    private static RxBus sInstance;

    private RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());
    }

    public static RxBus getInstance() {
        if (sInstance == null) {
            // [1]
            synchronized (RxBus.class) {
                if (sInstance == null) {
                    //单例模式之双重检测：线程一在此之前线程二到达了位置[1],如果此处不二次判断，那么线程二执行到这里的时候还会重新new
                    sInstance = new RxBus();
                }
            }
        }
        return sInstance;
    }

    private Subject<Object, Object> getBus() {
        return mBus;
    }

    public static boolean hasObservers() {
        return getInstance().getBus().hasObservers();
    }

    public static void send(Object obj) {
        if (getInstance().hasObservers()) {
            getInstance().getBus().onNext(obj);
        }
    }

    public static Observable<Object> toObservable() {
        return getInstance().getBus();
    }

    public static <T> Observable<T> toObservable(Class<T> clazz) {
        return getInstance().toObservable().ofType(clazz).observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> toObservableAndBindToLifecycle(Class<T> clazz, LifecycleProvider lifecycleProvider) {
        return getInstance().toObservable(clazz).compose(lifecycleProvider.bindToLifecycle());
    }

    public static <T> Observable<T> toObservableAndBindUntilStop(Class<T> clazz, LifecycleProvider lifecycleProvider) {
        return getInstance().toObservable(clazz).compose(lifecycleProvider.bindUntilEvent(lifecycleProvider instanceof RxAppCompatActivity ? ActivityEvent.STOP : FragmentEvent.STOP));
    }
}
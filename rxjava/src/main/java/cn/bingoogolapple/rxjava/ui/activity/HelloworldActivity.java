package cn.bingoogolapple.rxjava.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.engine.Engine;
import cn.bingoogolapple.rxjava.model.Course;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import cn.bingoogolapple.rxjava.model.Student;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class HelloworldActivity extends TitlebarActivity {
    private Engine mEngine;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_helloworld);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("Helloworld");

        mEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(Engine.class);
    }

    @Override
    public void onClick(View v) {
    }

    /**
     * 在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只有一个，并且是事件序列中的最后一个。
     * 需要注意的是，onCompleted() 和 onError() 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。
     */
    private Observer mObserver = new Observer<String>() {
        @Override
        public void onNext(String s) {
            Logger.i(TAG, "onNext " + s + " ThreadName:" + Thread.currentThread().getName());
        }

        /**
         * 事件队列完结。RxJava 不仅把每个事件单独处理，还会把它们看做一个队列。
         * RxJava 规定，当不会再有新的 onNext() 发出时，需要触发 onCompleted() 方法作为标志
         */
        @Override
        public void onCompleted() {
            Logger.i(TAG, "onCompleted ThreadName:" + Thread.currentThread().getName());
        }

        /**
         * 事件队列异常。在事件处理过程中出异常时，onError() 会被触发，同时队列自动终止，不允许再有事件发出。
         * @param e
         */
        @Override
        public void onError(Throwable e) {
            Logger.i(TAG, "onError ThreadName:" + Thread.currentThread().getName());
        }
    };

    private Subscriber mSubscriber = new Subscriber<String>() {
        /**
         * 在 subscribe 刚开始，而事件还未发送之前被调用，可以用于做一些准备工作，例如数据的清零或重置。
         * 这是一个可选方法，默认情况下它的实现为空。
         * 需要注意的是，如果对准备工作的线程有要求（例如弹出一个显示进度的对话框，这必须在主线程执行），
         * onStart() 就不适用了，因为它总是在 subscribe 所发生的线程被调用，而不能指定线程。
         */
        @Override
        public void onStart() {
            Logger.i(TAG, "onStart ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onNext(String s) {
            Logger.i(TAG, "onNext " + s + " ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onCompleted() {
            Logger.i(TAG, "onCompleted ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onError(Throwable e) {
            Logger.i(TAG, "onError ThreadName:" + Thread.currentThread().getName());
        }
    };

    public void test1(View v) {
        /**
         * 在 RxJava 中， Observable 并不是在创建的时候就立即开始发送事件，而是在它被订阅的时候，
         * 即当 Observable 被订阅的时候(包括subscribe方法不传任何参数时)，OnSubscribe 的 call() 方法会自动被调用
         *
         * subscribe() 这个方法有点怪,它看起来是『observalbe 订阅了 observer / subscriber』
         * 而不是『observer / subscriber 订阅了 observalbe』，这看起来就像『杂志订阅了读者』一样颠倒了对象关系。
         * 这让人读起来有点别扭，不过如果把 API 设计成 observer.subscribe(observable) / subscriber.subscribe(observable) ，
         * 虽然更加符合思维逻辑，但对流式 API 的设计就造成影响了，比较起来明显是得不偿失的
         */
        Logger.i(TAG, "method ThreadName:" + Thread.currentThread().getName());
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i(TAG, "call ThreadName:" + Thread.currentThread().getName());
                subscriber.onNext("Hello");
                subscriber.onNext("RxJava");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())  // 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。
                .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
                .subscribe(mObserver);


        /**
         * Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
         * Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
         * Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
         * Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
         * 另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
         */
    }

    public void test2(View v) {
//        executeTest2();
        new Thread() {
            @Override
            public void run() {
                executeTest2();
            }
        }.start();
    }

    private void executeTest2() {
        Logger.i(TAG, "method ThreadName:" + Thread.currentThread().getName());
        final Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i(TAG, "call ThreadName:" + Thread.currentThread().getName());
                subscriber.onNext("Hello");
                subscriber.onCompleted();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1() {
                    @Override
                    public void call(Object o) {
                        Logger.i(TAG, "doOnNext call:" + Thread.currentThread().getName());
                    }
                })  // doOnNext中Action1的call方法所在线程受上一个observeOn影响,否则就是第一个subscribeOn指定的线程
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

//        subscribeTest2(observable);
        new Thread() {
            @Override
            public void run() {
                subscribeTest2(observable);
            }
        }.start();
    }

    private void subscribeTest2(Observable observable) {
        Logger.i(TAG, "subscribe ThreadName:" + Thread.currentThread().getName());
        observable.subscribe(mSubscriber);
    }

    public void test3(View v) {
        Observable.just("Hello", "RxJava", "RxAndroid").observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    public void test4(View v) {
        String[] words = {"Hello", "RxJava", "RxAndroid"};
        Observable.from(words).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(mSubscriber);
    }

    public void test5(View v) {
        // 除了 subscribe(Observer) 和 subscribe(Subscriber) ，subscribe() 还支持不完整定义的回调
        Action1<String> onNextAction = new Action1<String>() {
            // onNext()
            @Override
            public void call(String s) {
                Logger.i(TAG, "onNext " + s + " ThreadName:" + Thread.currentThread().getName());
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                Logger.i(TAG, "onError ThreadName:" + Thread.currentThread().getName());
            }
        };
        Action0 onCompletedAction = new Action0() {
            // onCompleted()
            @Override
            public void call() {
                Logger.i(TAG, "onCompleted ThreadName:" + Thread.currentThread().getName());
            }
        };
        Observable observable = Observable.just("Hello", "RxJava", "RxAndroid").observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
        observable.subscribe(onNextAction);
        observable.subscribe(onNextAction, onErrorAction);
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    public void test6(View v) {
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Drawable>() {
            @Override
            public void onNext(Drawable drawable) {
                ((ImageView) findViewById(R.id.iv_helloworld_test)).setImageDrawable(drawable);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.show("加载图片失败");
            }
        });
    }

    public void test7(View v) {

    }

    /**
     * 事件对象的直接变换
     */
    private void map() {
        Observable.just("").map(new Func1<String, Bitmap>() {
            @Override
            public Bitmap call(String s) {
                return null;
            }
        }).subscribe(new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {

            }
        });
    }

    private void flatMap() {
        Student[] students = null;
        Observable.from(students).flatMap(new Func1<Student, Observable<Course>>() {
            @Override
            public Observable<Course> call(Student student) {
                return Observable.from(student.courses);
            }
        }).subscribe(new Subscriber<Course>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Course course) {
            }
        });

        /**
         Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
         .subscribeOn(Schedulers.io())
         .observeOn(Schedulers.newThread())
         .map(mapOperator) // 新线程，由 observeOn() 指定
         .observeOn(Schedulers.io())
         .map(mapOperator2) // IO 线程，由 observeOn() 指定
         .observeOn(AndroidSchedulers.mainThread)
         .subscribe(subscriber);  // Android 主线程，由 observeOn() 指定


         不同于 observeOn() ， subscribeOn() 的位置放在哪里都可以，但它是只能调用一次的
         */
    }

    public void test8(View v) {
        mEngine.loadMoreDataRx(1)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadingDialog(R.string.loading);
                        Logger.i(TAG, "doOnSubscribe call:" + Thread.currentThread().getName());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())  // 用于指定前面那个doOnSubscribe中Action0的call方法在UI线程执行
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<RefreshModel>>() {
                    @Override
                    public void call(List<RefreshModel> refreshModels) {
                        Logger.i(TAG, "doOnNext call:" + Thread.currentThread().getName());
                    }
                }) // doOnNext中Action1的call方法所在线程受上一个observeOn影响,否则就是第一个subscribeOn指定的线程
                .observeOn(Schedulers.io())
                .flatMap(new Func1<List<RefreshModel>, Observable<RefreshModel>>() {
                    @Override
                    public Observable<RefreshModel> call(List<RefreshModel> refreshModels) {
                        Logger.i(TAG, "flatMap call:" + Thread.currentThread().getName());
                        return Observable.from(refreshModels);
                    }
                }) // flatMap中Func1的call方法所在线程受上一个observeOn影响,否则就是第一个subscribeOn指定的线程
                .filter(new Func1<RefreshModel, Boolean>() {
                    @Override
                    public Boolean call(RefreshModel refreshModel) {
                        return !refreshModel.title.contains("4");
                    }
                }) // filter()输出和输入相同的元素，并且会过滤掉那些不满足检查条件的
                .take(3)  // take()输出最多指定数量的结果。
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefreshModel>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载失败");
                    }

                    @Override
                    public void onNext(RefreshModel refreshModel) {
                        Logger.i(TAG, refreshModel.title);
                    }
                });
    }

    public void test9(View v) {
//        Observable.zip(mEngine.loadInitDatasRx(), mEngine.loadMoreDataRx(1), new Func2<List<RefreshModel>, List<RefreshModel>, List<RefreshModel>>() {
//            @Override
//            public List<RefreshModel> call(List<RefreshModel> refreshModels, List<RefreshModel> refreshModels2) {
//                refreshModels.addAll(refreshModels2);
//                return refreshModels;
//            }
//        })

//        Observable.merge(mEngine.loadInitDatasRx(), mEngine.loadMoreDataRx(1), mEngine.loadMoreDataRx(1)).subscribeOn(Schedulers.io())
        Observable.concat(mEngine.loadInitDatasRx(), mEngine.loadMoreDataRx(1), mEngine.loadMoreDataRx(1)).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadingDialog(R.string.loading);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<RefreshModel>, Observable<RefreshModel>>() {
                    @Override
                    public Observable<RefreshModel> call(List<RefreshModel> refreshModels) {
                        return Observable.from(refreshModels);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefreshModel>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载失败");
                    }

                    @Override
                    public void onNext(RefreshModel refreshModel) {
                        Logger.i(TAG, refreshModel.title);
                    }
                });
    }

    public void test10(View v) {
        newMethod(1).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadingDialog(R.string.loading);
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<RefreshModel>, Observable<RefreshModel>>() {
                    @Override
                    public Observable<RefreshModel> call(List<RefreshModel> refreshModels) {
                        return Observable.from(refreshModels);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RefreshModel>() {
                    @Override
                    public void onCompleted() {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoadingDialog();
                        ToastUtil.show("数据加载失败");
                    }

                    @Override
                    public void onNext(RefreshModel refreshModel) {
                        Logger.i(TAG, refreshModel.title);
                    }
                });
    }

    /**
     * Retrofit可以返回Observable对象，但是如果你使用的别的库并不支持这样怎么办？
     * 或者说一个内部的内码，你想把他们转换成Observable的？有什么简单的办法没？
     * 绝大多数时候Observable.just() 和 Observable.from() 能够帮助你从遗留代码中创建 Observable 对象
     * 如果oldMethod()足够快是没有什么问题的，但是如果很慢呢？调用oldMethod()将会阻塞住他所在的线程。
     * <p/>
     * 使用defer()来包装缓慢的代码
     */
    private Observable<List<RefreshModel>> newMethod(final int param) {
        return Observable.defer(new Func0<Observable<List<RefreshModel>>>() {
            @Override
            public Observable<List<RefreshModel>> call() {
                Logger.i(TAG, "defer call:" + Thread.currentThread().getName());
                try {
                    return Observable.just(oldMethod(param));
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    private List<RefreshModel> oldMethod(int page) throws IOException {
        return mEngine.loadMoreData(page).execute().body();
    }

    private void testConcat() {
        Observable<String> memory = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String memoryCache = "";
                if (memoryCache != null) {
                    subscriber.onNext(memoryCache);
                } else {
                    subscriber.onCompleted();
                }
            }
        });
        Observable<String> disk = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                String cachePref = "";
                if (!TextUtils.isEmpty(cachePref)) {
                    subscriber.onNext(cachePref);
                } else {
                    subscriber.onCompleted();
                }
            }
        });
        Observable<String> network = Observable.just("network");
        Observable.concat(memory, disk, network)
                .first()
                .subscribeOn(Schedulers.newThread())
                .subscribe();
    }

    public void test11(View v) {
        Observable
                .just("1", "2", "3", "3", "5", "6")
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return Integer.parseInt(s);
                    }
                })
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 1;
                    }
                })
                .distinct()
                .take(3)
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Logger.i(TAG, "和为 = " + integer);
                    }
                });
    }

}
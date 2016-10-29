package cn.bingoogolapple.rxjava.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.CalendarUtil;
import cn.bingoogolapple.basenote.util.NetResult;
import cn.bingoogolapple.basenote.util.RxUtil;
import cn.bingoogolapple.basenote.util.SimpleSubscriber;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.engine.RemoteServerEngine;
import cn.bingoogolapple.rxjava.model.Course;
import cn.bingoogolapple.rxjava.model.ModelCombine;
import cn.bingoogolapple.rxjava.model.ModelOne;
import cn.bingoogolapple.rxjava.model.ModelTwo;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import cn.bingoogolapple.rxjava.model.Student;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
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
    private RemoteServerEngine mRemoteServerEngine;

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

        mRemoteServerEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(RemoteServerEngine.class);
    }

    /**
     * 在一个正确运行的事件序列中, onCompleted() 和 onError() 有且只有一个，并且是事件序列中的最后一个。
     * 需要注意的是，onCompleted() 和 onError() 二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个。
     */
    private Observer mObserver = new Observer<String>() {
        @Override
        public void onNext(String s) {
            Logger.i("onNext " + s + " ThreadName:" + Thread.currentThread().getName());
        }

        /**
         * 事件队列完结。RxJava 不仅把每个事件单独处理，还会把它们看做一个队列。
         * RxJava 规定，当不会再有新的 onNext() 发出时，需要触发 onCompleted() 方法作为标志
         */
        @Override
        public void onCompleted() {
            Logger.i("onCompleted ThreadName:" + Thread.currentThread().getName());
        }

        /**
         * 事件队列异常。在事件处理过程中出异常时，onError() 会被触发，同时队列自动终止，不允许再有事件发出。
         * @param e
         */
        @Override
        public void onError(Throwable e) {
            Logger.i("onError ThreadName:" + Thread.currentThread().getName());
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
            Logger.i("onStart ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onNext(String s) {
            Logger.i("onNext " + s + " ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onCompleted() {
            Logger.i("onCompleted ThreadName:" + Thread.currentThread().getName());
        }

        @Override
        public void onError(Throwable e) {
            Logger.i("onError ThreadName:" + Thread.currentThread().getName());
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
        Logger.i("method ThreadName:" + Thread.currentThread().getName());
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("call ThreadName:" + Thread.currentThread().getName());
                subscriber.onNext("Hello");
                subscriber.onNext("World1");
                subscriber.onNext("World2");
//                subscriber.onError(new Throwable("测试错误"));
                subscriber.onNext("RxJava");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())  // 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Logger.i("doOnNext ThreadName:" + Thread.currentThread().getName());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.i("doOnError ThreadName:" + Thread.currentThread().getName());
                    }
                })
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
        Logger.i("method ThreadName:" + Thread.currentThread().getName());
        final Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("call ThreadName:" + Thread.currentThread().getName());
                subscriber.onNext("Hello");
                subscriber.onCompleted();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1() {
                    @Override
                    public void call(Object o) {
                        Logger.i("doOnNext call:" + Thread.currentThread().getName());
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
        Logger.i("subscribe ThreadName:" + Thread.currentThread().getName());
        observable.subscribe(mSubscriber);
    }

    public void test3(View v) {
        Observable.just("Hello", "RxJava", "RxAndroid").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mObserver);
    }

    public void test4(View v) {
        String[] words = {"Hello", "RxJava", "RxAndroid"};
        Observable.from(words).observeOn(Schedulers.io()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(mSubscriber);
    }

    public void test5(View v) {
        // 除了 subscribe(Observer) 和 subscribe(Subscriber) ，subscribe() 还支持不完整定义的回调
        Action1<String> onNextAction = new Action1<String>() {
            // onNext()
            @Override
            public void call(String s) {
                Logger.i("onNext " + s + " ThreadName:" + Thread.currentThread().getName());
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                Logger.i("onError ThreadName:" + Thread.currentThread().getName());
            }
        };
        Action0 onCompletedAction = new Action0() {
            // onCompleted()
            @Override
            public void call() {
                Logger.i("onCompleted ThreadName:" + Thread.currentThread().getName());
            }
        };
        Observable observable = Observable.just("Hello", "RxJava", "RxAndroid").subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io());
        observable.subscribe(onNextAction);
        observable.subscribe(onNextAction, onErrorAction);
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);


        observable.subscribe(s -> {
            Logger.i("onNext " + s + " ThreadName:" + Thread.currentThread().getName());
        });
        observable.subscribe(s -> Logger.i("onNext " + s + " ThreadName:" + Thread.currentThread().getName()));

        observable.subscribe(s -> Logger.i("onNext " + s + " ThreadName:" + Thread.currentThread().getName()), throwable -> Logger.i("onError ThreadName:" + Thread.currentThread().getName()));

        observable.subscribe(s -> Logger.i("onNext " + s + " ThreadName:" + Thread.currentThread().getName()), throwable -> Logger.i("onError ThreadName:" + Thread.currentThread().getName()), () -> Logger.i("onCompleted ThreadName:" + Thread.currentThread().getName()));
    }

    public void test6(View v) {
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
                subscriber.onNext(drawable);
                subscriber.onCompleted();
            }
        }).compose(RxUtil.applySchedulers())
                .subscribe(new SimpleSubscriber<Drawable>() {
                    @Override
                    public void onNext(Drawable drawable) {
                        ((ImageView) findViewById(R.id.iv_helloworld_test)).setImageDrawable(drawable);
                    }
                });
    }

    /**
     * Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
     * .subscribeOn(Schedulers.io())
     * .observeOn(Schedulers.newThread())
     * .map(mapOperator) // 新线程，由 observeOn() 指定
     * .observeOn(Schedulers.io())
     * .map(mapOperator2) // IO 线程，由 observeOn() 指定
     * .observeOn(AndroidSchedulers.mainThread)
     * .subscribe(subscriber);  // Android 主线程，由 observeOn() 指定
     * <p>
     * <p>
     * 不同于 observeOn() ， subscribeOn() 的位置放在哪里都可以，但它是只能调用一次的
     */
    public void test8(View v) {
        mRemoteServerEngine.loadMoreDataRx(1)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoadingDialog(R.string.loading);
                        Logger.i("doOnSubscribe call:" + Thread.currentThread().getName());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())  // 用于指定前面那个doOnSubscribe中Action0的call方法在UI线程执行
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<List<RefreshModel>>() {
                    @Override
                    public void call(List<RefreshModel> refreshModels) {
                        Logger.i("doOnNext call:" + Thread.currentThread().getName());
                    }
                }) // doOnNext中Action1的call方法所在线程受上一个observeOn影响,否则就是第一个subscribeOn指定的线程
                .observeOn(Schedulers.io())
                .flatMap(new Func1<List<RefreshModel>, Observable<RefreshModel>>() {
                    @Override
                    public Observable<RefreshModel> call(List<RefreshModel> refreshModels) {
                        Logger.i("flatMap call:" + Thread.currentThread().getName());
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
                        Logger.i(refreshModel.title);
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

//        Observable.concat(mRemoteServerEngine.loadInitDatasRx(), mRemoteServerEngine.loadMoreDataRx(1), mRemoteServerEngine.loadMoreDataRx(1)).subscribeOn(Schedulers.io())
//                .doOnSubscribe(new Action0() {
//                    @Override
//                    public void call() {
//                        showLoadingDialog(R.string.loading);
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .flatMap(new Func1<List<RefreshModel>, Observable<RefreshModel>>() {
//                    @Override
//                    public Observable<RefreshModel> call(List<RefreshModel> refreshModels) {
//                        return Observable.from(refreshModels);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<RefreshModel>() {
//                    @Override
//                    public void onCompleted() {
//                        dismissLoadingDialog();
//                        ToastUtil.show("数据加载成功");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        dismissLoadingDialog();
//                        ToastUtil.show("数据加载失败");
//                    }
//
//                    @Override
//                    public void onNext(RefreshModel refreshModel) {
//                        Logger.i(refreshModel.title);
//                    }
//                });


        showLoadingDialog(R.string.loading);
        Observable.concat(mRemoteServerEngine.loadInitDatasRx(), mRemoteServerEngine.loadMoreDataRx(1), mRemoteServerEngine.loadMoreDataRx(1))
                .flatMap(new Func1<List<RefreshModel>, Observable<RefreshModel>>() {
                    @Override
                    public Observable<RefreshModel> call(List<RefreshModel> refreshModels) {
                        return Observable.from(refreshModels);
                    }
                })
                .compose(RxUtil.applySchedulers())
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
                        Logger.i(refreshModel.title);
                    }
                });
    }

    public void interval(View v) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    ((TextView) v).setText(CalendarUtil.formatYearMonthDayHourMinute(CalendarUtil.getCalendar().getTime()));
                });
    }

    public void defer(View v) {
        newMethod(1).flatMap(new Func1<List<RefreshModel>, Observable<RefreshModel>>() {
            @Override
            public Observable<RefreshModel> call(List<RefreshModel> refreshModels) {
                return Observable.from(refreshModels);
            }
        }).compose(RxUtil.applySchedulers())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<RefreshModel>() {
                    @Override
                    public void onNext(RefreshModel refreshModel) {
                        Logger.i(refreshModel.title);
                    }
                });
    }

    /**
     * Retrofit可以返回Observable对象，但是如果你使用的别的库并不支持这样怎么办？
     * 或者说一个内部的代码，你想把他们转换成Observable的？有什么简单的办法没？
     * 绝大多数时候Observable.just() 和 Observable.from() 能够帮助你从遗留代码中创建 Observable 对象
     * 如果oldMethod()足够快是没有什么问题的，但是如果很慢呢？调用oldMethod()将会阻塞住他所在的线程。
     * 可以使用defer()来包装缓慢的代码
     */
    private Observable<List<RefreshModel>> newMethod(final int param) {
        logWithThread("defer", "newMethod");
        return Observable.defer(new Func0<Observable<List<RefreshModel>>>() {
            @Override
            public Observable<List<RefreshModel>> call() {
                logWithThread("defer", "call");
                try {
                    return Observable.just(oldMethod(param));
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    private List<RefreshModel> oldMethod(int page) throws IOException {
        return mRemoteServerEngine.loadMoreData(page).execute().body();
    }

    public void test11(View v) {
        Observable
                .just("1", "2", "3", "3", "5", "6")
                .map(new Func1<String, Integer>() {   // 类型转类型
                    @Override
                    public Integer call(String s) {
                        return Integer.parseInt(s);
                    }
                })
                .filter(new Func1<Integer, Boolean>() {  // 过滤后    2、3、3、5、6
                    @Override
                    public Boolean call(Integer integer) {
                        return integer > 1;
                    }
                })
                .distinct()  // 去重后    2、3、5、6
                .take(3)   // 只取前三个后     2、3、5
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new SimpleSubscriber<Integer>() {
                    @Override
                    public void onNext(Integer integer) {
                        logWithThread("和为", integer + "");
                    }
                });
    }

    /**
     * map: 对Observable发射的数据都应用一个函数，然后再发射最后的结果集。最后map()方法返回一个新的Observable。队列中的数据一个个通过Func1转换，同步的，输出的结果不是乱序的
     */
    public void mapDemo1(View v) {
        // 假设我们从服务器获取了一个字符串集合，我们想里面的数据项都转成大写，然后把集合的顺序反转。
        // 如把[“this”,”is”,”rxJava”]转成[“RXJAVA”,”IS”,“THIS”]
        Observable.from(new String[]{"This", "is", "RxJava"})
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        logWithThread("转换大写", s);
                        return s.toUpperCase();
                    }
                })
                .toList()
                .map(new Func1<List<String>, List<String>>() {
                    @Override
                    public List<String> call(List<String> strings) {
                        logWithThread("倒序排队", strings.toString());
                        Collections.reverse(strings);
                        return strings;
                    }
                })
                .compose(RxUtil.applySchedulers())
                .subscribe(new SimpleSubscriber<List<String>>() {
                    @Override
                    public void onNext(List<String> strings) {
                        logWithThread("结果", strings.toString());
                    }
                });
    }

    public void mapDemo2(View v) {
        // 假设我们有一个主机列表，想根据这个主机列表好获取它们的IP地址。
        Observable.just(
                "http://www.baidu.com",
                "http://www.google.com",
                "https://www.bing.com")
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String url) {
                        logWithThread("获取IP地址", url);

                        // 这里添加ip地址的转换是耗时操作，转换完一个就会调用一次SimpleSubscriber的onNext方法，并不是所有的网址都转换完才调用SimpleSubscriber的onNext方法
                        try {
                            url = getIpString(url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return url;
                    }
                })
                .compose(RxUtil.applySchedulers())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        logWithThread("结果", s);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");
                    }
                });
    }

    private String getIpString(String url) throws Exception {
        Thread.sleep(2000);
        String address = InetAddress.getByName(new URL(url).getHost()).toString();
        int preIndex = address.indexOf("/");
        return url + ":" + address.substring(preIndex + 1);
    }

    /**
     * 对Observable发射的数据都应用(apply)一个函数，这个函数返回一个Observable，然后合并这些Observables，并且发送（emit）合并的结果。
     * flatMap和map操作符很相像，flatMap发送的是合并后的Observables，map操作符发送的是应用函数后返回的结果集。
     * flatMap的call方法里返回的Observable指定线程后，输出的结果是乱序的。不指定线程的话，输出的结果不是乱序的
     */
    public void flatMapDemo1(View v) {
        Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        logWithThread("外层获取IP地址", url);
                        return Observable.create(new Observable.OnSubscribe<String>() {

                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                logWithThread("内层获取IP地址", url);
                                try {
                                    subscriber.onNext(getIpString(url));
                                } catch (Exception e) {
                                    //subscriber.onError(e);
                                    subscriber.onNext(null);
                                }
                                subscriber.onCompleted();
                            }
//                        });
                            // 如果这里不指定线程，他们是通过一个线程来完成所有的任务的，队列中的数据一个个通过Func1转换，同步的。输出的结果不是乱序的
                        }).subscribeOn(Schedulers.io());
                        // 如果这里指定了线程，他们是通过不同线程来完成任务的，输出的结果是乱序的
                    }
                })
                .compose(RxUtil.applySchedulers())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        logWithThread("结果", s);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");
                    }
                });
    }

    public void flatMapDemo2(View v) {
        Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        logWithThread("外层获取IP地址", url);

                        return Observable.defer(new Func0<Observable<String>>() {
                            @Override
                            public Observable<String> call() {
                                logWithThread("内层获取IP地址", url);
                                try {
                                    return Observable.just(getIpString(url));
                                } catch (Exception e) {
                                    return Observable.error(e);
                                }
                            }
//                        });
                            // 如果这里不指定线程，他们是通过一个线程来完成所有的任务的，队列中的数据一个个通过Func1转换，同步的。输出的结果不是乱序的
                        }).subscribeOn(Schedulers.io());
                        // 如果这里指定了线程，他们是通过不同线程来完成任务的，输出的结果是乱序的
                    }
                })
                .compose(RxUtil.applySchedulers())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        logWithThread("结果", s);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");
                    }
                });
    }

    public void flatMapDemo3(View v) {
        Student[] students = null;
        Observable.from(students).flatMap(new Func1<Student, Observable<Course>>() {
            @Override
            public Observable<Course> call(Student student) {
                return Observable.from(student.courses);
            }
        }).compose(RxUtil.applySchedulers())
                .subscribe(new SimpleSubscriber<Course>() {
                    @Override
                    public void onNext(Course course) {
                    }
                });
    }

    /**
     * 功能与flatMap类似，但是发射数据是有序的。既要是多个线程完成任务，又要保持任务的顺序
     */
    public void concatMapDemo1(View v) {
        Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .concatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        logWithThread("外层获取IP地址", url);
                        return Observable.create(new Observable.OnSubscribe<String>() {

                            @Override
                            public void call(Subscriber<? super String> subscriber) {
                                logWithThread("内层获取IP地址", url);
                                try {
                                    subscriber.onNext(getIpString(url));
                                } catch (Exception e) {
                                    //subscriber.onError(e);
                                    subscriber.onNext(null);
                                }
                                subscriber.onCompleted();
                            }
//                        });
                            // 如果这里不指定线程，他们是通过一个线程来完成所有的任务的，队列中的数据一个个通过Func1转换，同步的。输出的结果不是乱序的
                        }).subscribeOn(Schedulers.io());
                        // 这里指定了线程，但输出的结果不是乱序的
                    }
                })
                .compose(RxUtil.applySchedulers())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        logWithThread("结果", s);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");
                    }
                });
    }

    public void concatMapDemo2(View v) {
        Observable.just(
                "http://www.baidu.com/",
                "http://www.google.com/",
                "https://www.bing.com/")
                .concatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String url) {
                        logWithThread("外层获取IP地址", url);

                        return Observable.defer(new Func0<Observable<String>>() {
                            @Override
                            public Observable<String> call() {
                                logWithThread("内层获取IP地址", url);
                                try {
                                    return Observable.just(getIpString(url));
                                } catch (Exception e) {
                                    return Observable.error(e);
                                }
                            }
//                        });
                            // 如果这里不指定线程，他们是通过一个线程来完成所有的任务的，队列中的数据一个个通过Func1转换，同步的。输出的结果不是乱序的
                        }).subscribeOn(Schedulers.io());
                        // 这里指定了线程，但输出的结果不是乱序的
                    }
                })
                .compose(RxUtil.applySchedulers())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<String>() {
                    @Override
                    public void onNext(String s) {
                        logWithThread("结果", s);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");
                    }
                });
    }


    private Observable mModelOneObservable = Observable.create(new Observable.OnSubscribe<ModelOne>() {
        @Override
        public void call(Subscriber<? super ModelOne> subscriber) {
            Logger.i("call ThreadName:" + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(new ModelOne("value1"));
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(new ModelOne("value2"));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(new ModelOne("value3"));
            try {
                Thread.sleep(4000);   // combineLatest时：mModelOneObservable里总的call的时间比mModelTwoObservable里总的call的时间长，当mModelTwoObservable里调了onCompleted方法后，如果mModelOneObservable还在睡眠就会报InterruptedException。zip时不会有这个问题
//                Thread.sleep(1000);     // combineLatest时：mModelOneObservable里总的call的时间比mModelTwoObservable里总的call的时间短，当mModelTwoObservable里调了onCompleted方法后，如果mModelOneObservable还在睡眠就不会报InterruptedException
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(new ModelOne("value4"));
            subscriber.onCompleted();
        }
    }).subscribeOn(Schedulers.io()); // 这里如果不指定线程，则两个Observable会在combineLatest/zip后指定的线程中串行执行

    private Observable mModelTwoObservable = Observable.create(new Observable.OnSubscribe<ModelTwo>() {
        @Override
        public void call(Subscriber<? super ModelTwo> subscriber) {
            Logger.i("call ThreadName:" + Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(new ModelTwo(1));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(new ModelTwo(2));
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            subscriber.onNext(new ModelTwo(3));
            subscriber.onCompleted();
        }
    }).subscribeOn(Schedulers.io()); // 这里如果不指定线程，则两个Observable会在combineLatest/zip后指定的线程中串行执行

    public void zip(View v) {

        /**
         * Zip操作符返回一个Obversable，它使用这个函数按顺序结合两个或多个Observables发射的数据项，然后它发射这个函数返回的结果。
         * 它按照严格的顺序应用这个函数。它只发射与发射数据项最少的那个Observable一样多的数据。
         */
        Observable.zip(mModelOneObservable, mModelTwoObservable, new Func2<ModelOne, ModelTwo, ModelCombine>() {

            @Override
            public ModelCombine call(ModelOne modelOne, ModelTwo modelTwo) {
                return new ModelCombine(modelOne, modelTwo);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<ModelCombine>() {

                    @Override
                    public void onNext(ModelCombine modelCombine) {
                        Logger.i("onNext " + modelCombine.modelOne.value + " " + modelCombine.modelTwo.value);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");  // 设置compose(bindUntilEvent(ActivityEvent.DESTROY))时，如果在销毁Activity时工作线程里的任务还未执行完毕，onCompleted方法会被调用
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i("onError " + e.getLocalizedMessage());
                    }
                });
    }


    public void combineLatest(View v) {
        /**
         * CombineLatest操作符行为类似于zip，但是只有当原始的Observable中的每一个都发射了一条数据时zip才发射数据。
         * CombineLatest则在原始的Observable中任意一个发射了数据时发射一条数据。当原始Observables的任何一个发射了一条数据时，
         * CombineLatest使用一个函数结合它们最近发射的数据，然后发射这个函数的返回值。
         */
        Observable.combineLatest(mModelOneObservable, mModelTwoObservable, new Func2<ModelOne, ModelTwo, ModelCombine>() {

            @Override
            public ModelCombine call(ModelOne modelOne, ModelTwo modelTwo) {
                return new ModelCombine(modelOne, modelTwo);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .doOnNext(new Action1<ModelCombine>() {

                    @Override
                    public void call(ModelCombine modelCombine) {
                        Logger.i("doOnNext " + modelCombine.modelOne.value + " " + modelCombine.modelTwo.value + " " + Thread.currentThread().getName());
                    }
                })
                .subscribe(new SimpleSubscriber<ModelCombine>() {

                    @Override
                    public void onNext(ModelCombine modelCombine) {
                        Logger.i("onNext " + modelCombine.modelOne.value + " " + modelCombine.modelTwo.value);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");   // 设置compose(bindUntilEvent(ActivityEvent.DESTROY))时，如果在销毁Activity时工作线程里的任务还未执行完毕，onCompleted方法会被调用
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.i("onError " + e.getLocalizedMessage());
                    }

                });
    }

    public void compose(View v) {
        Observable.create(new Observable.OnSubscribe<NetResult<ModelOne>>() {
            @Override
            public void call(Subscriber<? super NetResult<ModelOne>> subscriber) {
                Logger.i("call ThreadName:" + Thread.currentThread().getName());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                NetResult<ModelOne> netResult = new NetResult<>();
                netResult.code = 0;
                subscriber.onNext(netResult);
                subscriber.onCompleted();
            }
        }).compose(RxUtil.applySchedulersAndFlatMapResult())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<ModelOne>(this, false) {
                    @Override
                    public void onNext(ModelOne modelOne) {
                        ToastUtil.show("请求成功");
                    }
                });
    }

    /**
     * concat操作符是接收若干个Observables，发射数据是有序的，不会交叉。
     * concat + takeFirst 实现三级缓存
     * <p>
     * 使用first时需要注意的是，如果memoryObservable, diskObservable, networkObservable返回的都null，那么会报一个异常：java.util.NoSuchElementException: Sequence contains no elements
     * 可以使用takeFirst操作，即使都没有数据，也不会报异常。
     * 如果想针对三个都返回空时提示具体的提示信息就还是用first，并根据NoSuchElementException来提示
     */
    public void concat(View v) {
        Observable<String> memoryObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("memoryObservable call ThreadName:" + Thread.currentThread().getName());
                String result = null;
                try {
                    Thread.sleep(3000); // 取消订阅时，如果刚好这里正在睡眠，会报InterruptedException
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(result)) {
                    subscriber.onNext(result);
                }
                subscriber.onCompleted();
            }
        });
        Observable<String> diskObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("diskObservable call ThreadName:" + Thread.currentThread().getName());
                String result = null;
                try {
                    Thread.sleep(2000); // 取消订阅时，如果刚好这里正在睡眠，会报InterruptedException
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(result)) {
                    subscriber.onNext(result);
                }
                subscriber.onCompleted();
            }
        }).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                // 缓存到内存中
                return s;
            }
        });
        Observable<String> networkObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Logger.i("networkObservable call ThreadName:" + Thread.currentThread().getName());
                String result = null;
                try {
                    Thread.sleep(1000); // 取消订阅时，如果刚好这里正在睡眠，会报InterruptedException
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(result)) {
                    subscriber.onNext(result);
                }
                subscriber.onCompleted();
            }
        }).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                // 缓存到硬盘和内存中
                return s;
            }
        });
        // 不管是否给concat中的Observable单独指定工作线程，都是按添加的先后顺序串行执行的
        Observable.concat(memoryObservable, diskObservable, networkObservable)
//                .first()   // 如果memoryObservable, diskObservable, networkObservable返回的都null，那么会报一个异常：java.util.NoSuchElementException: Sequence contains no elements
                .takeFirst(new Func1<String, Boolean>() {  // 使用takeFirst操作，即使memoryObservable, diskObservable, networkObservable返回的都null，也不会报异常。
                    @Override
                    public Boolean call(String s) {
                        return s != null;
                    }
                })
                .compose(RxUtil.applySchedulers())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new SimpleSubscriber<String>() {
                    @Override
                    public void onNext(String result) {
                        Logger.i("请求成功:" + result);
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        Logger.i("onCompleted");
                    }

                    @Override
                    public void onError(String msg) {
                        Logger.i("onError:" + msg);
                    }
                });
    }

    private void logWithThread(String methodName, String msg) {
        Logger.i("| " + Thread.currentThread().getName() + " | " + methodName + " | " + msg);
    }

}

/**
 * RxJava 使用debounce和switchMap操作符 优化app搜索功能   http://blog.csdn.net/johnny901114/article/details/51555203
 * <p>
 * switchMap操作符 和 flatMap操作符 差不多，区别是switchMap操作符只会发射[emit]最近的Observables。
 * 也就是说，当400毫秒后，发出第一个搜索请求，当这个请求的过程中，用户又去搜索了，发出第二个请求，不管怎样，switchMap操作符只会发射第二次请求的Observable
 * <p>
 * RxJava retryWhen操作符实现错误重试机制    http://blog.csdn.net/johnny901114/article/details/51539708
 * <p>
 * RxJava onErrorResumeNext操作符实现app与服务器间token机制    http://blog.csdn.net/johnny901114/article/details/51533586
 */

/**
 * RxJava retryWhen操作符实现错误重试机制    http://blog.csdn.net/johnny901114/article/details/51539708
 */

/**
 * RxJava onErrorResumeNext操作符实现app与服务器间token机制    http://blog.csdn.net/johnny901114/article/details/51533586
 */
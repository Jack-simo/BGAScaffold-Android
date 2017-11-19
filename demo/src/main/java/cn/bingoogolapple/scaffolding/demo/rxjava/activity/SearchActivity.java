package cn.bingoogolapple.scaffolding.demo.rxjava.activity;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.scaffolding.demo.R;
import cn.bingoogolapple.scaffolding.util.StringUtil;
import cn.bingoogolapple.scaffolding.view.MvcActivity;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 下午5:53
 * 描述:
 */
public class SearchActivity extends MvcActivity {
    private SearchView mKeywordSv;
    private PublishSubject<String> mKeywordPs;
    private TextView mResultTv;

    @Override
    protected int getRootLayoutResID() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mKeywordSv = findViewById(R.id.sv_search_keyword);
        mResultTv = findViewById(R.id.tv_search_result);
    }

    @Override
    protected void setListener() {
        mKeywordSv.setIconified(false);
        mKeywordSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String keyword) {
                mKeywordPs.onNext(keyword);
                return true;
            }
        });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mKeywordPs = PublishSubject.create();
        mKeywordPs.debounce(400, TimeUnit.MILLISECONDS) // debounce 默认是在 computation 线程的。发送一个延时消息给下游，如果在这段延时时间内没有收到新的请求，那么下游就会收到该消息；而如果在这段延时时间内收到来新的请求，那么就会取消之前的消息，并重新发送一个新的延时消息
                .observeOn(AndroidSchedulers.mainThread()) // 这里手动将后续操作符切换到主线程，否则 filter 也是在 computation 线程的
                .filter(keyword -> { // 只有返回 true 时，才会将事件发送给下游，否则就丢弃该事件
                    if (StringUtil.isNotEmpty(keyword)) {
                        return true;
                    } else {
                        mResultTv.setText("清空了关键字");
                        return false;
                    }
                })
                .switchMap(keyword -> getSearchObservable(keyword)) // switchMap 将上游的事件转换成新的 Observable，如果在该节点收到一个新的事件之后，那么如果之前收到的事件所产生的 Observable A 还没有发送事件给下游，那么下游就再也不会收到 Observable A
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .doOnError(throwable -> {
                    mResultTv.setText("错误：" + throwable.getMessage()); // 将 Observer 的 onError 中的错误处理放到 doOnError 中处理
                })
                .retryWhen(throwableObservable -> throwableObservable) // 处理 onError 时重订阅，避免发生一次错误后就再也搜索不到结果。Observer 的 onError 将不会再被回调
                .filter(result -> StringUtil.isNotEmpty(mKeywordSv.getQuery())) // 避免返回结果时，如果当前搜索框关键字为空则忽略此次搜索结果
                .subscribe(result -> {
                    Logger.d("onNext：" + result);
                    mResultTv.setText(result);
                });
    }

    private Observable<String> getSearchObservable(final String keyword) {
        return Observable.create((ObservableOnSubscribe<String>) emitter -> {
            try {
                if (StringUtil.isEqual("q", keyword)) { // 搜索 q 时延时 3 秒返回结果
                    Thread.sleep(3000);
                } else if (StringUtil.isEqual("e", keyword)) { // 搜索 q 时延时 3 秒返回网络异常
                    Thread.sleep(3000);
                    emitter.onError(new Exception("网络异常"));
                } else { // 搜索其他关键字时延时 1 秒返回结果
                    Thread.sleep(1000);
                }
                if (!emitter.isDisposed()) {
                    emitter.onNext("关键词为:" + keyword);
                    emitter.onComplete();
                }
            } catch (Exception e) {
                if (!emitter.isDisposed()) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
    }
}
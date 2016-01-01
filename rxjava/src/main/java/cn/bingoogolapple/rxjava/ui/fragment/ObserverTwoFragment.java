package cn.bingoogolapple.rxjava.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.fragment.BaseFragment;
import cn.bingoogolapple.basenote.util.RxBus;
import cn.bingoogolapple.rxjava.R;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/2 上午12:49
 * 描述:
 */
public class ObserverTwoFragment extends BaseFragment {
    private TextView mCountTv;
    private TextView mTipTv;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_observer);
        mCountTv = getViewById(R.id.tv_observer_count);
        mTipTv = getViewById(R.id.tv_observer_tip);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
    }

    @Override
    public void onStart() {
        super.onStart();

        Observable<ObservableFragment.TapEvent> tapEventEmitter = RxBus.toObserverable()
                .compose(this.bindToLifecycle())
                .filter(o -> o instanceof ObservableFragment.TapEvent)
                .map(o -> (ObservableFragment.TapEvent) o)
                .share();

        tapEventEmitter
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    mTipTv.setVisibility(View.VISIBLE);
                    mTipTv.setAlpha(1f);
                    ViewCompat.animate(mTipTv).alphaBy(-1f).setDuration(400);
                });

        Observable<ObservableFragment.TapEvent> debouncedEmitter = tapEventEmitter.debounce(1, TimeUnit.SECONDS);
        Observable<List<ObservableFragment.TapEvent>> debouncedBufferEmitter = tapEventEmitter.buffer(debouncedEmitter);
        debouncedBufferEmitter
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(taps -> {
                    mCountTv.setText(String.valueOf(taps.size()));
                    mCountTv.setVisibility(View.VISIBLE);
                    mCountTv.setScaleX(1f);
                    mCountTv.setScaleY(1f);
                    ViewCompat.animate(mCountTv)
                            .scaleXBy(-1f)
                            .scaleYBy(-1f)
                            .setDuration(800)
                            .setStartDelay(100);
                });
    }
}
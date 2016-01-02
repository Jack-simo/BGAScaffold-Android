package cn.bingoogolapple.rxjava.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.TextView;

import cn.bingoogolapple.basenote.fragment.BaseFragment;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.basenote.util.RxBus;
import cn.bingoogolapple.rxjava.R;
import rx.android.schedulers.AndroidSchedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/2 上午12:49
 * 描述:
 */
public class ObserverOneFragment extends BaseFragment {
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
        RxBus.toObserverable()
                .compose(this.bindToLifecycle())
                .ofType(ObservableFragment.TapEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    mTipTv.setVisibility(View.VISIBLE);
                    mTipTv.setAlpha(1f);
                    ViewCompat.animate(mTipTv).alphaBy(-1f).setDuration(400);
                });

        RxBus.toObserverable()
                .compose(bindToLifecycle())
                .ofType(ObservableFragment.RefreshModelEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    Logger.i(TAG, event.getRefreshModel().title);
                });
    }
}
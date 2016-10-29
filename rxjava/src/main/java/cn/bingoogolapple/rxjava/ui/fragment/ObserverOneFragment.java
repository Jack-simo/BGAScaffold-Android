package cn.bingoogolapple.rxjava.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import cn.bingoogolapple.basenote.fragment.BaseFragment;
import cn.bingoogolapple.basenote.util.RxBus;
import cn.bingoogolapple.rxjava.R;

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
        RxBus.toObservableAndBindToLifecycle(ObservableFragment.TapEvent.class, this).subscribe(tapEvent -> {
            mTipTv.setVisibility(View.VISIBLE);
            mTipTv.setAlpha(1f);
            ViewCompat.animate(mTipTv).alphaBy(-1f).setDuration(400);
        });

        RxBus.toObservableAndBindToLifecycle(ObservableFragment.RefreshModelEvent.class, this).subscribe(event -> Logger.i(event.getRefreshModel().title));
    }
}
package cn.bingoogolapple.rxjava.ui.fragment;

import android.os.Bundle;
import android.view.View;

import cn.bingoogolapple.basenote.fragment.BaseFragment;
import cn.bingoogolapple.basenote.util.RxBus;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.engine.RemoteServerEngine;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/2 上午12:49
 * 描述:
 */
public class ObservableFragment extends BaseFragment {
    private RemoteServerEngine mRemoteServerEngine;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_observable);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_obdervable_test1).setOnClickListener(this);
        getViewById(R.id.btn_obdervable_test2).setOnClickListener(this);
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        mRemoteServerEngine = new Retrofit.Builder()
                .baseUrl("http://7xk9dj.com1.z0.glb.clouddn.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build().create(RemoteServerEngine.class);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_obdervable_test1) {
            if (RxBus.hasObservers()) {
                RxBus.send(new TapEvent());
            }
        } else if (v.getId() == R.id.btn_obdervable_test2) {
            if (RxBus.hasObservers()) {
                mRemoteServerEngine.loadMoreDataRx(1)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(refreshModels -> Observable.from(refreshModels))
                        .filter(refreshModel -> !refreshModel.title.contains("4"))
                        .take(3)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(refreshModel -> {
                            if (RxBus.hasObservers()) {
                                RxBus.send(new RefreshModelEvent(refreshModel));
                            }
                        });
            }
        }
    }

    public static class RefreshModelEvent {
        private RefreshModel mRefreshModel;

        public RefreshModelEvent(RefreshModel refreshModel) {
            mRefreshModel = refreshModel;
        }

        public RefreshModel getRefreshModel() {
            return mRefreshModel;
        }
    }

    public static class TapEvent {
    }
}
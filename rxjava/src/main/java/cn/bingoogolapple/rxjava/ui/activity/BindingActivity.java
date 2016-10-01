package cn.bingoogolapple.rxjava.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.activity.TitlebarActivity;
import cn.bingoogolapple.basenote.util.NetResult;
import cn.bingoogolapple.basenote.util.RxUtil;
import cn.bingoogolapple.basenote.util.SimpleSubscriber;
import cn.bingoogolapple.rxjava.R;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class BindingActivity extends TitlebarActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_binding);
    }

    @Override
    protected void setListener() {
        RxView
                .clicks(getViewById(R.id.btn_binding_throttle))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Logger.i("点击了按钮");
                    }
                });

        RxTextView.textChanges(getViewById(R.id.et_binding_debounce))
                .skip(1)
                .debounce(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(charSequence -> charSequence.toString().trim())
                .filter(s -> {
                    if (s.length() > 0) {
                        return true;
                    } else {
                        // showEmptyView();
                        return false;
                    }
                })
                .switchMap(new Func1<CharSequence, Observable<NetResult<ArrayList<RefreshModel>>>>() {
                    @Override
                    public Observable<NetResult<ArrayList<RefreshModel>>> call(CharSequence charSequence) {
                        return null;
//                        return mApi.search(new ApiParams("Keyword", charSequence)).subscribeOn(Schedulers.io());
                    }
                })
                .compose(RxUtil.flatMapResultAndApplySchedulersBindToLifecycle(this))
                .subscribe(new SimpleSubscriber<ArrayList<RefreshModel>>() {
                    @Override
                    public void onNext(ArrayList<RefreshModel> customerGroupModels) {
                        // 设置数据到列表
                    }
                });
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setTitle("RxBinding学习笔记");
    }

    @Override
    public void onClick(View v) {
    }

}
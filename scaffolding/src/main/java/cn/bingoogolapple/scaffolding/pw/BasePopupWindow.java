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

package cn.bingoogolapple.scaffolding.pw;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/13 上午12:09
 * 描述:PopupWindow基类
 */
public abstract class BasePopupWindow extends PopupWindow {
    protected Activity mActivity;
    protected View mWindowRootView;
    protected View mAnchorView;

    public BasePopupWindow(Activity activity, @LayoutRes int layoutId, View anchorView, int width, int height) {
        super(View.inflate(activity, layoutId, null), width, height, true);
        init(activity, anchorView);

        initView();
        setListener();
        processLogic();
    }

    private void init(Activity activity, View anchorView) {
        getContentView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        // 如果想让在点击别的地方的时候 关闭掉弹出窗体 一定要记得给mPopupWindow设置一个背景资源
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mAnchorView = anchorView;
        mActivity = activity;
        mWindowRootView = activity.getWindow().peekDecorView();
    }

    protected abstract void initView();

    protected abstract void setListener();

    protected abstract void processLogic();

    public abstract void show();

    /**
     * 设置点击事件，并防止重复点击
     *
     * @param id
     * @param consumer
     */
    protected void setOnClick(@IdRes int id, Consumer consumer) {
        setOnClick(findViewById(id), consumer);
    }

    /**
     * 设置点击事件，并防止重复点击
     *
     * @param view
     * @param consumer
     */
    protected void setOnClick(View view, Consumer consumer) {
        RxView.clicks(view).throttleFirst(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected final <VT extends View> VT findViewById(@IdRes int id) {
        return (VT) getContentView().findViewById(id);
    }
}
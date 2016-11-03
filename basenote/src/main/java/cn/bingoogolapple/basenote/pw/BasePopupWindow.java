package cn.bingoogolapple.basenote.pw;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

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
     * @param action
     */
    protected void setOnClick(@IdRes int id, Action1 action) {
        setOnClick(getViewById(id), action);
    }

    /**
     * 设置点击事件，并防止重复点击
     *
     * @param view
     * @param action
     */
    protected void setOnClick(View view, Action1 action) {
        RxView.clicks(view).throttleFirst(500, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(action);
    }

    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return (VT) getContentView().findViewById(id);
    }
}
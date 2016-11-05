package cn.bingoogolapple.basenote.dialog;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatDialog;
import android.view.MotionEvent;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.basenote.util.KeyboardUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/5 上午7:37
 * 描述:
 */
public abstract class BaseDialog extends AppCompatDialog {

    public BaseDialog(Context context) {
        super(context, R.style.AppDialog);
        initView();
        setListener();
        processLogic();
    }

    /**
     * 初始化View控件
     */
    protected abstract void initView();

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑
     */
    protected abstract void processLogic();

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
        return (VT) findViewById(id);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        KeyboardUtil.handleAutoCloseKeyboard(isAutoCloseKeyboard(), getCurrentFocus(), ev, this);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 点击非 EditText 时，是否自动关闭键盘
     *
     * @return
     */
    protected boolean isAutoCloseKeyboard() {
        return true;
    }
}
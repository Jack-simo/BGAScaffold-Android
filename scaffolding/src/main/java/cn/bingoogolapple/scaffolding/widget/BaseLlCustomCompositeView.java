package cn.bingoogolapple.scaffolding.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/13 上午12:07
 * 描述:基于 LinearLayout 的自定义组合控件基类
 */
public abstract class BaseLlCustomCompositeView extends LinearLayout {

    public BaseLlCustomCompositeView(Context context) {
        this(context, null);
    }

    public BaseLlCustomCompositeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseLlCustomCompositeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, getLayoutId(), this);
        initView();
        setListener();
        initDefaultAttrs(context);
        initAttrs(context, attrs);
        processLogic();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, getAttrs());
        final int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            initAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected abstract void setListener();

    protected void initDefaultAttrs(Context context) {
    }

    protected int[] getAttrs() {
        return new int[0];
    }

    protected void initAttr(int attr, TypedArray typedArray) {
    }

    protected abstract void processLogic();

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
}
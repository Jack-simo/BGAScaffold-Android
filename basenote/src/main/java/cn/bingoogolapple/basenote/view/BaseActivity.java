package cn.bingoogolapple.basenote.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import cn.bingoogolapple.alertcontroller.BGAAlertController;
import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.util.KeyboardUtil;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.widget.BGASwipeBackLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements BaseView, View.OnClickListener, BGASwipeBackLayout.PanelSlideListener {
    protected String TAG;
    protected BGASwipeBackLayout mSwipeBackLayout;
    protected App mApp;
    protected SweetAlertDialog mLoadingDialog;
    protected BGAAlertController mMoreMenu;

    protected P mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        mApp = App.getInstance();

        initView(savedInstanceState);
        setListener();
        processLogic(savedInstanceState);
        initPresenter();
    }

    /**
     * 初始化滑动返回
     */
    private void initSwipeBackFinish() {
        if (isSupportSwipeBack()) {
            mSwipeBackLayout = new BGASwipeBackLayout(this);
            mSwipeBackLayout.attachToActivity(this);
            mSwipeBackLayout.setPanelSlideListener(this);
        }
    }

    /**
     * 是否支持滑动返回
     *
     * @return
     */
    protected boolean isSupportSwipeBack() {
        return false;
    }

    /**
     * 设置滑动返回是否可用
     *
     * @param swipeBackEnable
     */
    protected void setSwipeBackEnable(boolean swipeBackEnable) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setSwipeBackEnable(swipeBackEnable);
        }
    }

    @Override
    public void onPanelClosed(View view) {
    }

    @Override
    public void onPanelOpened(View view) {
        swipeBackward();
    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    /**
     * 设置点击事件
     *
     * @param id 控件的id
     */
    protected void setOnClickListener(@IdRes int id) {
        getViewById(id).setOnClickListener(this);
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

    /**
     * 初始化布局以及View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 给View控件添加事件监听器
     */
    protected abstract void setListener();

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract void processLogic(Bundle savedInstanceState);

    protected void initPresenter() {
    }

    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    @Override
    public void onBackPressed() {
        backward();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    /**
     * 跳转到下一个Activity，并且销毁当前Activity
     *
     * @param cls 下一个Activity的Class
     */
    public void forwardAndFinish(Class<?> cls) {
        forward(cls);
        finish();
    }

    /**
     * 跳转到下一个Activity，不销毁当前Activity
     *
     * @param cls 下一个Activity的Class
     */
    public void forward(Class<?> cls) {
        KeyboardUtil.closeKeyboard(this);
        startActivity(new Intent(this, cls));
        executeForwardAnim();
    }

    public void forward(Class<?> cls, int requestCode) {
        forward(new Intent(this, cls), requestCode);
    }

    public void forwardAndFinish(Intent intent) {
        forward(intent);
        finish();
    }

    public void forward(Intent intent) {
        KeyboardUtil.closeKeyboard(this);
        startActivity(intent);
        executeForwardAnim();
    }

    public void forward(Intent intent, int requestCode) {
        KeyboardUtil.closeKeyboard(this);
        startActivityForResult(intent, requestCode);
        executeForwardAnim();
    }

    /**
     * 执行跳转到下一个Activity的动画
     */
    public void executeForwardAnim() {
        overridePendingTransition(R.anim.activity_forward_enter, R.anim.activity_forward_exit);
    }

    /**
     * 回到上一个Activity，并销毁当前Activity
     */
    public void backward() {
        KeyboardUtil.closeKeyboard(this);
        finish();
        executeBackwardAnim();
    }

    /**
     * 滑动返回上一个Activity，并销毁当前Activity
     */
    public void swipeBackward() {
        KeyboardUtil.closeKeyboard(this);
        finish();
        executeSwipeBackAnim();
    }

    /**
     * 回到上一个Activity，并销毁当前Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个Activity的Class
     */
    public void backwardAndFinish(Class<?> cls) {
        KeyboardUtil.closeKeyboard(this);
        startActivity(new Intent(this, cls));
        executeBackwardAnim();
        finish();
    }

    /**
     * 执行回到到上一个Activity的动画
     */
    public void executeBackwardAnim() {
        overridePendingTransition(R.anim.activity_backward_enter, R.anim.activity_backward_exit);
    }

    /**
     * 执行滑动返回到到上一个Activity的动画
     */
    public void executeSwipeBackAnim() {
        overridePendingTransition(R.anim.activity_swipeback_enter, R.anim.activity_swipeback_exit);
    }

    /**
     * 显示加载对话框
     *
     * @param resId
     */
    public void showLoadingDialog(@StringRes int resId) {
        showLoadingDialog(getString(resId));
    }

    public void showLoadingDialog(String msg) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            mLoadingDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorPrimary));
            mLoadingDialog.setCancelable(false);
        }
        mLoadingDialog.setTitleText(msg);
        mLoadingDialog.show();
    }

    /**
     * 隐藏加载对话框
     */
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    /**
     * 显示底部的更多菜单
     */
    protected void showMoreMenu() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMsg(@StringRes int resId) {
        ToastUtil.show(resId);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }
}
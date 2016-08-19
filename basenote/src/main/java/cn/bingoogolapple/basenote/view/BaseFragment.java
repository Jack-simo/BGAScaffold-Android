package cn.bingoogolapple.basenote.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhy.changeskin.SkinManager;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.util.ToastUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:57
 * 描述:
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView, View.OnClickListener {
    protected String TAG;
    protected App mApp;
    protected View mContentView;
    protected BaseActivity mActivity;

    protected boolean mIsLoadedData = false;

    protected P mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
        mApp = App.getInstance();
        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免多次从xml中加载布局文件
        if (mContentView == null) {
            initView(savedInstanceState);
            setListener();
            processLogic(savedInstanceState);
        }
        return mContentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        handleLazyLoadDataOnce();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handleLazyLoadDataOnce();
    }

    private void handleLazyLoadDataOnce() {
        if (mContentView != null && getUserVisibleHint() && !mIsLoadedData) {
            mIsLoadedData = true;
            lazyLoadDataOnce();
        }
    }

    /**
     * 初始化View控件
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

    /**
     * 懒加载一次
     */
    protected void lazyLoadDataOnce() {
    }

    /**
     * 设置布局资源id
     *
     * @param layoutResID
     */
    protected void setContentView(@LayoutRes int layoutResID) {
        mContentView = LayoutInflater.from(getActivity()).inflate(layoutResID, null);
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
        return (VT) mContentView.findViewById(id);
    }

    /**
     * 需要处理点击事件时，重写该方法
     *
     * @param v
     */
    public void onClick(View v) {
    }

    @Override
    public void onStart() {
        super.onStart();
        SkinManager.getInstance().injectSkin(mContentView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        mApp.getRefWatcher().watch(this);
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
        return mActivity;
    }
}
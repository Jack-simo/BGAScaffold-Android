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

import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.presenter.BasePresenter;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.util.UmengUtil;
import pub.devrel.easypermissions.EasyPermissions;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:57
 * 描述:
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements BaseView, EasyPermissions.PermissionCallbacks {
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
        UmengUtil.setFragmentUserVisibleHint(this, isVisibleToUser);

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

    @Override
    public void onResume() {
        super.onResume();
        UmengUtil.onFragmentResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        UmengUtil.onFragmentPause(this);
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
        return (VT) mContentView.findViewById(id);
    }

    @Override
    public void onDestroy() {
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        super.onDestroy();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}
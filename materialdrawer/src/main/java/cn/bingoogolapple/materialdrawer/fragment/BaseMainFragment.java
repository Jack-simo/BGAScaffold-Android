package cn.bingoogolapple.materialdrawer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cn.bingoogolapple.basenote.view.ToolbarFragment;
import cn.bingoogolapple.basenote.util.Logger;
import cn.bingoogolapple.materialdrawer.activity.MainActivity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午2:49
 * 描述:
 */
public abstract class BaseMainFragment extends ToolbarFragment {
    protected MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.i(TAG, "onAttach");
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.i(TAG, "onActivityCreated");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        Logger.i(TAG, "setUserVisibleHint " + isVisibleToUser);
    }

    @Override
    public void lazyLoadDataOnce() {
        Logger.i(TAG, "lazyLoadDataOnce");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // show/hidden方式和replace方式都会打印
        Logger.i(TAG, "onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Logger.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 按home键推到后台会打印
        Logger.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        Logger.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Logger.i(TAG, "onDetach");
        super.onDetach();
    }
}
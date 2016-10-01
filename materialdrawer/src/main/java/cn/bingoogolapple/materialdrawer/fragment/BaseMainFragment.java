package cn.bingoogolapple.materialdrawer.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import cn.bingoogolapple.basenote.fragment.ToolbarFragment;
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
        Logger.i("onAttach");
        mMainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Logger.i("onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.i("onActivityCreated");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        Logger.i("setUserVisibleHint " + isVisibleToUser);
    }

    @Override
    public void lazyLoadDataOnce() {
        Logger.i("lazyLoadDataOnce");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // show/hidden方式和replace方式都会打印
        Logger.i("onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.i("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.i("onResume");
    }

    @Override
    public void onPause() {
        Logger.i("onPause");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // 按home键推到后台会打印
        Logger.i("onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        Logger.i("onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Logger.i("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Logger.i("onDetach");
        super.onDetach();
    }
}
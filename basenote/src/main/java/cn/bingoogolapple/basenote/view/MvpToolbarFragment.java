package cn.bingoogolapple.basenote.view;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;

import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.basenote.presenter.BasePresenter;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午2:49
 * 描述:
 */
public abstract class MvpToolbarFragment<P extends BasePresenter> extends BaseMvpFragment<P> {
    protected Toolbar mToolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.viewstub_linearlayout, layoutResID);
    }

    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.viewstub_framelayout, layoutResID);
    }

    private void initViewStubContentView(@LayoutRes int rootLayoutResID, @LayoutRes int layoutResID) {
        super.setContentView(rootLayoutResID);

        ViewStubCompat toolbarVs = getViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_toolbar);
        toolbarVs.inflate();
        mToolbar = getViewById(R.id.toolbar);

        ViewStubCompat viewStub = getViewById(R.id.contentVs);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();

        setHasOptionsMenu(true);
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public void setTitle(CharSequence title) {
        getActivity().setTitle(title);
    }

    public void setTitle(@StringRes int titleId) {
        getActivity().setTitle(titleId);
    }
}
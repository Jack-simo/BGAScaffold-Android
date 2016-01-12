package cn.bingoogolapple.basenote.fragment;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.ViewStub;

import cn.bingoogolapple.basenote.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午2:49
 * 描述:
 */
public abstract class ToolbarFragment extends BaseFragment {
    protected Toolbar mToolbar;

    @Override
    protected void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.toolbar_viewstub);
        mToolbar = getViewById(R.id.toolbar);
        ViewStub viewStub = getViewById(R.id.viewStub);
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
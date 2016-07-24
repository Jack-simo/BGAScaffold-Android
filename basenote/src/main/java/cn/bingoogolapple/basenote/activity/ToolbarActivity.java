package cn.bingoogolapple.basenote.activity;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;

import cn.bingoogolapple.basenote.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/11 下午11:10
 * 描述:
 */
public abstract class ToolbarActivity extends BaseActivity {
    protected Toolbar mToolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.toolbar_viewstub_linearlayout, layoutResID);
    }

    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.toolbar_viewstub_framelayout, layoutResID);
    }

    private void initViewStubContentView(@LayoutRes int rootLayoutResID, @LayoutRes int layoutResID) {
        super.setContentView(rootLayoutResID);
        mToolbar = getViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewStubCompat viewStub = getViewById(R.id.viewStub);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }
}
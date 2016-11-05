package cn.bingoogolapple.basenote.view;

import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.ViewStubCompat;
import android.view.ViewStub;

import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/11 下午11:10
 * 描述:
 */
public abstract class TitleBarActivity extends BaseActivity {
    protected BGATitlebar mTitleBar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.viewstub_linearlayout, layoutResID);
    }

    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.viewstub_merge, layoutResID);
    }

    private void initViewStubContentView(@LayoutRes int rootLayoutResID, @LayoutRes int layoutResID) {
        super.setContentView(rootLayoutResID);

        ViewStubCompat toolbarVs = getViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_titlebar);
        toolbarVs.inflate();

        mTitleBar = getViewById(R.id.titleBar);
        mTitleBar.setLeftDrawable(getResources().getDrawable(R.mipmap.back_normal));
        mTitleBar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
            @Override
            public void onClickLeftCtv() {
                onClickLeft();
            }

            @Override
            public void onClickRightCtv() {
                onClickRight();
            }

            @Override
            public void onClickTitleCtv() {
                onClickTitle();
            }
        });

        ViewStub viewStub = getViewById(R.id.contentVs);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleBar.setTitleText(title);
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        mTitleBar.setTitleText(titleId);
    }

    protected void onClickLeft() {
        onBackPressed();
    }

    protected void onClickRight() {
    }

    protected void onClickTitle() {
    }
}
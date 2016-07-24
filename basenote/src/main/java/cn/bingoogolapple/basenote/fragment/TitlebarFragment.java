package cn.bingoogolapple.basenote.fragment;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.ViewStub;

import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午2:49
 * 描述:
 */
public abstract class TitlebarFragment extends BaseFragment {
    protected BGATitlebar mTitlebar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.titlebar_viewstub_linearlayout, layoutResID);
    }

    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.titlebar_viewstub_framelayout, layoutResID);
    }

    private void initViewStubContentView(@LayoutRes int rootLayoutResID, @LayoutRes int layoutResID) {
        super.setContentView(rootLayoutResID);
        mTitlebar = getViewById(R.id.titlebar);
        setLeftDrawable(R.mipmap.back_normal);
        mTitlebar.setDelegate(new BGATitlebar.BGATitlebarDelegate() {
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

        ViewStub viewStub = getViewById(R.id.viewStub);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    public BGATitlebar getToolbar() {
        return mTitlebar;
    }

    public void setTitle(CharSequence title) {
        mTitlebar.setTitleText(title);
    }

    public void setTitle(@StringRes int titleId) {
        mTitlebar.setTitleText(titleId);
    }

    public void hiddenLeftCtv() {
        mTitlebar.hiddenLeftCtv();
    }

    protected void setLeftDrawable(Drawable drawable) {
        mTitlebar.setLeftDrawable(drawable);
    }

    protected void setLeftDrawable(@DrawableRes int resId) {
        mTitlebar.setLeftDrawable(getResources().getDrawable(resId));
    }

    protected void setRightDrawable(Drawable drawable) {
        mTitlebar.setRightDrawable(drawable);
    }

    protected void setRightDrawable(@DrawableRes int resId) {
        mTitlebar.setRightDrawable(getResources().getDrawable(resId));
    }

    protected void setTitleDrawable(Drawable drawable) {
        mTitlebar.setTitleDrawable(drawable);
    }

    protected void setTitleDrawable(@DrawableRes int resId) {
        mTitlebar.setTitleDrawable(getResources().getDrawable(resId));
    }

    protected void onClickLeft() {
    }

    protected void onClickRight() {
    }

    protected void onClickTitle() {
    }
}
package cn.bingoogolapple.basenote.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.ViewStub;
import android.widget.RelativeLayout;

import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/11 下午11:10
 * 描述:
 */
public abstract class TitlebarActivity extends BaseActivity {
    protected BGATitlebar mTitlebar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(layoutResID, true);
    }

    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(layoutResID, false);
    }

    private void initViewStubContentView(@LayoutRes int layoutResID, boolean isLinear) {
        super.setContentView(R.layout.titlebar_viewstub);
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
        if (isLinear) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) viewStub.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, R.id.titlebar);
        }
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitlebar.setTitleText(title);
    }

    @Override
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
        onBackPressed();
    }

    protected void onClickRight() {
    }

    protected void onClickTitle() {
    }
}
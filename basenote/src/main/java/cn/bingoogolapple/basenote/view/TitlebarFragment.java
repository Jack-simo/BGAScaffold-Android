package cn.bingoogolapple.basenote.view;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.ViewStubCompat;

import cn.bingoogolapple.basenote.R;
import cn.bingoogolapple.titlebar.BGATitlebar;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/11 下午2:49
 * 描述:
 */
public abstract class TitleBarFragment extends BaseFragment {
    protected BGATitlebar mTitleBar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.rootlayout_linear, layoutResID);
    }

    public void setNoLinearContentView(@LayoutRes int layoutResID) {
        initViewStubContentView(R.layout.rootlayout_frame, layoutResID);
    }

    private void initViewStubContentView(@LayoutRes int rootLayoutResID, @LayoutRes int layoutResID) {
        super.setContentView(rootLayoutResID);

        ViewStubCompat toolbarVs = getViewById(R.id.toolbarVs);
        toolbarVs.setLayoutResource(R.layout.inc_titlebar);
        toolbarVs.inflate();

        mTitleBar = getViewById(R.id.titleBar);
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

        ViewStubCompat viewStub = getViewById(R.id.contentVs);
        viewStub.setLayoutResource(layoutResID);
        viewStub.inflate();
    }

    public BGATitlebar getTitleBar() {
        return mTitleBar;
    }

    protected void onClickLeft() {
    }

    protected void onClickRight() {
    }

    protected void onClickTitle() {
    }
}
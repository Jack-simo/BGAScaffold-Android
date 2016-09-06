package cn.bingoogolapple.bottomnavigation.fragment;

import android.os.Bundle;
import android.widget.PopupWindow;

import java.util.List;

import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.bottomnavigation.R;
import cn.bingoogolapple.bottomnavigation.activity.TestCountDownActivity;
import cn.bingoogolapple.bottomnavigation.activity.TestWebViewActivity;
import cn.bingoogolapple.bottomnavigation.model.HomeCategory;
import cn.bingoogolapple.bottomnavigation.pw.HomeCategoryPopupWindow;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/3 下午8:29
 * 描述:
 */
public class HomeFragment extends BaseMainFragment {
    private HomeCategoryPopupWindow mCategoryPw;
    private List<HomeCategory> mHomeCategorys;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_home);
    }

    @Override
    protected void setListener() {
    }

    @Override
    protected void processLogic(Bundle savedInstanceState) {
        setLeftDrawable(R.drawable.selector_nav_friendsearch);
        setRightDrawable(R.drawable.selector_nav_pop);
        setTitle(R.string.home);
        setTitleDrawable(R.drawable.selector_nav_arrow_orange);
    }

    @Override
    protected void onClickLeft() {
        mActivity.forward(TestCountDownActivity.class);
    }

    @Override
    protected void onClickRight() {
        mActivity.forward(TestWebViewActivity.class);
    }

    @Override
    protected void onClickTitle() {
        if (mCategoryPw == null) {
            mCategoryPw = new HomeCategoryPopupWindow(getActivity(), mTitlebar.getTitleCtv());
            mCategoryPw.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mTitlebar.setTitleCtvChecked(false);
                }
            });
            mCategoryPw.setDelegate(new HomeCategoryPopupWindow.HomeCategoryPopupWindowDelegate() {
                @Override
                public void onSelectCategory(HomeCategory category) {
                    ToastUtil.show("选择了分类：" + category.title);
                }
            });
        }

        if (mHomeCategorys == null) {
            mHomeCategorys = HomeCategory.getTestDatas();
        }
        mCategoryPw.setCategorys(mHomeCategorys);
        mCategoryPw.show();
        mTitlebar.setTitleCtvChecked(true);
    }
}
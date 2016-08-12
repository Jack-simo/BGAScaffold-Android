package cn.bingoogolapple.bottomnavigation.pw;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.mikepenz.materialize.util.UIUtils;

import java.util.List;

import cn.bingoogolapple.androidcommon.adapter.BGAOnItemChildClickListener;
import cn.bingoogolapple.androidcommon.adapter.BGARecyclerViewAdapter;
import cn.bingoogolapple.androidcommon.adapter.BGAViewHolderHelper;
import cn.bingoogolapple.basenote.pw.BasePopupWindow;
import cn.bingoogolapple.basenote.util.ToastUtil;
import cn.bingoogolapple.basenote.util.UIUtil;
import cn.bingoogolapple.bottomnavigation.widget.BGABadgeCheckedTextView;
import cn.bingoogolapple.bottomnavigation.R;
import cn.bingoogolapple.bottomnavigation.model.HomeCategory;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/7/5 上午9:42
 * 描述:
 */
public class HomeCategoryPopupWindow extends BasePopupWindow {
    private RecyclerView mCategoryRv;
    private CategoryAdapter mCategoryAdapter;
    private HomeCategoryPopupWindowDelegate mDelegate;
    List<HomeCategory> mHomeCategories;

    public HomeCategoryPopupWindow(Activity activity, View anchorView) {
        super(activity, R.layout.popwindow_home_category, anchorView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void initView() {
        mCategoryRv = getViewById(R.id.rv_home_category_data);
    }

    @Override
    protected void setListener() {
        getViewById(R.id.btn_home_category_edit).setOnClickListener(this);
        mCategoryAdapter = new CategoryAdapter(mCategoryRv);
        mCategoryAdapter.setOnItemChildClickListener(new BGAOnItemChildClickListener() {
            @Override
            public void onItemChildClick(ViewGroup parent, View childView, int position) {
                if (mDelegate != null && childView.getId() == R.id.bctv_item_home_category) {
                    HomeCategory homeCategory = mCategoryAdapter.getItem(position);
                    for (HomeCategory category : mHomeCategories) {
                        category.selected = false;
                    }
                    homeCategory.selected = true;
                    homeCategory.hasNewStatus = false;
                    mDelegate.onSelectCategory(homeCategory);
                }
                dismiss();
            }
        });
    }

    @Override
    protected void processLogic() {
        mCategoryRv.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mCategoryRv.setAdapter(mCategoryAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_home_category_edit) {
            ToastUtil.show("点击了编辑分组");
            dismiss();
        }
    }

    public void setCategorys(List<HomeCategory> homeCategories) {
        mHomeCategories = homeCategories;
        mCategoryAdapter.setData(mHomeCategories);
    }

    @Override
    public void show() {
        setWidth(UIUtils.getScreenWidth(mActivity) / 2);
        setHeight(UIUtils.getScreenHeight(mActivity) / 2);

        showAsDropDown(mAnchorView, (mAnchorView.getWidth() - getWidth()) / 2, -UIUtil.dp2px(12));
    }

    public void setDelegate(HomeCategoryPopupWindowDelegate delegate) {
        mDelegate = delegate;
    }

    private static class CategoryAdapter extends BGARecyclerViewAdapter<HomeCategory> {

        public CategoryAdapter(RecyclerView recyclerView) {
            super(recyclerView, R.layout.item_home_category);
        }

        @Override
        protected void setItemChildListener(BGAViewHolderHelper viewHolderHelper) {
            viewHolderHelper.setItemChildClickListener(R.id.bctv_item_home_category);
        }

        @Override
        protected void fillData(BGAViewHolderHelper viewHolderHelper, int position, HomeCategory homeCategory) {
            if (TextUtils.isEmpty(homeCategory.header)) {
                viewHolderHelper.setVisibility(R.id.ll_item_home_header, View.GONE);
            } else {
                viewHolderHelper.setVisibility(R.id.ll_item_home_header, View.VISIBLE)
                        .setText(R.id.tv_item_home_header, homeCategory.header);
            }
            BGABadgeCheckedTextView categoryBctv = viewHolderHelper.getView(R.id.bctv_item_home_category);
            if (homeCategory.selected) {
                categoryBctv.setChecked(true);
            } else {
                categoryBctv.setChecked(false);
            }
            if (homeCategory.hasNewStatus) {
                categoryBctv.showCirclePointBadge();
            } else {
                categoryBctv.hiddenBadge();
            }
            categoryBctv.setText(homeCategory.title);
        }
    }

    public interface HomeCategoryPopupWindowDelegate {
        void onSelectCategory(HomeCategory homeCategory);
    }
}
/**
 * Copyright 2016 bingoogolapple
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bingoogolapple.scaffolding.widget;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.bingoogolapple.scaffolding.R;
import cn.bingoogolapple.scaffolding.util.AppManager;

public class Divider extends RecyclerView.ItemDecoration {
    private Drawable mDividerDrawable;
    private int mLeftMargin;
    private int mRightMargin;

    public Divider(@DrawableRes int resId) {
        mDividerDrawable = ContextCompat.getDrawable(AppManager.getApp(), resId);
    }

    public static Divider newShapeDivider() {
        return new Divider(R.drawable.shape_divider);
    }

    public static Divider newBitmapDivider() {
        return new Divider(R.drawable.list_divider);
    }

    /**
     * 设置左边距和右边距
     *
     * @param resId
     * @return
     */
    public Divider setMargin(@DimenRes int resId) {
        mLeftMargin = AppManager.getApp().getResources().getDimensionPixelOffset(resId);
        mRightMargin = mLeftMargin;
        return this;
    }

    /**
     * 设置左边距
     *
     * @param resId
     * @return
     */
    public Divider setLeftMargin(@DimenRes int resId) {
        mLeftMargin = AppManager.getApp().getResources().getDimensionPixelOffset(resId);
        return this;
    }

    /**
     * 设置右边距
     *
     * @param resId
     * @return
     */
    public Divider setRightMargin(@DimenRes int resId) {
        mRightMargin = AppManager.getApp().getResources().getDimensionPixelOffset(resId);
        return this;
    }

    /**
     * 设置分割线颜色
     *
     * @param resId
     * @param isSrcTop
     * @return
     */
    public Divider setColor(@ColorRes int resId, boolean isSrcTop) {
        mDividerDrawable.setColorFilter(AppManager.getApp().getResources().getColor(resId), isSrcTop ? PorterDuff.Mode.SRC_ATOP : PorterDuff.Mode.SRC);
        return this;
    }

    // 如果等于分割线的宽度或高度的话可以不用重写该方法
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == parent.getChildCount() - 1) {
            outRect.set(0, 0, 0, 0);
        } else {
            outRect.set(0, 0, 0, mDividerDrawable.getIntrinsicHeight());
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft() + mLeftMargin;
        int right = parent.getWidth() - parent.getPaddingRight() - mRightMargin;
        View child;
        RecyclerView.LayoutParams layoutParams;
        int top;
        int bottom;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            child = parent.getChildAt(i);
            layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            top = child.getBottom() + layoutParams.bottomMargin;
            bottom = top + mDividerDrawable.getIntrinsicHeight();
            mDividerDrawable.setBounds(left, top, right, bottom);
            mDividerDrawable.draw(c);
        }
    }

}
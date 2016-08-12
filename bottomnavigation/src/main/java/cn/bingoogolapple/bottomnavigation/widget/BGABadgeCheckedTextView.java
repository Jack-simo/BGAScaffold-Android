package cn.bingoogolapple.bottomnavigation.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.TintContextWrapper;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.bingoogolapple.badgeview.BGABadgeViewHelper;
import cn.bingoogolapple.badgeview.BGABadgeable;
import cn.bingoogolapple.badgeview.BGADragDismissDelegate;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/13 上午12:22
 * 描述:
 */
public class BGABadgeCheckedTextView extends AppCompatCheckedTextView implements BGABadgeable {
    private BGABadgeViewHelper mBadgeViewHelper;

    public BGABadgeCheckedTextView(Context context) {
        this(context, null);
    }

    public BGABadgeCheckedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkedTextViewStyle);
    }

    public BGABadgeCheckedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(TintContextWrapper.wrap(context), attrs, defStyleAttr);
        mBadgeViewHelper = new BGABadgeViewHelper(this, context, attrs, BGABadgeViewHelper.BadgeGravity.RightCenter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mBadgeViewHelper.onTouchEvent(event);
    }

    @Override
    public boolean callSuperOnTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mBadgeViewHelper.drawBadge(canvas);
    }

    @Override
    public void showCirclePointBadge() {
        mBadgeViewHelper.showCirclePointBadge();
    }

    @Override
    public void showTextBadge(String badgeText) {
        mBadgeViewHelper.showTextBadge(badgeText);
    }

    @Override
    public void hiddenBadge() {
        mBadgeViewHelper.hiddenBadge();
    }

    @Override
    public void showDrawableBadge(Bitmap bitmap) {
        mBadgeViewHelper.showDrawable(bitmap);
    }

    @Override
    public void setDragDismissDelegage(BGADragDismissDelegate delegate) {
        mBadgeViewHelper.setDragDismissDelegage(delegate);
    }

    @Override
    public boolean isShowBadge() {
        return mBadgeViewHelper.isShowBadge();
    }

    @Override
    public BGABadgeViewHelper getBadgeViewHelper() {
        return mBadgeViewHelper;
    }
}
/**
 * Copyright 2016 bingoogolapple
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bingoogolapple.scaffold.view

import android.content.Intent
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.annotation.IntRange
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v7.widget.Toolbar
import android.support.v7.widget.ViewStubCompat
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View

import com.afollestad.materialdialogs.MaterialDialog
import com.jaeger.library.StatusBarUtil
import com.jakewharton.rxbinding2.view.RxView
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import java.util.concurrent.TimeUnit

import cn.bingoogolapple.scaffold.R
import cn.bingoogolapple.scaffold.util.PermissionUtil
import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil
import cn.bingoogolapple.swipebacklayout.BGASwipeBackHelper
import cn.bingoogolapple.titlebar.BGAOnNoDoubleClickListener
import cn.bingoogolapple.titlebar.BGATitleBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import pub.devrel.easypermissions.EasyPermissions

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
abstract class MvcActivity : RxAppCompatActivity(), EasyPermissions.PermissionCallbacks, PermissionUtil.Delegate, BGATitleBar.Delegate, BGASwipeBackHelper.Delegate {
    protected lateinit var mSwipeBackHelper: BGASwipeBackHelper
    protected var mLoadingDialog: MaterialDialog? = null

    protected lateinit var mTitleBar: BGATitleBar
    protected lateinit var mToolbar: Toolbar

    /**
     * 有 TitleBar 或者 Toolbar 时，是否为线性布局
     *
     * @return
     */
    protected val isLinear: Boolean
        get() = true

    protected val topBarType: TopBarType
        get() = TopBarType.None

    /**
     * 获取布局文件根视图
     *
     * @return
     */
    @get:LayoutRes
    protected abstract val rootLayoutResID: Int

    /**
     * 点击非 EditText 时，是否自动关闭键盘
     *
     * @return
     */
    protected val isAutoCloseKeyboard: Boolean
        get() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        initSwipeBackFinish()
        super.onCreate(savedInstanceState)

        initContentView()
        initView(savedInstanceState)
        setListener()
        processLogic(savedInstanceState)
    }

    /**
     * 初始化滑动返回。在 super.onCreate(savedInstanceState) 之前调用该方法
     */
    private fun initSwipeBackFinish() {
        mSwipeBackHelper = BGASwipeBackHelper(this, this)
        // 设置滑动返回是否可用。默认值为 true
        mSwipeBackHelper.setSwipeBackEnable(true)
        // 设置是否仅仅跟踪左侧边缘的滑动返回。默认值为 true
        mSwipeBackHelper.setIsOnlyTrackingLeftEdge(true)
        // 设置是否是微信滑动返回样式。默认值为 true
        mSwipeBackHelper.setIsWeChatStyle(true)
        // 设置阴影资源 id。默认值为 R.drawable.bga_sbl_shadow
        mSwipeBackHelper.setShadowResId(R.drawable.bga_sbl_shadow)
        // 设置是否显示滑动返回的阴影效果。默认值为 true
        mSwipeBackHelper.setIsNeedShowShadow(true)
        // 设置阴影区域的透明度是否根据滑动的距离渐变。默认值为 true
        mSwipeBackHelper.setIsShadowAlphaGradient(true)
        // 设置触发释放后自动滑动返回的阈值，默认值为 0.3f
        mSwipeBackHelper.setSwipeBackThreshold(0.3f)
        // 设置底部导航条是否悬浮在内容上，默认值为 false
        mSwipeBackHelper.setIsNavigationBarOverlap(false)
    }

    /**
     * 是否支持滑动返回。这里在父类中默认返回 true 来支持滑动返回，如果某个界面不想支持滑动返回则重写该方法返回 false 即可
     *
     * @return
     */
    override fun isSupportSwipeBack(): Boolean {
        return true
    }

    /**
     * 正在滑动返回
     *
     * @param slideOffset 从 0 到 1
     */
    override fun onSwipeBackLayoutSlide(slideOffset: Float) {}

    /**
     * 没达到滑动返回的阈值，取消滑动返回动作，回到默认状态
     */
    override fun onSwipeBackLayoutCancel() {}

    /**
     * 滑动返回执行完毕，销毁当前 Activity
     */
    override fun onSwipeBackLayoutExecuted() {
        mSwipeBackHelper.swipeBackward()
    }

    override fun onBackPressed() {
        // 正在滑动返回的时候取消返回按钮事件
        if (mSwipeBackHelper.isSliding) {
            return
        }
        mSwipeBackHelper.backward()
    }

    protected open fun initContentView() {
        if (topBarType == TopBarType.None) {
            setContentView(rootLayoutResID)
        } else if (topBarType == TopBarType.TitleBar) {
            initTitleBarContentView()
        } else if (topBarType == TopBarType.Toolbar) {
            initToolbarContentView()
        }
    }

    protected fun initToolbarContentView() {
        super.setContentView(if (isLinear) R.layout.rootlayout_linear else R.layout.rootlayout_merge)

        val toolbarVs = findViewById<ViewStubCompat>(R.id.toolbarVs)
        toolbarVs.layoutResource = R.layout.inc_toolbar
        toolbarVs.inflate()

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val viewStub = findViewById<ViewStubCompat>(R.id.contentVs)
        viewStub.layoutResource = rootLayoutResID
        viewStub.inflate()
    }

    protected fun initTitleBarContentView() {
        super.setContentView(if (isLinear) R.layout.rootlayout_linear else R.layout.rootlayout_merge)

        val toolbarVs = findViewById<ViewStubCompat>(R.id.toolbarVs)
        toolbarVs.layoutResource = R.layout.inc_titlebar
        toolbarVs.inflate()

        mTitleBar = findViewById(R.id.titleBar)

        val viewStub = findViewById<ViewStubCompat>(R.id.contentVs)
        viewStub.layoutResource = rootLayoutResID
        viewStub.inflate()
    }

    override fun setTitle(title: CharSequence) {
        if (topBarType == TopBarType.None) {
            super.setTitle(title)
        } else if (topBarType == TopBarType.TitleBar) {
            mTitleBar.setTitleText(title)
        } else if (topBarType == TopBarType.Toolbar) {
            supportActionBar!!.title = title
        }
    }

    override fun onClickLeftCtv() {
        onBackPressed()
    }

    override fun onClickRightCtv() {}

    override fun onClickRightSecondaryCtv() {}

    override fun onClickTitleCtv() {}

    /**
     * 设置点击事件，并防止重复点击
     *
     * @param id
     * @param consumer
     */
    protected fun setOnClick(@IdRes id: Int, consumer: Consumer<View>) {
        setOnClick(findViewById<View>(id), consumer)
    }

    /**
     * 设置点击事件，并防止重复点击
     *
     * @param view
     * @param consumer
     */
    protected fun setOnClick(view: View, consumer: Consumer<View>) {
        view.setOnClickListener(object : BGAOnNoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                consumer.accept(v)
            }
        })
    }

    /**
     * 初始化View控件
     */
    protected abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 给View控件添加事件监听器
     */
    protected abstract fun setListener()

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract fun processLogic(savedInstanceState: Bundle?)

    /**
     * 执行跳转到下一个Activity的动画
     */
    fun executeForwardAnim() {
        mSwipeBackHelper.executeForwardAnim()
    }

    /**
     * 执行回到到上一个Activity的动画
     */
    fun executeBackwardAnim() {
        mSwipeBackHelper.executeBackwardAnim()
    }

    /**
     * 跳转到下一个 Activity，并且销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    fun forwardAndFinish(cls: Class<*>) {
        mSwipeBackHelper.forwardAndFinish(cls)
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    fun forward(cls: Class<*>) {
        mSwipeBackHelper.forward(cls)
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls         下一个 Activity 的 Class
     * @param requestCode 请求码
     */
    fun forward(cls: Class<*>, requestCode: Int) {
        mSwipeBackHelper.forward(cls, requestCode)
    }

    /**
     * 跳转到下一个 Activity，销毁当前 Activity
     *
     * @param intent 下一个 Activity 的意图对象
     */
    fun forwardAndFinish(intent: Intent) {
        mSwipeBackHelper.forwardAndFinish(intent)
    }

    /**
     * 跳转到下一个 Activity,不销毁当前 Activity
     *
     * @param intent 下一个 Activity 的意图对象
     */
    fun forward(intent: Intent) {
        mSwipeBackHelper.forward(intent)
    }

    /**
     * 跳转到下一个 Activity,不销毁当前 Activity
     *
     * @param intent      下一个 Activity 的意图对象
     * @param requestCode 请求码
     */
    fun forward(intent: Intent, requestCode: Int) {
        mSwipeBackHelper.forward(intent, requestCode)
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity
     */
    fun backward() {
        mSwipeBackHelper.backward()
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个 Activity 的 Class
     */
    fun backwardAndFinish(cls: Class<*>) {
        mSwipeBackHelper.backwardAndFinish(cls)
    }

    /**
     * 显示加载对话框
     *
     * @param resId
     */
    fun showLoadingDialog(@StringRes resId: Int) {
        showLoadingDialog(getString(resId))
    }

    fun showLoadingDialog(msg: String) {
        if (mLoadingDialog == null) {
            mLoadingDialog = MaterialDialog.Builder(this)
                    .progress(true, 0)
                    .cancelable(false)
                    .build()
        }
        mLoadingDialog!!.setContent(msg)
        mLoadingDialog!!.show()
    }

    /**
     * 隐藏加载对话框
     */
    fun dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog!!.isShowing) {
            mLoadingDialog!!.dismiss()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        BGAKeyboardUtil.handleAutoCloseKeyboard(isAutoCloseKeyboard, currentFocus, ev, this)
        return super.dispatchTouchEvent(ev)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        PermissionUtil.onPermissionsDenied(this, this, requestCode)
    }

    /**
     * 某些权限被永久拒绝
     *
     * @param requestCode 权限请求码
     */
    override fun onSomePermissionDenied(requestCode: Int) {}

    /**
     * 点击取消打开权限设置界面
     *
     * @param requestCode 权限请求码
     */
    override fun onClickCancelOpenPermissionsSettingsScreen(requestCode: Int) {}

    /**
     * 设置状态栏颜色
     *
     * @param color
     */
    protected fun setStatusBarColor(@ColorInt color: Int) {
        setStatusBarColor(color, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 设置状态栏颜色
     *
     * @param color
     * @param statusBarAlpha 透明度
     */
    fun setStatusBarColor(@ColorInt color: Int, @IntRange(from = 0, to = 255) statusBarAlpha: Int) {
        StatusBarUtil.setColorForSwipeBack(this, color, statusBarAlpha)
    }
}
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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.Toolbar
import android.support.v7.widget.ViewStubCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jakewharton.rxbinding2.view.RxView
import com.orhanobut.logger.Logger
import com.trello.rxlifecycle2.components.support.RxFragment
import java.util.concurrent.TimeUnit

import cn.bingoogolapple.scaffold.R
import cn.bingoogolapple.scaffold.util.AppManager
import cn.bingoogolapple.scaffold.util.PermissionUtil
import cn.bingoogolapple.swipebacklayout.BGAKeyboardUtil
import cn.bingoogolapple.titlebar.BGAOnNoDoubleClickListener
import cn.bingoogolapple.titlebar.BGATitleBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import pub.devrel.easypermissions.EasyPermissions

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:57
 * 描述:
 */
abstract class MvcFragment : RxFragment(), EasyPermissions.PermissionCallbacks, PermissionUtil.Delegate, BGATitleBar.Delegate {
    protected var mContentView: View? = null

    protected var mIsLoadedData = false

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 避免多次从xml中加载布局文件
        if (mContentView == null) {
            initContentView()
            setListener()
            processLogic(savedInstanceState)
        } else {
            val parent = mContentView!!.parent as ViewGroup
            parent?.removeView(mContentView)
        }
        return mContentView
    }

    protected fun initContentView() {
        if (topBarType == TopBarType.None) {
            mContentView = LayoutInflater.from(activity).inflate(rootLayoutResID, null)
        } else if (topBarType == TopBarType.TitleBar) {
            initTitleBarContentView()
        } else if (topBarType == TopBarType.Toolbar) {
            initToolbarContentView()
        }
    }

    protected fun initTitleBarContentView() {
        mContentView = LayoutInflater.from(activity).inflate(if (isLinear) R.layout.rootlayout_linear else R.layout.rootlayout_frame, null)

        val toolbarVs = findViewById<ViewStubCompat>(R.id.toolbarVs)
        toolbarVs.layoutResource = R.layout.inc_titlebar
        toolbarVs.inflate()

        mTitleBar = findViewById(R.id.titleBar)
        mTitleBar.setDelegate(this)

        val viewStub = findViewById<ViewStubCompat>(R.id.contentVs)
        viewStub.layoutResource = rootLayoutResID
        viewStub.inflate()
    }

    override fun onClickLeftCtv() {}

    override fun onClickRightCtv() {}

    override fun onClickRightSecondaryCtv() {}

    override fun onClickTitleCtv() {}

    protected fun initToolbarContentView() {
        mContentView = LayoutInflater.from(activity).inflate(if (isLinear) R.layout.rootlayout_linear else R.layout.rootlayout_frame, null)

        val toolbarVs = findViewById<ViewStubCompat>(R.id.toolbarVs)
        toolbarVs.layoutResource = R.layout.inc_toolbar
        toolbarVs.inflate()
        mToolbar = findViewById(R.id.toolbar)

        val viewStub = findViewById<ViewStubCompat>(R.id.contentVs)
        viewStub.layoutResource = rootLayoutResID
        viewStub.inflate()

        setHasOptionsMenu(true)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            handleOnVisibilityChangedToUser(isVisibleToUser)
        }
    }

    override fun onResume() {
        super.onResume()
        if (userVisibleHint) {
            handleOnVisibilityChangedToUser(true)
        }
    }

    override fun onPause() {
        super.onPause()
        if (userVisibleHint) {
            handleOnVisibilityChangedToUser(false)
        }
    }

    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser
     */
    private fun handleOnVisibilityChangedToUser(isVisibleToUser: Boolean) {
        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                Logger.d(this.javaClass.simpleName + " 懒加载一次")
                mIsLoadedData = true
                onLazyLoadOnce()
            }
            Logger.d(this.javaClass.simpleName + " 对用户可见")
            onVisibleToUser()
        } else {
            // 对用户不可见
            Logger.d(this.javaClass.simpleName + " 对用户不可见")
            onInvisibleToUser()
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected fun onLazyLoadOnce() {}

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected fun onVisibleToUser() {}

    /**
     * 对用户不可见时触发该方法
     */
    protected fun onInvisibleToUser() {}

    /**
     * 初始化 View 控件
     */
    protected open fun initView(savedInstanceState: Bundle) {}

    /**
     * 给 View 控件添加事件监听器
     */
    protected abstract fun setListener()

    /**
     * 处理业务逻辑，状态恢复等操作
     *
     * @param savedInstanceState
     */
    protected abstract fun processLogic(savedInstanceState: Bundle?)

    /**
     * 跳转到下一个 Activity，并且销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    fun forwardAndFinish(cls: Class<*>) {
        forward(cls)
        finishActivity()
    }

    /**
     * 跳转到下一个 Activity，不销毁当前 Activity
     *
     * @param cls 下一个 Activity 的 Class
     */
    fun forward(cls: Class<*>) {
        withActivity(Consumer { activity ->
            BGAKeyboardUtil.closeKeyboard(activity)
            startActivity(Intent(activity, cls))
            (activity as MvcActivity).executeForwardAnim()
        })
    }

    fun forward(cls: Class<*>, requestCode: Int) {
        withActivity(Consumer { activity ->
            forward(Intent(activity, cls), requestCode)
        })
    }

    fun forwardAndFinish(intent: Intent) {
        forward(intent)
        finishActivity()
    }

    fun forward(intent: Intent) {
        withActivity(Consumer { activity ->
            BGAKeyboardUtil.closeKeyboard(activity)
            startActivity(intent)
            (activity as MvcActivity).executeForwardAnim()
        })
    }

    fun forward(intent: Intent, requestCode: Int) {
        withActivity(Consumer { activity ->
            BGAKeyboardUtil.closeKeyboard(activity)
            startActivityForResult(intent, requestCode)
            (activity as MvcActivity).executeForwardAnim()
        })
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity
     */
    fun backward() {
        withActivity(Consumer { activity ->
            BGAKeyboardUtil.closeKeyboard(activity)
            activity.finish()
            (activity as MvcActivity).executeBackwardAnim()
        })
    }

    /**
     * 回到上一个 Activity，并销毁当前 Activity（应用场景：欢迎、登录、注册这三个界面）
     *
     * @param cls 上一个 Activity 的 Class
     */
    fun backwardAndFinish(cls: Class<*>) {
        withActivity(Consumer { activity ->
            BGAKeyboardUtil.closeKeyboard(activity)
            startActivity(Intent(activity, cls))
            (activity as MvcActivity).executeBackwardAnim()
            activity.finish()
        })
    }

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
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return
    </VT> */
    protected fun <VT : View> findViewById(@IdRes id: Int): VT {
        return mContentView!!.findViewById<View>(id) as VT
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getInstance().refWatcherWatchFragment(this)
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

    protected fun withActivity(consumer: Consumer<Activity>?) {
        if (consumer == null) {
            return
        }
        if (activity == null || activity!!.isDestroyed) {
            return
        }

        consumer.accept(activity)
    }

    fun finishActivity() {
        withActivity(Consumer { activity -> activity.finish() })
    }

    fun shortToast(resId: Int, vararg formatArgs: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun shortToast(content: CharSequence) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun showProgressDialog(resId: Int, cancelAble: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun dismissProgressDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
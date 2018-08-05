/**
 * Copyright 2018 bingoogolapple
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

package cn.bingoogolapple.scaffold.mvp.impl

import android.content.res.Configuration
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.UiThread
import cn.bingoogolapple.scaffold.mvp.IMvpView
import cn.bingoogolapple.scaffold.mvp.IPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:05/08/2018 7:13 PM
 * 描述:
 */
abstract class BasePresenter<out V : IMvpView<BasePresenter<V>>> : IPresenter<V> {
    private val mCompositeDisposable = CompositeDisposable()
    override lateinit var mView: WeakReference<@UnsafeVariance V>
    private var nullView: V? = null

    private fun generateNullViewIfNeed(): V {
        if (nullView == null) {
            var viewClass: Class<V>? = null
            var currentClass: Class<*> = javaClass

            while (viewClass == null) {
                var genericSuperclassType = currentClass.genericSuperclass
                while (genericSuperclassType !is ParameterizedType) {
                    currentClass = currentClass.superclass
                    genericSuperclassType = currentClass.genericSuperclass
                }
                val types = genericSuperclassType.actualTypeArguments
                for (i in types.indices) {
                    val genericType = types[i] as Class<*>
                    if (genericType.isInterface && IMvpView::class.java!!.isAssignableFrom(genericType)) {
                        viewClass = genericType as Class<V>
                        break
                    }
                }
                currentClass = currentClass.superclass
            }

            nullView = DefaultInvocationHandler.of(viewClass)
        }
        return nullView as V
    }

    @UiThread
    fun getView(): V {
        return if (mView.get() != null) mView.get()!! else generateNullViewIfNeed()
    }

    fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun onCreate(savedInstanceState: Bundle?) = Unit
    override fun onStart() = Unit
    override fun onResume() = Unit
    override fun onStop() = Unit
    override fun onPause() = Unit

    @CallSuper
    override fun onDestroy() {
        mCompositeDisposable.clear()
    }

    override fun onSaveInstanceState(outState: Bundle) = Unit
    override fun onViewStateRestored(savedInstanceState: Bundle?) = Unit
    override fun onConfigurationChanged(newConfig: Configuration) = Unit
}
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

package cn.bingoogolapple.scaffold.mvp.impl

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import cn.bingoogolapple.scaffold.BR

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:57
 * 描述:
 */
abstract class MvpBindingFragment<B : ViewDataBinding, out P : BasePresenter<MvpFragment<P>>> : MvpFragment<P>() {
    protected var mBinding: B? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 避免多次从xml中加载布局文件
        if (mBinding == null) {
            mBinding = DataBindingUtil.inflate(inflater, rootLayoutResID, container, false)
            mBinding!!.setVariable(BR.eventHandler, this)
            initView(savedInstanceState!!)
            setListener()
            processLogic(savedInstanceState)
        } else {
            val parent = mBinding!!.root.parent as ViewGroup
            parent?.removeView(mBinding!!.root)
        }
        return mBinding!!.root
    }

    override fun initView(savedInstanceState: Bundle) {}

    override fun setListener() {}
}
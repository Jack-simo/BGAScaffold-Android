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

import android.databinding.DataBindingUtil
import android.databinding.OnRebindCallback
import android.databinding.ViewDataBinding
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.ViewGroup

import cn.bingoogolapple.scaffold.BR

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午5:07
 * 描述:
 */
abstract class MvcBindingActivity<B : ViewDataBinding> : MvcActivity() {
    protected lateinit var mBinding: B

    override fun initContentView() {
        mBinding = DataBindingUtil.setContentView(this, rootLayoutResID)
        mBinding.setVariable(BR.eventHandler, this)

        mBinding.addOnRebindCallback(object : OnRebindCallback<ViewDataBinding>() {
            override fun onPreBind(binding: ViewDataBinding): Boolean {
                val view = binding.root as ViewGroup
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    TransitionManager.beginDelayedTransition(view)
                }
                return true
            }
        })
    }

    override fun initView(savedInstanceState: Bundle?) {}

    override fun setListener() {}
}
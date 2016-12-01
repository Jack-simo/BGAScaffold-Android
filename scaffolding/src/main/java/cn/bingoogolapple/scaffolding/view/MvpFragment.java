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

package cn.bingoogolapple.scaffolding.view;

import android.support.annotation.StringRes;

import com.trello.rxlifecycle.LifecycleProvider;

import cn.bingoogolapple.scaffolding.presenter.BasePresenter;
import cn.bingoogolapple.scaffolding.util.ToastUtil;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/9/2 下午10:57
 * 描述:
 */
public abstract class MvpFragment<P extends BasePresenter> extends MvcFragment implements BaseView {
    protected P mPresenter;

    @Override
    public void showMsg(@StringRes int resId) {
        ToastUtil.show(resId);
    }

    @Override
    public void showMsg(String msg) {
        ToastUtil.show(msg);
    }

    @Override
    public MvcActivity getBaseActivity() {
        return mActivity;
    }

    @Override
    public LifecycleProvider getLifecycleProvider() {
        return this;
    }
}
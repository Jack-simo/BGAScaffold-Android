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

package cn.bingoogolapple.scaffolding.util;

import android.app.Activity;
import android.support.annotation.StringRes;

import com.orhanobut.logger.Logger;

import cn.bingoogolapple.scaffolding.R;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 上午1:15
 * 描述:
 */
public abstract class RemoteSubscriber<T> extends LocalSubscriber<T> {

    public RemoteSubscriber() {
    }

    public RemoteSubscriber(Activity activity) {
        super(activity);
    }

    public RemoteSubscriber(Activity activity, boolean cancelable) {
        super(activity, cancelable);
    }

    public RemoteSubscriber(Activity activity, @StringRes int resId) {
        super(activity, resId);
    }

    public RemoteSubscriber(Activity activity, @StringRes int resId, boolean cancelable) {
        super(activity, resId, cancelable);
    }

    public RemoteSubscriber(Activity activity, String msg) {
        super(activity, msg);
    }

    public RemoteSubscriber(Activity activity, String msg, boolean cancelable) {
        super(activity, msg, cancelable);
    }

    @Override
    public void onError(Throwable e) {
        Logger.e(e, RemoteSubscriber.class.getSimpleName());

        dismissLoadingDialog();

        if (!NetUtil.isNetworkAvailable()) {
            onError(AppManager.getApp().getString(R.string.network_unavailable));
        } else if (e instanceof ApiException) {
            onError(e.getMessage());
            try {
                if (AppManager.getInstance().isFrontStage()) {
                    RxUtil.runInIoThread().subscribe(aVoid -> AppManager.getInstance().handleServerException((ApiException) e));
                }
            } catch (Exception e2) {
            }
        } else {
            onError(AppManager.getApp().getString(R.string.try_again_later));
        }
    }
}

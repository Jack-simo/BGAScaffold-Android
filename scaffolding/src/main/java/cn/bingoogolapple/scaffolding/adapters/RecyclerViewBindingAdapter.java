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

package cn.bingoogolapple.scaffolding.adapters;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;

import cn.bingoogolapple.scaffolding.widget.Divider;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/13 上午10:21
 * 描述:
 */
public class RecyclerViewBindingAdapter {

    @BindingAdapter("rv_isShapeDivider")
    public static void addItemDecoration(RecyclerView recyclerView, boolean isShapeDivider) {
        recyclerView.addItemDecoration(isShapeDivider ? Divider.newShapeDivider() : Divider.newBitmapDivider());
    }

    @BindingAdapter(value = {"rv_onScrollStateChanged", "rv_onScrolled"}, requireAll = false)
    public static void addOnScrollListener(RecyclerView view, final OnScrollStateChanged stateChanged, final OnScrolled onScrolled) {
        final RecyclerView.OnScrollListener newValue;
        if (stateChanged == null && onScrolled == null) {
            newValue = null;
        } else {
            newValue = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (stateChanged != null) {
                        stateChanged.onScrollStateChanged(recyclerView, newState);
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (onScrolled != null) {
                        onScrolled.onScrolled(recyclerView, dx, dy);
                    }
                }
            };
        }
        if (newValue != null) {
            view.addOnScrollListener(newValue);
        }
    }

    public interface OnScrollStateChanged {
        void onScrollStateChanged(RecyclerView recyclerView, int newState);
    }

    public interface OnScrolled {
        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }
}
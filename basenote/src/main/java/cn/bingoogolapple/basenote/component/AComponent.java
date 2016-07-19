package cn.bingoogolapple.basenote.component;

import cn.bingoogolapple.basenote.activity.Dagger2ThreeActivity;
import dagger.Subcomponent;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/20 上午12:21
 * 描述:
 */
@AScope
@Subcomponent(modules = AModule.class)
public interface AComponent {
    void inject(Dagger2ThreeActivity dagger2ThreeActivity);
}
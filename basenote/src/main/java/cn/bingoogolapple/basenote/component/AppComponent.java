package cn.bingoogolapple.basenote.component;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午11:19
 * 描述:
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    Gson getGson();

    AComponent plus(AModule module);
}
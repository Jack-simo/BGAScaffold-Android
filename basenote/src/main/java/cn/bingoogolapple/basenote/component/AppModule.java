package cn.bingoogolapple.basenote.component;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午11:18
 * 描述:
 */
@Module
public class AppModule {

    @Singleton
    @Provides
    public Gson provideGson() {
        return new Gson();
    }
}
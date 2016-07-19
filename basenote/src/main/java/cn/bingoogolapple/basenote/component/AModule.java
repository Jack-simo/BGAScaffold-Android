package cn.bingoogolapple.basenote.component;

import dagger.Module;
import dagger.Provides;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/20 上午12:21
 * 描述:
 */
@Module
public class AModule {

    @PoetryQualifier("A")
    @AScope
    @Provides
    public Poetry getPoetry(){
        return new Poetry("万物美好");
    }

    @PoetryQualifier("B")
    @AScope
    @Provides
    public Poetry getOtherPoetry(){
        return new Poetry("我在中间");
    }
}
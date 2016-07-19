package cn.bingoogolapple.rxjava.component;

import cn.bingoogolapple.basenote.component.Poetry;
import dagger.Module;
import dagger.Provides;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午9:39
 * 描述:
 */
@Module
public class PoetryModule {

    // 这个方法需要一个String参数，在Dagger2注入中，这些参数也是注入形式的，也就是
    // 要有其他对方提供参数poems的生成，不然会造成编译出错
    @Provides
    @PoetryScope
    public Poetry providePoetry(String poems){
        return new Poetry(poems);
    }

    // 这里提供了一个生成String的方法，在这个Module里生成Poetry实例时，会查找到这里
    // 可以为上面提供String类型的参数
    @Provides
    public String providePoems(){
        return "只有意志坚强的人，才能到达彼岸";
    }
}
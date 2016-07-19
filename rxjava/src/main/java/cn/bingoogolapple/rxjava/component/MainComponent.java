package cn.bingoogolapple.rxjava.component;

import cn.bingoogolapple.basenote.App;
import cn.bingoogolapple.basenote.component.AppComponent;
import cn.bingoogolapple.rxjava.ui.activity.Dagger2OneActivity;
import cn.bingoogolapple.rxjava.ui.activity.Dagger2TwoActivity;
import dagger.Component;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午6:04
 * 描述:
 * 用@Component表示这个接口是一个连接器，能用@Component注解的只能是interface或者抽象类。
 *
 * modules = MainModule.class表示Component会从MainModule类中拿那些用@Provides注解的方法来生成需要注入的实例
 *
 * 这样就达到了MainComponent依赖AppComponent。并且这里需要注意的是，MainComponent的作用域不能和AppComponent的作用域一样，否则会报错，一般来讲，我们应该对每个Component都定义不同的作用域。
 */
@PoetryScope
@Component(dependencies = AppComponent.class, modules = {MainModule.class, PoetryModule.class})
public abstract class MainComponent {
    private static MainComponent sComponent;

    public static MainComponent getInstance() {
        if (sComponent == null) {
            sComponent = DaggerMainComponent.builder()
                    .appComponent(App.getInstance().getAppComponent())
                    .build();
        }
        return sComponent;
    }

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性（被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就用inject即可。
     */
    public abstract void inject(Dagger2OneActivity dagger2OneActivity);

    /**
     * 需要用到这个连接器的对象，就是这个对象里面有需要注入的属性（被标记为@Inject的属性）
     * 这里inject表示注入的意思，这个方法名可以随意更改，但建议就用inject即可。
     */
    public abstract void inject(Dagger2TwoActivity dagger2TwoActivity);
}
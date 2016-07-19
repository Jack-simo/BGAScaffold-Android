package cn.bingoogolapple.rxjava.component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午10:41
 * 描述:通过实现自定义@Scope注解，标记当前生成对象的使用范围，标识一个类型的注射器只实例化一次，在同一个作用域内，只会生成一个实例，然后在此作用域内共用一个实例。这样看起来很像单例模式，我们可以查看@Singleton其实就是@Scope的一个默认实现而已。但得是同一个Component对象来生成。
 * 同时在Module与Component加上这个自定义Scope
 */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PoetryScope {
}
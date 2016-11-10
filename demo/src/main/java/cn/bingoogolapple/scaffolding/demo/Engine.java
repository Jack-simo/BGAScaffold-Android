package cn.bingoogolapple.scaffolding.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/10 下午10:09
 * 描述:
 */
public class Engine {
    public static final Gson sGson = new GsonBuilder().create();

    public static String toJsonString(Object object) {
        if (object == null) {
            return "需要转换成Json String的对象为null";
        } else {
            return sGson.toJson(object);
        }
    }
}

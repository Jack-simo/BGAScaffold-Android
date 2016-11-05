package cn.bingoogolapple.scaffolding.engine;

import java.util.HashMap;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/16 上午12:56
 * 描述:
 */
public class ApiParams extends HashMap<String, Object> {

    public ApiParams() {
    }

    public ApiParams(String key, Object value) {
        put(key, value);
    }

    public ApiParams with(String key, Object value) {
        put(key, value);
        return this;
    }
}
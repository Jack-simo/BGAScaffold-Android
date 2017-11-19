package cn.bingoogolapple.scaffolding.util;

import java.util.Collection;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/11/19
 * 描述:
 */
public class CollectionUtil {
    private CollectionUtil() {
    }

    /**
     * 列表是否为空
     *
     * @param args 多参数列表
     * @return 是否为空
     */
    public static boolean isEmpty(Collection collection, Collection... args) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        for (Collection arg : args) {
            if (arg == null || arg.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 列表是否不为空
     *
     * @param args 多参数列表
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Collection collection, Collection... args) {
        return !isEmpty(collection, args);
    }
}

package cn.bingoogolapple.scaffolding.util;

import cn.bingoogolapple.scaffolding.BuildConfig;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/3 下午6:09
 * 描述:
 */
public class EnvironmentUtil {
    private EnvironmentUtil() {
    }

    /**
     * 是否构建的是debug
     *
     * @return
     */
    public static boolean isBuildDebug() {
        return BuildConfig.BUILD_TYPE.equalsIgnoreCase("debug");
    }
}

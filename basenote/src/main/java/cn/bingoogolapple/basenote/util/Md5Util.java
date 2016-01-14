package cn.bingoogolapple.basenote.util;

import android.text.TextUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/15 上午1:36
 * 描述:
 */
public class Md5Util {
    private Md5Util() {
    }

    public static String md5(String... strs) {
        if (strs == null || strs.length == 0) {
            throw new RuntimeException("请输入需要加密的字符串!");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            boolean isNeedThrowNotNullException = true;
            for (String str : strs) {
                if (!TextUtils.isEmpty(str)) {
                    isNeedThrowNotNullException = false;
                    md.update(str.getBytes());
                }
            }
            if (isNeedThrowNotNullException) {
                throw new RuntimeException("请输入需要加密的字符串!");
            }
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
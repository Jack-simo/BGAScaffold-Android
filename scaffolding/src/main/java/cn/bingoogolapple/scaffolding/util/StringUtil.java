package cn.bingoogolapple.scaffolding.util;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/11/4 上午12:11
 * 描述:
 */
public class StringUtil {
    private StringUtil() {
    }

    /**
     * 字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

    /**
     * 字符串是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !TextUtils.isEmpty(str);
    }

    /**
     * EditText 是否为空
     *
     * @param editText
     * @return
     */
    public static boolean isEmpty(EditText editText) {
        return isEmpty(editText.getText().toString().trim());
    }

    /**
     * 比较两个字符串是否相等
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isEqual(CharSequence a, CharSequence b) {
        return TextUtils.equals(a, b);
    }

    /**
     * 比较两个字符串是否不相等
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isNotEqual(CharSequence a, CharSequence b) {
        return !TextUtils.equals(a, b);
    }
}
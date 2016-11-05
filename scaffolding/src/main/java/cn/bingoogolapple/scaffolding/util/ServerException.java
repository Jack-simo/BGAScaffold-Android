package cn.bingoogolapple.scaffolding.util;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/8/14 上午12:45
 * 描述:
 */
public class ServerException extends Exception {
    private int mCode;

    public ServerException(String msg, int code) {
        super(msg);
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
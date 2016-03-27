package cn.bingoogolapple.basenote.util;

import rx.functions.Func1;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/3/27 下午11:36
 * 描述:
 */
public class NetResultFunc<T> implements Func1<NetResult<T>, T> {
    @Override
    public T call(NetResult<T> netResult) {
        if (netResult.code == 0) {
            return netResult.content;
        }
        throw  new RuntimeException(netResult.msg);
    }
}
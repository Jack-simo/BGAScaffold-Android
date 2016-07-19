package cn.bingoogolapple.basenote.component;

import javax.inject.Inject;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/7/19 下午6:02
 * 描述:
 */
public class Poetry {
    private String mPoem;

    // 用Inject标记构造函数,表示用它来注入到目标对象中去
    @Inject
    public Poetry(String poem) {
        mPoem = poem;
    }

    public String getPoem() {
        return mPoem;
    }
}
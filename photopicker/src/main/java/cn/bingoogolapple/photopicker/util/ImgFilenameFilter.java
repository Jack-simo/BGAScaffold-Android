package cn.bingoogolapple.photopicker.util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/2/15 上午12:58
 * 描述:
 */
public class ImgFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String filename) {
        return filename.endsWith(".jpg") || filename.endsWith(".jpeg") || filename.endsWith(".png");
    }
}
package cn.bingoogolapple.photopicker.model;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/2/14 上午1:41
 * 描述:
 */
public class FolderModel {
    private String dirPath;
    private String firstImgPath;
    private String name;
    private int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
        int lastIndexOf = dirPath.lastIndexOf("/");
        this.name = dirPath.substring(lastIndexOf + 1);
    }

    public String getFirstImgPath() {
        return firstImgPath;
    }

    public void setFirstImgPath(String firstImgPath) {
        this.firstImgPath = firstImgPath;
    }

    public String getName() {
        return name;
    }
}

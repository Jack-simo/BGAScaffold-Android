package cn.bingoogolapple.recorder.model;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:16/1/25 上午12:01
 * 描述:
 */
public class Recorder {
    public float time;
    public String filePath;

    public Recorder() {
    }

    public Recorder(float time, String filePath) {
        this.time = time;
        this.filePath = filePath;
    }
}
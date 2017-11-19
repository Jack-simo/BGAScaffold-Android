package cn.bingoogolapple.scaffolding.demo.rxjava.entity;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/11/19
 * 描述:博客实体
 */
public class Blog {
    private Long id;
    private String title;
    private String content;
    private Long categoryId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

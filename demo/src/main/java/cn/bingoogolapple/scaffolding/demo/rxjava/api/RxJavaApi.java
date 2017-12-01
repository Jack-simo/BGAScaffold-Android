package cn.bingoogolapple.scaffolding.demo.rxjava.api;

import java.util.List;

import cn.bingoogolapple.scaffolding.demo.rxjava.entity.Blog;
import cn.bingoogolapple.scaffolding.demo.rxjava.entity.Category;
import cn.bingoogolapple.scaffolding.demo.rxjava.entity.UploadToken;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:2017/11/20
 * 描述:
 */
public interface RxJavaApi {
    /**
     * 添加博客
     *
     * @param blog
     * @return
     */
    @POST("api/blogs")
    Observable<Blog> addBlog(@Body Blog blog);

    /**
     * 查询博客列表
     *
     * @param keyword
     * @return
     */
    @GET("api/blogs")
    Observable<List<Blog>> findBlogList(@Query("keyword") String keyword);

    /**
     * 查询分类列表
     *
     * @return
     */
    @GET("api/categorys")
    Observable<List<Category>> getCategoryList();

    /**
     * 获取文件上传 token
     *
     * @return
     */
    @GET("api/file/token")
    Observable<UploadToken> getUploadToken();

    /**
     * 上传文件
     *
     * @param requestBody
     * @return
     */
    @POST("api/file/upload")
    Observable<String> upload(@Body RequestBody requestBody);

    /**
     * 测试返回的 data 字段为字符串
     *
     * @return
     */
    @GET("api/test/stringData")
    Observable<String> stringData();

    /**
     * 测试返回的数据为字符串
     *
     * @return
     */
    @GET("api/test/string")
    Observable<String> string();
}

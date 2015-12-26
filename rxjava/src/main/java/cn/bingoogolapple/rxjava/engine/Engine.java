package cn.bingoogolapple.rxjava.engine;

import java.util.List;

import cn.bingoogolapple.rxjava.model.RefreshModel;
import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/25 下午5:27
 * 描述:
 */
public interface Engine {

    @GET("refreshlayout/api/moredata{pageNumber}.json")
    Call<List<RefreshModel>> loadMoreData(@Path("pageNumber") int pageNumber);
}
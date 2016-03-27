package cn.bingoogolapple.rxjava.engine;

import java.util.List;

import cn.bingoogolapple.basenote.util.NetResult;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/25 下午5:27
 * 描述:
 */
public interface RemoteServerEngine {

    @GET("refreshlayout/api/moredata{pageNumber}.json")
    Call<List<RefreshModel>> loadMoreData(@Path("pageNumber") int pageNumber);


    @GET("refreshlayout/api/defaultdata.json")
    Observable<List<RefreshModel>> loadInitDatasRx();

    @GET("refreshlayout/api/moredata{pageNumber}.json")
    Observable<List<RefreshModel>> loadMoreDataRx(@Path("pageNumber") int pageNumber);

    @GET("medianote/oppo.mp4")
    Observable<ResponseBody> download();


    Observable<NetResult<List<RefreshModel>>> testNetResult1();
}
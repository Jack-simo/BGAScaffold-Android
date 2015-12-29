package cn.bingoogolapple.rxjava.engine;

import cn.bingoogolapple.rxjava.model.JsonResp;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Query;
import rx.Observable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/25 下午5:27
 * 描述:
 */
public interface LocalServerEngine {

    @GET("login")
    Observable<JsonResp> loginGet(@Query("username") String username, @Query("password") String password);

    @FormUrlEncoded
    @POST("login")
    Observable<JsonResp> loginPost(@Field("username") String username, @Field("password") String password);



    @Headers("headerParam1: headerParam1Value")
    @GET("header1")
    Observable<JsonResp> staticHeaders1();

    @Headers({
            "headerParam1: headerParam1Value",
            "headerParam2: headerParam2Value"
    })
    @GET("header2")
    Observable<JsonResp> staticHeaders2();

    @GET("header1")
    Observable<JsonResp> dynamicHeader1(@Header("headerParam1") String headerParam1);

    @GET("header2")
    Observable<JsonResp> dynamicHeader2(@Header("headerParam1") String headerParam1, @Header("headerParam2") String headerParam2);


}
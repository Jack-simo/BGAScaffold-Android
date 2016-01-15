package cn.bingoogolapple.rxjava.engine;


import java.util.List;
import java.util.Map;

import cn.bingoogolapple.rxjava.model.JsonResp;
import cn.bingoogolapple.rxjava.model.Person;
import cn.bingoogolapple.rxjava.model.RefreshModel;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:15/12/25 下午5:27
 * 描述:
 */
public interface LocalServerEngine {

    @GET("login")
    Observable<JsonResp> loginGet(@Query("username") String username, @Query("password") String password);

    @GET("login")
    Observable<JsonResp> loginGetMap(@QueryMap Map<String, Object> apiParams);

    // 注意：@Field parameters can only be used with form encoding
    @FormUrlEncoded
    @POST("login")
    Observable<JsonResp> loginPost(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("login")
    Observable<JsonResp> loginPostMap(@FieldMap Map<String, Object> apiParams);


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

    @POST("createPerson")
    Observable<JsonResp> createPerson(@Body Person person);

    @Multipart
    @POST("updateAvatar")
    Observable<JsonResp> updateAvatarByPart(@Part("avatar\"; filename=\"image.png\" ") RequestBody avatar, @Part("desc") String desc);

    @Multipart
    @POST("updateAvatar")
    Observable<JsonResp> updateAvatarByPart(@Part("avatar\"; filename=\"image.png\" ") RequestBody avatar, @Part("desc") RequestBody desc);

    @POST("updateAvatar")
    Observable<JsonResp> updateAvatarByBody(@Body RequestBody avatar);

    // @Path parameters may not be used with @Url
    @GET
    Observable<List<RefreshModel>> dynamicUrl(@Url String url);
}
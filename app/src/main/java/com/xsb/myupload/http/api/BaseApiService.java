package com.xsb.myupload.http.api;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface BaseApiService {
    /*post map提交参数*/
    @POST
    @FormUrlEncoded
    Observable<ResponseBody> executePost(
            @Url  String url,
            @FieldMap Map<String,Object> map);

   /*@Body 以 Post方式 传递 自定义数据类型 给服务器 如果提交的是一个Map，那么作用相当于 @Field */
   @POST
   Observable<ResponseBody> executePostBody(
           @Url String url,
           @Body Object object);

   /*get请求的 map写法*/
    Observable<ResponseBody> executeGet(
            @Url String url,
            @QueryMap Map<String,Object> map);

    @DELETE()
    <T> Observable<ResponseBody> executeDelete(
            @Url String url,
            @QueryMap Map<String, Object> maps);

    @PUT()
    <T> Observable<ResponseBody> executePut(
            @Url String url,
            @FieldMap Map<String, Object> maps);

    @Multipart
    @POST()
    Observable<ResponseBody> upLoadImage(
            @Url() String url,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);

   /*单文件上传*/
    @Multipart
    @POST
    Observable<ResponseBody> uploadFlie(
            @Url String url,
            @Part("description") RequestBody description,
            @Part("image\"; filename=\"image.jpg") MultipartBody.Part file);

    /*多文件上传*/
    @POST
    Observable<ResponseBody> uploadFiles(
            @Url String url,
            @Body Map<String,RequestBody> maps
    );

    /*单文件上传*/
    @Multipart
    @POST
    Observable<ResponseBody> uploadFlieWithPart(
            @Url String url,
            @Part MultipartBody.Part file);

    /*多文件上传*/
    @Multipart
    @POST
    Observable<ResponseBody> uploadFlieWithPartList(
            @Url String url,
            @Part List<MultipartBody.Part> list);

    /*多文件上传*/
    @Multipart
    @POST
    Observable<ResponseBody> uploadFlieWithPartMap(
            @Url String url,
            @Part  Map<String,MultipartBody.Part> maps);

    @POST
    Observable<ResponseBody> uploadFile(
            @Url String url,
            @Body RequestBody file);

    @Multipart
    @POST
    Observable<ResponseBody> uploadFileWithPartMap(
            @Url() String url,
            @PartMap() Map<String, RequestBody> partMap,
            @Part() MultipartBody.Part file);

    /*下载文件*/
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String fileUrl);

   /*下载小文件*/
    @GET
    Observable<ResponseBody> downloadSmallFile(@Url String fileUrl);


    @FormUrlEncoded
    @POST()
    <T> Observable<ResponseBody> postForm(
            @Url() String url,
            @FieldMap Map<String, Object> maps);

    @POST()
    Observable<ResponseBody> postRequestBody(
            @Url() String url,
            @Body RequestBody Body);

}

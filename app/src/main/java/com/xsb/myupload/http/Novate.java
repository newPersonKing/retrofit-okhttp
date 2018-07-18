package com.xsb.myupload.http;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xsb.myupload.http.api.BaseApiService;
import com.xsb.myupload.http.callback.ResponseCallBack;
import com.xsb.myupload.http.callback.rxcallback.ResponseCallback;
import com.xsb.myupload.http.callback.rxsubscriber.RxSubscriber;
import com.xsb.myupload.http.cookie.CookieCacheImpl;
import com.xsb.myupload.http.cookie.NovateCookieManager;
import com.xsb.myupload.http.cookie.SharedPrefsCookiePersistor;
import com.xsb.myupload.http.exception.NovateException;
import com.xsb.myupload.http.interceptor.AddCookiesInterceptor;
import com.xsb.myupload.http.request.NovateRequestBody;
import com.xsb.myupload.http.response.NovateResponse;
import com.xsb.myupload.http.subscriber.NovateSubscriber;
import com.xsb.myupload.untils.ContentType;
import com.xsb.myupload.untils.FileUtil;
import com.xsb.myupload.untils.Utils;

import java.io.File;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class Novate {

    private static final int DEFAULT_MAXIDLE_CONNECTIONS = 5;
    private static final long DEFAULT_KEEP_ALIVEDURATION = 8;
    private static final long DEFAULT_CACHEMAXSIZE = 10 * 1024 * 1024;
    private static int DEFAULT_MAX_STALE = 60 * 60 * 24 * 3;

    private static  OkHttpClient.Builder okhttpBuilder;
    private static Retrofit.Builder retrofitBuilder;
    private static OkHttpClient okHttpClient;
    private static Retrofit retrofit;
    public static BaseApiService apiManager;

    private static final int DEFAULT_TIMEOUT = 15;

    private static Context mContext;

    public static final String KEY_CACHE = "Novate_Http_cache";

    public void setOkhttpAndRetrofit(){

    }

    public static final class Builder {

        private Context context;
        private int connectTimeout = DEFAULT_TIMEOUT;
        private int writeTimeout = DEFAULT_TIMEOUT;
        private int readTimeout = DEFAULT_TIMEOUT;

        private String baseUrl;

        private Boolean isCookie=true;
        private Boolean isCache=false;
        private Boolean isLog=true;
        private Boolean isSkip = false;

        private Converter.Factory converterFactory;
        private CallAdapter.Factory callAdapterFactory;

        private File httpCacheDirectory;
        private Cache cache = null;

        private int default_maxidle_connections = DEFAULT_MAXIDLE_CONNECTIONS;
        private long default_keep_aliveduration = DEFAULT_KEEP_ALIVEDURATION;
        private long cacheMaxSize = DEFAULT_CACHEMAXSIZE;
        private int cacheTimeout = DEFAULT_MAX_STALE;

        private Object tag;

        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR;
        private Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE;

        private ConnectionPool connectionPool;

        private Proxy proxy;

        private NovateCookieManager cookieManager;

        private okhttp3.Call.Factory callFactory;

        public Builder(Context context){
            okhttpBuilder=new OkHttpClient.Builder();
            retrofitBuilder=new Retrofit.Builder();
            this.context=context;
        }

        public Builder connectTimeout(int timeout) {
            if (timeout>0){
                this.connectTimeout=timeout;
            }
            return this;
        }

        public Builder writeTimeout(int timeout) {
            if (timeout>0){
                this.writeTimeout=timeout;
            }
            return this;
        }

        public Builder readTimeout(int timeout) {
            if (timeout>0){
                this.readTimeout=timeout;
            }
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            if (baseUrl==null) throw new NullPointerException("baseUrl==null");
            this.baseUrl=baseUrl;
            return this;
        }

        public <T> Builder addParameters(Map<String, T> parameters) {

            if (parameters!=null){
                okhttpBuilder.addInterceptor(new BaseParameters<>(Utils.checkNotNull(parameters,"parameters==null")));
            }
            return this;
        }

        public <T> Builder addHeader(Map<String, T> headers) {
            if(null != headers) {
                okhttpBuilder.addInterceptor(new BaseInterceptor(Utils.checkNotNull(headers, "header == null")));
            }
            return this;
        }

        public Builder addCookie(boolean isCookie) {
            this.isCookie = isCookie;
            return this;
        }

        public Builder addCache(boolean isCache) {
            this.isCache = isCache;
            return this;
        }

        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        /**
         * setCache
         *
         * @param cache cahe
         * @return Builder
         */
        public Builder addCache(Cache cache) {
            return addCache(cache, cacheTimeout);
        }

        public Builder addCache(Cache cache, final int cacheTimeOut) {
            addCache(cache, String.format("max-age=%d", cacheTimeOut));
            return this;
        }

        private Builder addCache(Cache cache, final String cacheControlValue) {
            REWRITE_CACHE_CONTROL_INTERCEPTOR = new CacheInterceptor(mContext, cacheControlValue);
            REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE = new CacheInterceptorOffline(mContext, cacheControlValue);
            okhttpBuilder.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
            okhttpBuilder.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            okhttpBuilder.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR_OFFLINE);
            this.cache=cache;
            return this;
        }


        public Novate build() {
            if(baseUrl==null){
                throw new IllegalStateException("Base URL required.");
            }

            if (okhttpBuilder==null){
                throw new IllegalStateException("okhttpBuilder required.");
            }

            if (retrofitBuilder==null){
                throw new IllegalStateException("retrofitBuilder required.");
            }

            mContext=context;

            retrofitBuilder.baseUrl(baseUrl);

            if (converterFactory==null){
                converterFactory= GsonConverterFactory.create();
            }

            retrofitBuilder.addConverterFactory(converterFactory);

            if (callAdapterFactory==null){
                callAdapterFactory= RxJavaCallAdapterFactory.create();
            }

            retrofitBuilder.addCallAdapterFactory(callAdapterFactory);

            /*实现自己需求的拦截器*/
            if (tag != null) {
                okhttpBuilder.addInterceptor(new RequestInterceptor<>(tag));
            }

            if (isLog){
                okhttpBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS));
                okhttpBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
            }

            if (isSkip){
                /*这个是信任所有的证书 设置  信任自签名证书 请自行设定*/
                okhttpBuilder.sslSocketFactory(NovateHttpsFactroy.getSSLSocketFactory(),NovateHttpsFactroy.creatX509TrustManager());

                okhttpBuilder.hostnameVerifier(NovateHttpsFactroy.creatSkipHostnameVerifier());
            }

            if (httpCacheDirectory == null) {
                httpCacheDirectory = new File(mContext.getCacheDir(), KEY_CACHE);
            }

            if (isCache) {
                try {
                    if (cache == null) {
                        cache = new Cache(httpCacheDirectory, cacheMaxSize);
                    }
                    addCache(cache);

                } catch (Exception e) {
                    Log.e("OKHttp", "Could not create http cache", e);
                }
                if (cache == null) {
                    cache = new Cache(httpCacheDirectory, cacheMaxSize);
                }
            }

            if (cache != null) {
                okhttpBuilder.cache(cache);
            }

            if (connectionPool == null) {
                connectionPool = new ConnectionPool(default_maxidle_connections, default_keep_aliveduration, TimeUnit.SECONDS);
            }
            okhttpBuilder.connectionPool(connectionPool);
            /*设置代理 没明白什么是代理*/
            if (proxy != null) {
                okhttpBuilder.proxy(proxy);
            }
            /*设置cookie自动管理类*/
            if (isCookie && cookieManager == null) {
                okhttpBuilder.cookieJar(new NovateCookieManager(new CookieCacheImpl(), new SharedPrefsCookiePersistor(context)));
            }

            if (isCookie){
                okhttpBuilder.addInterceptor(new AddCookiesInterceptor(context, ""));
            }

            if (cookieManager != null) {
                okhttpBuilder.cookieJar(cookieManager);
            }
            /*todo 设置callFactory 不知道是什么意思*/
            if (callFactory != null) {
                retrofitBuilder.callFactory(callFactory);
            }
            okHttpClient = okhttpBuilder.build();

            retrofitBuilder.client(okHttpClient);

            /**
             * create Retrofit
             */
            retrofit = retrofitBuilder.build();

            apiManager = retrofit.create(BaseApiService.class);
            return null;
        }
    }


    /*各种最终的网络请求*/
    public <T> T executeGet(String url, Map<String,Object> maps, ResponseCallBack<T> callBack){
        return (T) apiManager.executeGet(url,maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new NovateSubscriber<T>(mContext,callBack));
    }




    /*工作线程 主线程之间的切换*/
    final Observable.Transformer schedulersTransformer=new Observable.Transformer() {
        @Override
        public Object call(Object observable) {

            return ((Observable)observable).subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /*post 自定义参数*/
    public <T> T rxBody(Object tag, String url, Object bean, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executePostBody(url, bean)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /*get map 请求*/
    public <T> T rxGet(final String tag, final String url, @NonNull final Map<String, Object> maps, final ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.executeGet(url, maps)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /*post 单张图片*/
    public <T> T uploadImage(String url, File file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.upLoadImage(url, Utils.createImage(file))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /*传递单个文件*/
    public <T> T uploadFlie(String url, RequestBody description, MultipartBody.Part file, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.uploadFlie(url, description, file)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /**/
    public <T> T upload(String url, RequestBody requestBody, Subscriber<ResponseBody> subscriber) {
        return (T) apiManager.postRequestBody(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(subscriber);
    }

    /*多文件上传body map*/
    public <T> T rxUploadWithBodyMapByFile(Object tag, String url, ContentType type, Map<String, File> maps, ResponseCallback<T, ResponseBody> callBack) {
        Map<String, RequestBody> bodys = new HashMap<>();


        if (maps != null && maps.size() > 0) {
            Iterator<String> keys = maps.keySet().iterator();
            NovateRequestBody requestBody = null;
            while (keys.hasNext()) {
                String i = keys.next();
                File file = maps.get(i);
                if (FileUtil.exists(file)) {
                    throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
                } else {
                    requestBody = Utils.createRequestBody(file, type, callBack);
                    bodys.put(i, requestBody);
                }
            }
        }

        return (T) apiManager.uploadFiles(url, bodys)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /*多文件上传part map*/
    public <T> T rxUploadWithPartMapByFile(Object tag, String url, ContentType type, Map<String, File> maps, ResponseCallback<T, ResponseBody> callBack) {


        return (T) apiManager.uploadFlieWithPartMap(url, Utils.createParts("image", maps, type, callBack))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    /*多文件上传*/
    public <T> T rxUploadWithPartListByFileCustom(String url, List<File> list, ResponseCallback<T, ResponseBody> callBack) {
        return rxUploadWithPartListByFile(url, url, ContentType.IMAGE, list, callBack);
    }

     /*文件上传 单part*/
    public <T> T rxUploadWithPart(Object tag, String url, MultipartBody.Part requestBody, ResponseCallback<T, ResponseBody> callBack) {
        return (T) apiManager.uploadFlieWithPart(url, requestBody)
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }

    public <T> T rxUploadWithPartListByFile(Object tag, String url, ContentType type, List<File> list, ResponseCallback<T, ResponseBody> callBack) {


        return (T) apiManager.uploadFlieWithPartList(url, Utils.createPartLists("file", list, type, callBack))
                .compose(schedulersTransformer)
                .compose(handleErrTransformer())
                .subscribe(new RxSubscriber<T, ResponseBody>(tag, callBack).addContext(mContext));
    }



    /*Rxjava 的抛异常的方法*/
    private Observable.Transformer exceptTransformer = null;
    public <T>  Observable.Transformer<NovateResponse<T>,T> handleErrTransformer(){

        if (exceptTransformer!=null){
            return exceptTransformer;
        }else {
            return exceptTransformer=new Observable.Transformer() {
                @Override
                public Object call(Object observable) {
                    return ((Observable)observable).onErrorResumeNext(new Func1<Throwable, Observable>() {
                        @Override
                        public Observable call(Throwable throwable) {
                            return Observable.error(NovateException.handleException(throwable));
                        }
                    });
                }
            };
        }
    }

}

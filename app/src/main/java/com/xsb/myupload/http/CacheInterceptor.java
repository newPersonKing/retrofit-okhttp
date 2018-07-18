package com.xsb.myupload.http;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/*添加有网的缓存时间设置*/
public class CacheInterceptor implements Interceptor {

    protected Context context;
    protected String cacheControlValue_Offline;
    protected String cacheControlValue_Online;

    //set cahe times is 3 days
    protected static final int maxStale = 60 * 60 * 24 * 3;
    // read from cache for 60 s
    protected static final int maxStaleOnline = 60;

    public CacheInterceptor(Context context) {
        this(context, String.format("max-age=%d", maxStaleOnline));
    }

    public CacheInterceptor(Context context, String cacheControlValue) {
        this(context, cacheControlValue, String.format("max-age=%d", maxStale));
    }

    public CacheInterceptor(Context context, String cacheControlValueOffline, String cacheControlValueOnline) {
        this.context = context;
        this.cacheControlValue_Offline = cacheControlValueOffline;
        this.cacheControlValue_Online = cacheControlValueOnline;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        okhttp3.Response originalResponse = chain.proceed(chain.request());
        String cacheControl = originalResponse.header("Cache-Control");
        //String cacheControl = request.cacheControl().toString();
        if (TextUtils.isEmpty(cacheControl) || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                cacheControl.contains("must-revalidate") || cacheControl.contains("max-age") || cacheControl.contains("max-stale")) {
            return originalResponse.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxStale)
                    .build();

        } else {
            return originalResponse;
        }
    }
}

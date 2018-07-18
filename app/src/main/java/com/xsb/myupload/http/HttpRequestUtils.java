package com.xsb.myupload.http;

import android.content.Context;

import com.xsb.myupload.untils.ObjectUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class HttpRequestUtils {

    private static HttpRequestUtils instance;

    private static Context mContext;

   private static final String HOST_URL = "http://124.65.123.170:8088";

    public static synchronized HttpRequestUtils getInstance(Context context) {
        if (ObjectUtils.isNull(instance)) {
            instance = new HttpRequestUtils();
            mContext=context;
        }
        return instance;
    }


}

package com.xsb.myupload.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/*这里可以根据自己的tag去决定自己要做什么*/
public class RequestInterceptor<T> implements Interceptor {

    private Object tag;

//    private NovateRequest request;

    public RequestInterceptor(Object tag) {
        this.tag = tag;
    }

//    public RequestInterceptor(NovateRequest request) {
//        this.request = request;
//    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request.Builder builder = chain.request()
                .newBuilder().tag(tag);
        //todo dev more funchion()
        return chain.proceed(builder.build());
    }
}

package com.xsb.myupload.http;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
/*动态添加参数*/
public class BaseParameters<T> implements Interceptor {

    private Map<String, T> parameters;

    public BaseParameters(Map<String, T> headers) {
        this.parameters = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original=chain.request();
        HttpUrl httpUrl=original.url();

        HttpUrl.Builder builder=httpUrl.newBuilder();
        if (parameters != null && parameters.size() > 0) {
             Set<String> keys=parameters.keySet();
            for (String headerKey : keys) {
                builder.addQueryParameter(headerKey,parameters.get(headerKey)==null?"":(String)parameters.get(headerKey)).build();
            }
        }
        HttpUrl url=builder.build();
        Request.Builder requestBuilder=original.newBuilder().url(url);
        return chain.proceed(requestBuilder.build());
    }
}

package com.xsb.myupload.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BaseInterceptor<T> implements Interceptor {
    private Map<String, T> headers;

    public BaseInterceptor(Map<String, T> headers) {
        this.headers = headers;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request()
                .newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String>keys=headers.keySet();
            for (String headerKey : keys) {
                builder.addHeader(headerKey,headers.get(headerKey)==null?"":getValueEncoded((String) headers.get(headerKey))).build();
            }
        }
        return null;
    }
    private  String getValueEncoded(String value) throws UnsupportedEncodingException {
        if (value == null) return "null";
        String newValue = value.replace("\n", "");
        for (int i = 0, length = newValue.length(); i < length; i++) {
            char c = newValue.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                return URLEncoder.encode(newValue, "UTF-8");
            }
        }
        return newValue;
    }
}

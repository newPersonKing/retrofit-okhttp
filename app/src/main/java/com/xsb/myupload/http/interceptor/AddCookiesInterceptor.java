package com.xsb.myupload.http.interceptor;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.functions.Action1;

public class AddCookiesInterceptor implements Interceptor {

    private Context context;
    private String lang;

    public AddCookiesInterceptor(Context context, String language) {
        super();
        this.context = context;
        this.lang = language;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        final Request.Builder newBuilder=chain.request().newBuilder();

        SharedPreferences sharedPreferences = context.getSharedPreferences("cookie", Context.MODE_PRIVATE);

        Observable.just(sharedPreferences.getString("cookie",""))
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String cookie) {
                       if (cookie.contains("lang=ch")){
                           cookie=cookie.replace("lang=ch","lang="+lang);
                       }
                       if (cookie.contains("lang=en")){
                           cookie=cookie.replace("lang=en","lang="+lang);
                       }

                       newBuilder.addHeader("Cookie",cookie);
                    }
                });
        return chain.proceed(newBuilder.build());
    }
}

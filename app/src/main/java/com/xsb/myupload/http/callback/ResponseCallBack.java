package com.xsb.myupload.http.callback;

import com.xsb.myupload.http.exception.Throwable;

import org.json.JSONException;

public  interface ResponseCallBack<T> {

    void onStart();

    void onCompleted();

    void onError(Throwable e);

    void onSuccess(int code, String msg, T response, String originalResponse) throws JSONException;

}

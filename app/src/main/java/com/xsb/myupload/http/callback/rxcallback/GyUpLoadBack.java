package com.xsb.myupload.http.callback.rxcallback;

import okhttp3.ResponseBody;

public abstract class GyUpLoadBack<T> extends ResponseCallback<T, ResponseBody>{


    @Override
    public T onHandleResponse(ResponseBody response) {

        return null;
    }
}

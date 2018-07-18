package com.xsb.myupload.http.callback.rxsubscriber;

import android.content.Context;

import com.xsb.myupload.http.callback.rxcallback.ResponseCallback;
import com.xsb.myupload.http.exception.NovateException;

import okhttp3.ResponseBody;
import rx.Subscriber;

public class RxSubscriber<T,E> extends Subscriber<ResponseBody> {

    private ResponseCallback<T, E> callBack;
    private Object tag = null;
    private Context context;

    public RxSubscriber(Object tag, ResponseCallback<T, E> callBack) {
        super();

        this.callBack = callBack;
        this.callBack.setTag(tag);
        this.tag = tag;
    }

    public Context context() {
        return context;
    }

    public RxSubscriber addContext(Context context) {
        this.context = context;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callBack!=null){
            callBack.onStart(tag);
        }
    }

    @Override
    public void onCompleted() {
          if (callBack!=null){
              callBack.onCompleted(tag);
              callBack.onRelease();
          }
    }

    @Override
    public void onError(Throwable e) {
           if (callBack!=null){
               callBack.onError(tag, NovateException.handleException(e));
               callBack.onRelease();
           }
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            if (callBack!=null){
                callBack.onNext(tag,null,callBack.onHandleResponse(responseBody));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (callBack != null) {
                callBack.onError(tag, NovateException.handleException(e));
            }
        }
    }
}

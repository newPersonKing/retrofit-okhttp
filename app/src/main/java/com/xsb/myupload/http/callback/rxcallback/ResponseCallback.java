package com.xsb.myupload.http.callback.rxcallback;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xsb.myupload.http.exception.NovateException;
import com.xsb.myupload.http.exception.Throwable;
import com.xsb.myupload.untils.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import okhttp3.ResponseBody;


public abstract class ResponseCallback<T,E> implements Callback,IGenericsConvert<E> {

    protected Object tag;
    protected Handler handler;
    protected String TAG="novateCallback";

    private Context context;

    private Handler getHandler(){
        return handler==null?handler=new Handler(context.getMainLooper()):handler;
    }

    public void setHandler(Handler handler){
        this.handler=handler;
    }

    public ResponseCallback(Object tag){
        this.tag=tag;
    }

    public Object getTag(){
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }


    public ResponseCallback() {
        if (handler == null) handler = new Handler(Looper.getMainLooper());
    }

    /**
     * UI Thread
     *
     */
    public void onStart(Object tag) {
    }

    /**
     * UI Thread
     *
     * @param
     */
    public void onCompleted(Object tag) {
    }

    /**
     * UI Thread
     *
     * @param progress
     * todo 进度条回掉可以选择性的实现
     */
    public void onProgress(Object tag, float progress, long transfered, long total) {
    }

    public void onProgress(Object tag, int progress, long speed, long transfered, long total) {
    }

    public boolean isReponseOk(Object tag, ResponseBody responseBody) {
        return true;
    }
    /*onext之前执行*/
    public abstract T onHandleResponse(ResponseBody response) throws Exception;

    @Override
    public <T> T transform(E response, Class<T> classOfT)  throws Exception {
        return (T) response;
    }

    public abstract void onError(Object tag, Throwable e);

    public abstract void onCancel(Object tag, Throwable e);

    /*调用顺序 第一 onnext*/
    public abstract void onNext(Object tag, Call call, T response);

    /**
     * @param e
     */
    protected void finalOnError(final Exception e) {

        if (Utils.checkMain()) {
            onError(tag, NovateException.handleException(e));
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onError(tag, NovateException.handleException(e));
                }
            });
        }
    }

    @Override
    public void  onFailure(final Call call, final IOException e) {
        if (Utils.checkMain()) {
            onError(tag, NovateException.handleException(e));
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onError(call.request().tag(), NovateException.handleException(e));
                }
            });
        }


    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        if (call.isCanceled()) {
            onCancel(call.request().tag(), new Throwable(null, -200, "已取消"));
        }
        tag = call.request().tag();
        if(isReponseOk(tag, response.body())) {
            try {
                onHandleResponse(response.body());
            } catch (Exception e) {
                e.printStackTrace();
                onError(tag, NovateException.handleException(e));
            }
        }

    }

    /**
     * OnRelease 子类可以复写
     */
    public void onRelease() {

    }

}

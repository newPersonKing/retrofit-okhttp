package com.xsb.myupload.http.callback.rxcallback;

import com.google.gson.Gson;
import com.xsb.myupload.http.response.NovateResponse;
import com.xsb.myupload.untils.Utils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.ResponseBody;

public abstract class RxGenericsCallback<T,E> extends ResponseCallback<T,E> {

    protected T dataResponse = null;
    protected int code = -1;
    protected String msg = "";
    protected String dataStr = "";

    @Override
    public T onHandleResponse(ResponseBody response) throws IOException {
        //当前对象的直接超类的 Type
        Class<T> entityClass= (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (entityClass==String.class){
            return (T) new String(response.bytes());
        }
        String jstring = new String(response.bytes());
        return null;
    }

    /*根据实际对象进行返回*/
    public T transform(String response, final Class classOfT) throws ClassCastException {
        if (classOfT== NovateResponse.class){
            return (T) new Gson().fromJson(response, classOfT);
        }
        /*todo 根据自己的需求进行数据的处理*/
        return null;
    }

    @Override
    public void onNext(final Object tag, Call call, T response) {
        if (Utils.checkMain()) {
            onNext(tag, code, msg, dataResponse);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onNext(tag, code, msg, dataResponse);
                }
            });
        }
    }

    public abstract void onNext(Object tag, int code, String message, T response);
}

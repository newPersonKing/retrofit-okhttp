package com.xsb.myupload.http.callback.rxcallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Call;
import okhttp3.ResponseBody;

public abstract class RxBitmapCallback extends ResponseCallback<Bitmap, ResponseBody> {

    @Override
    public Bitmap onHandleResponse(ResponseBody response) {
        return BitmapFactory.decodeStream(response.byteStream());
    }

    @Override
    public void onNext(Object tag, Call call, Bitmap response) {
        onNext(tag,response);
    }

    public abstract void onNext(Object tag, Bitmap response);
}

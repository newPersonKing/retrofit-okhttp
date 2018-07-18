package com.xsb.myupload.http.callback.rxcallback;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;

public abstract class RxListCallback<T> extends ResponseCallback<T,ResponseBody> {
    private Type collectionType;
    private int code;
    private String msg;
    private String dataStr;
    private T dataResponse;
    @Override
    public T onHandleResponse(ResponseBody response) throws IOException {

        collectionType= ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        String jstring = new String(response.bytes());

        return transform(jstring,null);
    }

    /*todo 也是根据需求进行数据处理*/
    public T transform(String response, final Class classOfT) throws ClassCastException {
        JSONObject jsonObject = null;
        Log.e("xxx", response);
        try {
            jsonObject = new JSONObject(response);
            code = jsonObject.optInt("code");
            msg = jsonObject.optString("msg");
            if (TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("error");
            }

            if(TextUtils.isEmpty(msg)) {
                msg = jsonObject.optString("message");
            }

            dataStr = jsonObject.optJSONArray("data").toString();
            if (dataStr.isEmpty()) {
                dataStr = jsonObject.optJSONArray("result").toString();
            }

            dataResponse = new Gson().fromJson(dataStr, collectionType);

        } catch (Exception e) {
            e.printStackTrace();
            jsonObject = null;
        }

        return dataResponse;
    }
}

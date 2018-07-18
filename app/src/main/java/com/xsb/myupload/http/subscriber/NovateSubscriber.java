package com.xsb.myupload.http.subscriber;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.xsb.myupload.http.callback.ResponseCallBack;
import com.xsb.myupload.http.exception.NovateException;
import com.xsb.myupload.http.exception.ServerException;
import com.xsb.myupload.http.response.NovateResponse;
import com.xsb.myupload.untils.ReflectionUtil;


import org.json.JSONObject;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import rx.Subscriber;
/*处理状态码 处理成功与否的subscriber*/
public class NovateSubscriber<T> extends Subscriber<ResponseBody> {

    private Context context;
    private ResponseCallBack callBack;

    private Type finalNeedType;

    public NovateSubscriber(Context context, ResponseCallBack callBack){
        this.context=context;
        this.callBack=callBack;
    }

    @Override
    public void onStart() {
        super.onStart();
        /*目前使用返回的types的长度为1*/
        Type[] types= ReflectionUtil.getParameterizedTypeswithInterfaces(callBack);
        /*如果callback<T> 不传递T 这里不通过*/
        if (ReflectionUtil.methodHandler(types) == null || ReflectionUtil.methodHandler(types).size() == 0) {
            throw new NullPointerException("callBack<T> 中T不合法");
        }
        /*最后需要转换的type*/
        finalNeedType = ReflectionUtil.methodHandler(types).get(0);

        if (callBack!=null){
            callBack.onStart();
        }

    }

    @Override
    public void onCompleted() {

        if (callBack!=null){
            callBack.onCompleted();
        }

    }

    @Override
    public void onError(Throwable e) {
        if (callBack!=null){
            callBack.onError(NovateException.handleException(e));
        }
    }

    @Override
    public void onNext(ResponseBody responseBody) {

        try {
            byte[] bytes=responseBody.bytes();

            String jsStr = new String(bytes);
            if (callBack!=null){
               checkData(jsStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            callBack.onError(NovateException.handleException(e));
        }
    }
   /*根据不同的设定进行数据的处理*/
    private void checkData(String jStr){

        boolean success = false;
        String msg = "";
        int state;
        String dataStr = "";
        NovateResponse<T> baseResponse = null;
        T dataResponse = null;
        try {
            baseResponse=new NovateResponse<>();
            JSONObject jsonObject = new JSONObject(jStr.trim());

            success=jsonObject.getBoolean("success");
            state = jsonObject.optInt("state");
            msg = jsonObject.optString("msg");

            baseResponse.setSuccess(success);
            baseResponse.setMsg(msg);
            baseResponse.setState(state);
            if (success) {
                dataStr = jsonObject.optString("data");
                if (!TextUtils.isEmpty(dataStr) && !"null".equals(dataStr)) {
                    dataResponse = (T) new Gson().fromJson(dataStr, ReflectionUtil.newInstance(finalNeedType).getClass());
                    baseResponse.setData(dataResponse);
                }
                callBack.onSuccess(state,msg,dataResponse,jStr);
            }else {
               callBack.onError(NovateException.handleException(new ServerException(state,msg)));
            }

        } catch (Exception e) {
            e.printStackTrace();
            callBack.onError(NovateException.handleException(e));
        }
    }
}

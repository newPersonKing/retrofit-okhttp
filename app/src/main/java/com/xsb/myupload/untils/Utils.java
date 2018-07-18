package com.xsb.myupload.untils;

import android.content.res.Resources;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xsb.myupload.http.callback.rxcallback.ResponseCallback;
import com.xsb.myupload.http.request.NovateRequestBody;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;

public class Utils {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data;";
    public static final String MULTIPART_IMAGE_DATA = "image/*; charset=utf-8";
    public static final String MULTIPART_JSON_DATA = "application/json; charset=utf-8";
    public static final String MULTIPART_VIDEO_DATA = "video/*";
    public static final String MULTIPART_AUDIO_DATA = "audio/*";
    public static final String MULTIPART_TEXT_DATA = "text/plain";
    public static final String MULTIPART_APK_DATA = "application/vnd.android.package-archive";
    public static final String MULTIPART_JAVA_DATA = "java/*";
    public static final String MULTIPART_MESSAGE_DATA = "message/rfc822";

    public static <T> T checkNotNull(T object, String message) {
        if (object == null) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static boolean checkMain() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }



    /*图片 requestBody*/
    @NonNull
    public static RequestBody createImage(File file) {
        checkNotNull(file, "file not null!");
        return RequestBody.create(okhttp3.MediaType.parse(MULTIPART_IMAGE_DATA), file);
    }

    public static MultipartBody.Part createPart(String partName ,File file,ContentType type,ResponseCallback callback){

        NovateRequestBody requestBody = null;
        if (!FileUtil.exists(file)) {
            throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
        }else {
            requestBody=createRequestBody(file,type,callback);
            MultipartBody.Part body=MultipartBody.Part.createFormData(partName,file.getName(),requestBody);
            return body;
        }
    }

    @NonNull
    public static List<MultipartBody.Part> createPartLists(String partName , List<File> list, @NonNull ContentType type, ResponseCallback callback ) {
        List<MultipartBody.Part> parts=new ArrayList<>();
        if (list!=null&&list.size()>0){
            for (File file:list){
                NovateRequestBody requestBody = null;
                if (!FileUtil.exists(file)) {
                    throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
                }else {
                    requestBody=createRequestBody(file,type,callback);
                    MultipartBody.Part body=MultipartBody.Part.createFormData(partName,file.getName(),requestBody);
                    parts.add(body);
                }
            }
        }
        return parts;
    }

    /*create part map*/
    @NonNull
    public static Map<String, MultipartBody.Part> createParts(String partName , Map<String, File> maps, @NonNull ContentType type, ResponseCallback callback ) {
        // create RequestBody instance from file
        Map<String, MultipartBody.Part> parts = new HashMap<>();
        if (maps != null && maps.size() > 0) {
            Iterator<String> keys = maps.keySet().iterator();
            NovateRequestBody requestBody = null;
            while(keys.hasNext()){
                String i = keys.next();
                File file = maps.get(i);
                if (!FileUtil.exists(file)) {
                    throw new Resources.NotFoundException(file.getPath() + "file 路径无法找到");
                } else {
                    requestBody = createRequestBody(file, type, callback);
                    // MultipartBody.Part is used to send also the actual file name
                    MultipartBody.Part body =
                            MultipartBody.Part.createFormData(partName, file.getName(), requestBody);
                    parts.put(i, body);
                }
            }
        }
        return parts;
    }



    /*todo NovateRequestBody 可以替换成自己定制的requestbody*/
    public static NovateRequestBody createRequestBody(@NonNull File file, @NonNull ContentType type, ResponseCallback callback) {

        return new NovateRequestBody(createBody(file, type), callback);
    }

    @NonNull
    public static RequestBody createBody(File file, ContentType type) {
        if (TextUtils.isEmpty(typeToString(type))) {
            throw new NullPointerException("contentType not be null");
        }

        String mediaType=typeToString(type);
        return RequestBody.create(MediaType.parse(mediaType),file);

    }
    @NonNull
    public static String typeToString(@NonNull ContentType type) {
        switch (type) {
            case APK:
                return MULTIPART_APK_DATA;

            case VIDEO:
                return MULTIPART_VIDEO_DATA;

            case AUDIO:
                return MULTIPART_AUDIO_DATA;

            case JAVA:
                return MULTIPART_JAVA_DATA;

            case IMAGE:
                return MULTIPART_IMAGE_DATA;

            case TEXT:
                return MULTIPART_TEXT_DATA;

            case JSON:
                return MULTIPART_JSON_DATA;

            case FORM:
                return MULTIPART_FORM_DATA;
            case MESSAGE:
                return MULTIPART_MESSAGE_DATA;
            default:
                return MULTIPART_IMAGE_DATA;
        }
    }

}

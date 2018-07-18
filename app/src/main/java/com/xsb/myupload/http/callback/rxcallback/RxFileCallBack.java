package com.xsb.myupload.http.callback.rxcallback;

import android.os.Handler;
import android.os.Looper;

import com.xsb.myupload.untils.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.ResponseBody;

public abstract class RxFileCallBack extends ResponseCallback<File, ResponseBody> {

    /*** 文件夹路径*/
    private String destFileDir;
    /*** 文件名*/
    private String destFileName = FileUtil.DEFAULT_FILENAME;
    /*** FileOutputStream*/
    FileOutputStream fos = null;
    /*** FileOutputStream*/
    InputStream is = null;
    private long sum = 0;
    private int updateCount = 0;
    private int interval = 1;
    private int  progress = 0;

    public RxFileCallBack(String destFileName) {
        this("", destFileName);
    }

    public RxFileCallBack(String fileDir, String fileName) {
        super();
        this.destFileDir = fileDir;
        this.destFileName = fileName;
    }

    @Override
    public File onHandleResponse(ResponseBody response) throws Exception {
        return transform(response);
    }
    public File transform(ResponseBody response) throws Exception {
        return onNextFile(response);
    }

    public File onNextFile(ResponseBody response) throws Exception {
        byte[] buf=new byte[2048];
        int len=0;
        FileOutputStream fos=null;
        try {
            is=response.byteStream();
            final long total=response.contentLength();
            if (total<2048){
                interval= (int) 0.2;
            }else {
                interval=1;
            }

            File file=FileUtil.createDownloadFile(destFileDir,destFileName);

            fos=new FileOutputStream(file);
            while ((len=is.read(buf))!=-1){
                sum+=len;
                fos.write(buf,0,len);
                final long finalSum=sum;
                if (total==-1||total==0){
                    progress=100;
                }else {
                    progress= (int) (finalSum*100/total);
                }

                if (updateCount==0||progress>=updateCount){
                    updateCount+=interval;
                    handler=new Handler(Looper.getMainLooper());
                    final int finalProgress=progress;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(tag, finalProgress, finalSum, total);
                        }
                    });
                }
            }
            fos.flush();
            return file;
        }finally {
            onRelease();
        }
    }

    @Override
    public void onNext(Object tag, Call call, File response) {
        onNext(tag,response);
    }

    public abstract void onNext(Object tag, File file);
}

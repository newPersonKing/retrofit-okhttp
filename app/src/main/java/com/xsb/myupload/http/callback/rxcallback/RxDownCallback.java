package com.xsb.myupload.http.callback.rxcallback;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xsb.myupload.http.exception.Throwable;
import com.xsb.myupload.untils.FileUtil;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public abstract class RxDownCallback extends ResponseCallback<File, ResponseBody> {

    /*** 文件夹路径*/
    private String destFileDir;
    /*** 文件名*/
    private String destFileName;
    /*** Context */
    private Context context;
    /*** BufferedSink */
    protected BufferedSink sink;
    /** RANGE */
    private final String RANGE = "Range";
    /** TAG */
    private final String TAG = "DownLoadService";
    /**上次刷新UI时间*/
    protected long mLastRefreshTime;
    /**本次下载字节数*/
    protected long mBytesThistime;
    /**开始下载时间*/
    long mStarttime = 0;
    /**刷新UI时间间隔 */
    protected static final int REFRESH_INTEVAL = 1000;


    public RxDownCallback(String destFileDir,String destFileName){
        this.destFileDir=destFileDir;
        this.destFileName=destFileName;
    }


    /*onnext执行之前进行数据转换*/
    @Override
    public File onHandleResponse(ResponseBody response) throws EOFException {

        return onNextFile(response,true);
    }


    public File onNextFile(ResponseBody response, boolean isMax) throws EOFException {

        if (TextUtils.isEmpty(destFileDir)) {
            destFileDir = FileUtil.getBasePath(context);
        }

        try {
            long totalRead = 0;
            /*文件总大小*/
            final long fileSize = response.contentLength();
            BufferedSource source = response.source();

            final File file =  FileUtil.createDownloadFile(destFileDir, destFileName);
            sink= Okio.buffer(Okio.sink(file));

            long read=0;
            while ((read=source.read(sink.buffer(),2048))!=-1){
                totalRead+=read;
                mBytesThistime+=totalRead;
                long currenttime=System.currentTimeMillis();

                long speed=0;
                if (currenttime>mStarttime){
                    speed=totalRead*1000/(currenttime-mStarttime);
                }

                final int progress= (int) (read*100/fileSize);
                if (currenttime-mLastRefreshTime>=REFRESH_INTEVAL){
                    final long finalTotalRead=totalRead;
                    final long finalSpeed=speed;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onProgress(tag, progress, finalSpeed, finalTotalRead, fileSize);
                            mLastRefreshTime=System.currentTimeMillis();
                        }
                    });
                }
                if (totalRead > 0
                        && totalRead> fileSize * (1.5)) {
                    sink.writeAll(source);
                    sink.flush();
                    sink.close();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onError(tag, new Throwable(null, -100, "超过100%。总大小：" + fileSize + "，已下载：" + fileSize));
                        }
                    });
                    break;
                }
            }

            sink.writeAll(source);
            sink.flush();
            sink.close();

            if (totalRead==fileSize){
                onNext(tag,file);
            }else {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(tag, new Throwable(null, -100, "超过100%。总大小：" + fileSize + "，已下载：" + fileSize));
                    }
                });

                return null;
            }

        }catch (final Exception e){
            e.printStackTrace();
            if (e.getMessage().contains("No space left on device")) {
                // sd卡满
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(tag, new Throwable(e, -100, "SD卡满了"));
                    }
                });

            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onError(tag, new Throwable(e, -100, e.getMessage()));
                    }
                });

            }
            return null;
        }finally {
            close();
        }
        return null;
    }

    protected void close() {
        if (sink != null) {
            try {
                sink.close();
                sink = null;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "sink  is already closed!");
            }
        }
    }
    /*todo 最终实际能看到的返回方法是这个*/
    public abstract void onNext(Object tag, File file);
}

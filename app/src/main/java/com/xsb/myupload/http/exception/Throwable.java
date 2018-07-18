package com.xsb.myupload.http.exception;

/*自定义异常 存储message 与code*/
public class Throwable extends Exception {

    private int code;
    private String message;

    public Throwable(java.lang.Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public Throwable(java.lang.Throwable throwable, int code, String message) {
        super(throwable);
        this.code = code;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package com.xsb.myupload.http.response;

public class NovateResponse<T> {

    /**
     * 成功与否
     */
    private boolean success;
    /**
     * 消息
     */
    private String msg;
    /**
     * 数据
     */
    private T data;
    /**
     * 状态码
     */
    private int state;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}

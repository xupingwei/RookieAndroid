package me.pingwei.asynchttplibs;

import java.io.Serializable;

/**
 * Created by xupingwei on 2015/10/8.
 * 定义接口返回的协议
 */
public class BaseEntity implements Serializable {
    private int flag;
    private String msg;
    private String data;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

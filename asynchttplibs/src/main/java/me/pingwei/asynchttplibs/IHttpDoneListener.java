package me.pingwei.asynchttplibs;

/**
 * Created by xupingwei on 2015/8/20.
 * 请求成功后的回调
 */
public interface IHttpDoneListener {
    <W> void requestSuccess(W w, String subUrl);

    void requestFailed(int statusCode, String failedMessage, String subUrl);
}

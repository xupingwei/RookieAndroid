package me.pingwei.asynchttplibs;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.orhanobut.logger.Logger;

import org.apache.http.entity.ByteArrayEntity;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;


/**
 * Created by xupingwei on 2015/6/8.
 * 网络请求类
 */
public class RookieHttpUtils {

    private static final String TAG = "RookieHttpUtils";

    /**
     * BASE_URL如何设置
     */
    private static final String BASE_URL = RookieRequestConfig.BASE_URL;
    private static AsyncHttpClient httpClient = new AsyncHttpClient();
    private static final String FAILED = "啊哦，网络好像不给力\n\t请稍后再试~";
    private static final String NETWORK_FAILED = "啊哦，网络好像不给力\n\t请稍后再试~";
    private static final int BACK_DATA_EXCEPTION = -1;  //返回的数据异常状态码
    private static final int SERVICE_EXCEPTION = 9; //服务器端异常状态码
    private static final int TIME_OUT = RookieRequestConfig.DEFAULT_TIME_OUT;
    private static final int CONNECT_TIME_OUT = RookieRequestConfig.DEFAULT_CONNECT_TIME_OUT;


    static {
        httpClient.setTimeout(TIME_OUT);
        httpClient.setConnectTimeout(CONNECT_TIME_OUT);
    }

    /**
     * 取消所有请求
     *
     * @param mayInterruptIfRunning
     */
    public static void cancelAllRequests(boolean mayInterruptIfRunning) {
        httpClient.cancelAllRequests(mayInterruptIfRunning);
    }


    /**
     * 返回数据为字符串格式
     * POST
     * <p>Add a File object to the RequestParams to upload:
     * File myFile = new File("/path/to/file.png");
     * RequestParams params = new RequestParams();
     * try {
     * params.put("profile_picture", myFile);
     * } catch(FileNotFoundException e) {}</p>
     *
     * @param context
     * @param subUrl
     * @param clazz
     * @param listener
     */
    public static <W> void executePost(Context context, final String subUrl,
                                       final W clazz, final IHttpDoneListener listener) {
        final Class _aClazz = BaseEntity.class;
        final String requestUrl = BASE_URL + subUrl;
        RequestParams params = getParams(clazz);
        httpClient.post(context, requestUrl, params, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadFailed(subUrl, listener, statusCode, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadSuccess(subUrl, listener, clazz, statusCode, responseString);
            }
        });
    }


    /**
     * GET 返回数据为字符串形式
     *
     * @param context
     * @param subUrl
     * @param clazz
     * @param listener
     * @param <W>
     */
    public static <W> void executeGet(Context context, final String subUrl,
                                      final W clazz, final IHttpDoneListener listener) {
        final String requestUrl = BASE_URL + subUrl;
        httpClient.get(context, requestUrl, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                loadFailed(subUrl, listener, statusCode, responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadSuccess(subUrl, listener, clazz, statusCode, responseString);
            }
        });
    }


    /**
     * 封装messagepack的put请求
     *
     * @param context
     * @param subUrl
     * @param clazz
     * @param listener
     * @param <W>
     */
    public static <W> void executePut(Context context, final String subUrl,
                                      final W clazz, final IHttpDoneListener listener) {
        ByteArrayEntity arrayEntity = getMessagePackParams(clazz);
        String contentType = "Content-Type";
        final String requestUrl = BASE_URL + subUrl;
        httpClient.put(context, requestUrl, (HttpEntity) arrayEntity, contentType, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String resultValue = new MessagePack().read(responseBody).toString();
                    loadSuccess(subUrl, listener, clazz, statusCode, resultValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                loadFailed(subUrl, listener, statusCode, responseBody.toString(), error);
            }
        });
    }

    /**
     * 下载二进制文件
     *
     * @param context
     * @param subUrl
     * @param listener
     */
    public static void executeDownloadFile(Context context, final String subUrl, final IHttpDoneListener listener) {
        final String requestUrl = BASE_URL + subUrl;
        httpClient.get(context, requestUrl, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                listener.requestFailed(statusCode, throwable.getMessage(), subUrl);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                listener.requestSuccess(file, subUrl);
            }
        });
    }


    /**
     * 上传文件
     *
     * @param context
     * @param clazz    上传完成后，返回的数据对象
     * @param subUrl
     * @param hashMap  上传之前的参数设置
     * @param listener
     */
    public static void executeUploadFile(Context context, final Class clazz, HashMap hashMap, final String subUrl,
                                         final IHttpDoneListener listener) {
        final String requestUrl = BASE_URL + subUrl;
        RequestParams params = new RequestParams(hashMap);
        httpClient.post(context, requestUrl, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.requestFailed(statusCode, throwable.getMessage(), subUrl);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                loadSuccess(subUrl, listener, clazz, statusCode, responseString);
            }
        });

    }

    /**
     * 处理请求成功
     *
     * @param subUrl
     * @param listener
     * @param clazz
     * @param statusCode
     * @param responseString
     * @param <W>
     */
    private static <W> void loadSuccess(String subUrl, IHttpDoneListener listener, final W clazz,
                                        int statusCode, String responseString) {
        Logger.json(new GsonBuilder().serializeNulls().create().toJson(responseString));
        BaseEntity entity = getBaseData(responseString);
        int code = entity.getFlag();
        if (code == 0) {
            W w = (W) new GsonBuilder().serializeNulls().create().fromJson(entity.getData(), clazz.getClass());
            listener.requestSuccess(w, subUrl);
        } else if (code == 1) {
            String msg = entity.getMessage();
            listener.requestFailed(BACK_DATA_EXCEPTION, msg, subUrl);
        }
    }

    /**
     * 处理请求出现异常
     *
     * @param subUrl
     * @param listener
     * @param statusCode
     * @param responseString
     * @param throwable
     */
    private static void loadFailed(String subUrl, IHttpDoneListener listener,
                                   int statusCode, String responseString, Throwable throwable) {
        listener.requestFailed(statusCode, throwable.getMessage(), subUrl);
        Log.e(TAG, "onFailure: " + statusCode, throwable);
    }


    /*public static void httpPut(final Context context, String subUrl, Object obj,
                               final String action, final IHttpDoneListener listener) {
        final Class cls = BaseEntity.class;
        try {
            String requestUrl = BASE_URL + subUrl;
            ByteArrayEntity arrayEntity = getMessagePackParams(obj);
            String contentType = "Content-Type";
            httpClient.put(context, requestUrl, arrayEntity, contentType, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
//                        LoggerUtils.e(action + "返回的二进制数据为:" + responseBody.toString());
                        String resultValue = new MessagePack().read(responseBody).toString();
//                        LoggerUtils.out(responseBody.toString());
//                        LoggerUtils.json(resultValue);
                        Object result = getBaseData(resultValue);
                        if (null != result) {
                            Method codeM = cls.getMethod("getFlag");
                            int code = (int) codeM.invoke(result);
                            if (0 == code) {
                                Method dataMethod = cls.getMethod("getData");
                                String data = (String) dataMethod.invoke(result);
                                listener.requestSuccess(data, action);
                            } else if (code == 1) {
                                Method msgM = cls.getMethod("getMessage");
                                String msg = (String) msgM.invoke(result);
                                listener.requestFailed(-2, msg, action);
                            }
                        } else {
                            listener.requestFailed(-1, NETWORK_FAILED, action);
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        //3,其他错误
                        listener.requestFailed(-9, DATA_FAILED, action);
//                        LoggerUtils.e("NO Such Method Exception");
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        listener.requestFailed(-9, FAILED, action);
//                        LoggerUtils.e("Illegal Access Exception");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        listener.requestFailed(-9, FAILED, action);
//                        LoggerUtils.e("Invocation Target Exception");
                    } catch (IOException e) {
                        e.fillInStackTrace();
                        listener.requestFailed(-9, FAILED, action);
//                        LoggerUtils.e("IOException");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                    if (error instanceof HttpResponseException) {
                        HttpResponseException ex = (HttpResponseException) error;
                        int status = ex.getStatusCode();
                        listener.requestFailed(-1, FAILED, action);
//                        LoggerUtils.e("-1," + "服务器连接失败（" + status + "）" + action);
                    } else {
                        listener.requestFailed(-1, FAILED, action);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/


    /**
     * 获取返回的基本协议数据
     *
     * @param jsonString 返回的Json字符串
     * @return
     */
    private static BaseEntity getBaseData(String jsonString) {
        return new GsonBuilder().serializeNulls().create().fromJson(jsonString, BaseEntity.class);
    }

    /**
     * 生成请求的Messagepack参数
     *
     * @param _obj
     * @return
     */
    private static ByteArrayEntity getMessagePackParams(Object _obj) {
        Field[] fields = _obj.getClass().getDeclaredFields();
        RequestParams params = execRequestParams(fields, _obj);
        MessagePack msgPack = new MessagePack();
        byte[] outbytes = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Packer packer = msgPack.createPacker(out);
        try {
            packer.write(params);
            outbytes = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayEntity(outbytes);
    }

    /**
     * 生成请求的参数
     *
     * @param w
     * @return
     */
    private static <W> RequestParams getParams(W w) {
        Field[] fields = w.getClass().getDeclaredFields();
        RequestParams params = execRequestParams(fields, w);
        return params;
    }

    /**
     * 将bean转换为HashMap
     *
     * @param fields
     * @param _obj
     * @return
     */
    private static RequestParams execRequestParams(Field[] fields, Object _obj) {
        RequestParams params = new RequestParams();
        try {
            for (Field f : fields) {
                String name = f.getName();
                StringBuilder sb = new StringBuilder();
                sb.append("get");
                sb.append(name.substring(0, 1).toUpperCase());
                sb.append(name.substring(1));
                Method m = _obj.getClass().getMethod(sb.toString());

                Class fieldType = f.getType();
                String simpleName = fieldType.getSimpleName();
                if (simpleName.equalsIgnoreCase("byte[]")) {
                    byte[] byteValue = (byte[]) m.invoke(_obj);
                    params.put(name, byteValue);
                } else if (simpleName.equalsIgnoreCase("String")) {
                    String value = (String) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("int")) {
                    int value = (int) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("double")) {
                    double value = (double) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("boolean")) {
                    boolean value = (boolean) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("long")) {
                    long value = (long) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("short")) {
                    short value = (short) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("float")) {
                    float value = (float) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("char")) {
                    char value = (char) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("List")) {
                    List value = (List) m.invoke(_obj);
                    params.put(name, value);
                } else if (simpleName.equalsIgnoreCase("File")) {
                    File file = (File) m.invoke(_obj);
                    params.put(name, file);
                } else {
                    Log.e(TAG, "getParams: 未处理的类型" + simpleName);
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return params;
    }

    /**
     * Urlencode
     *
     * @param token
     * @return
     */
    public static String urlencode(String token) {
        String _t = null;
        try {
            _t = URLEncoder.encode(token, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return _t;
    }
}

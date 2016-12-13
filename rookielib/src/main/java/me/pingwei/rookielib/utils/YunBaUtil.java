package me.pingwei.rookielib.utils;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by Administrator on 2016/1/12.
 */
public class YunBaUtil {

    /**
     * yunba启动
     *
     * @param context
     */
    public static void yunBaStart(Context context) {
        YunBaManager.start(context);
    }

    /**
     * 获取绑定的topic列表
     */
    public static void getTopicList(Context context) {
        YunBaManager.getTopicList(context,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken mqttToken) {
                        JSONObject result = mqttToken.getResult();
                        try {
                            JSONArray topics = result.getJSONArray("topics");
                            LoggerUtils.out("绑定的topic:" + topics.toString());

                        } catch (JSONException e) {
                            LoggerUtils.out(e.fillInStackTrace().toString());
                        }
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "getTopicList failed with error code : " + ex.getReasonCode();
                            Log.e("获取App绑定topic_Fail", "getTopicList failed : " + msg);
                        }
                    }
                }
        );
    }

    /**
     * 解除绑定
     *
     * @param context
     * 线上时需要修改
     */
    private static final String OFFLINE_TOPIC = "TopFit";               //线下topic
    private static final String ONLINE_TOPIC = "TopFit";                //线上topic

    /**
     * 解绑用户的topic--------在退出登录时调用
     *
     * @param context
     */
    public static void unSubscribeAllTopic(Context context, String shop_userId) {

        YunBaManager.unsubscribe(context, shop_userId,
                new IMqttActionListener() {

                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {

                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "unSubscribe failed with error code : " + ex.getReasonCode();
                            Log.e("获取App绑定topic_Fail", "getTopicList failed : " + msg);
                        }
                    }
                }
        );
    }


    /**
     * 解绑系统的topic
     *
     * @param context
     */
    public static void unSubscribeTopic(Context context) {
        //解绑所有的topic列表
        YunBaManager.unsubscribe(context, new String[]{OFFLINE_TOPIC},
                new IMqttActionListener() {

                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "unSubscribe failed with error code : " + ex.getReasonCode();
                            Log.e("获取App绑定topic_Fail", "getTopicList failed : " + msg);
                        }
                    }
                }
        );
    }

    /**
     * 订阅用户topic
     *
     * @param context
     */
    public static void subscribeUserIdTopic(Context context, String shop_userId) {
        YunBaManager.subscribe(context, shop_userId,
                new IMqttActionListener() {

                    @Override
                    public void onSuccess(IMqttToken arg0) {
                        String[] playload = arg0.getTopics();

                        Log.e("用户subscribeTopic size:", String.valueOf(playload.length));
                        Log.e("用户subscribeTopic:", playload[0].toString());
                    }

                    @Override
                    public void onFailure(IMqttToken arg0, Throwable arg1) {
                        String msg = "Subscribe failed : " + arg1.getMessage();
                        Log.e("初始化App绑定topic_Fail", msg);
                        Log.e("初始化App绑定topic_Fail", "Subscribe topic failed");
                    }
                });
    }

    /**
     * 订阅系统topic
     *
     * @param context
     */
    public static void subscribeTopic(Context context) {
        YunBaManager.subscribe(context, ONLINE_TOPIC,
                new IMqttActionListener() {

                    @Override
                    public void onSuccess(IMqttToken arg0) {
                        String[] playload = arg0.getTopics();
                        LoggerUtils.out("subscribeTopic:" + playload.toString());
                        Log.e("系统subscribeTopic size:", String.valueOf(playload.length));
                        Log.e("系统subscribeTopic:", playload[0].toString());
                    }

                    @Override
                    public void onFailure(IMqttToken arg0, Throwable arg1) {
                        String msg = "Subscribe failed : " + arg1.getMessage();
                        Log.e("初始化App绑定topic_Fail", msg);
                        Log.e("初始化App绑定topic_Fail", "Subscribe topic failed");
                    }
                });
    }

    /**
     * 绑定别名
     *
     * @param context
     */
    public static void setAlias(final Context context, String user_id) {
        //绑定别名--用于点对点推送
        YunBaManager.setAlias(context, user_id,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken iMqttToken) {
                    }

                    @Override
                    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                        if (throwable instanceof MqttException) {
                            MqttException ex = (MqttException) throwable;
                            String msg = "setAlias failed with error code : " + ex.getReasonCode();
                            Log.e("初始化App绑定alias_Fail", "setAlias failed : " + msg);
                        }
                    }
                });
    }


    public static void publishToAlias(Context mContext, String topic, String msg) {
        YunBaManager.publishToAlias(mContext, topic, msg,
                new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        String topic = join(asyncActionToken.getTopics(), ", ");
                        String msgLog = "publish to alias succeed : " + topic;
                        LoggerUtils.out("msgLog:" + msgLog);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        if (exception instanceof MqttException) {
                            MqttException ex = (MqttException) exception;
                            String msg = "publishToAlias failed with error code : " + ex.getReasonCode();
                            LoggerUtils.out("msgLog:" + msg);
                        }
                    }
                }
        );
    }


    public static <T> String join(T[] array, String cement) {
        StringBuilder builder = new StringBuilder();

        if (array == null || array.length == 0) {
            return null;
        }
        for (T t : array) {
            builder.append(t).append(cement);
        }

        builder.delete(builder.length() - cement.length(), builder.length());

        return builder.toString();
    }
}

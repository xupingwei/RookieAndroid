package me.pingwei.rookielib.app;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by xupingwei on 2015/7/17.
 */
public class ActivityTask {
    private static Stack<Activity> activityStack = new Stack<Activity>();


    private ActivityTask() {

    }

    public static Stack<Activity> getStackInstance() {
        return activityStack;
    }

    /**
     * 添加Activity到堆栈
     *
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activityStack.push(activity);
    }

    /**
     * 获取当前的Activity（堆栈中最后一个压入的）
     *
     * @return
     */
    public static Activity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 结束当前的Activity（堆栈中最后一个压入的）
     */
    public static void finishCurrentActivity() {
        if (!activityStack.empty()) {
            Activity activity = activityStack.pop();
            activity.finish();
        }
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public static void finishActivity(Activity activity) {
        if (null != activity) {
            //解决ConcurrentModificationException
            synchronized (activity.getClass()) {
                activityStack.remove(activity);
            }
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public static void finishActivity(Activity activity, Class<?> aClass) {
        if (null != activity) {

            //解决ConcurrentModificationException
            synchronized (activity.getClass()) {
                if (activity.getClass().equals(aClass)) {
                    activityStack.remove(activity);
                }
            }

            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param aClass
     */
    public static void finishActivity(Class<?> aClass) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(aClass)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        for (Activity activity : activityStack) {
            if (null != activity) {
                activity.finish();
            }
        }
        activityStack.clear();
    }

}

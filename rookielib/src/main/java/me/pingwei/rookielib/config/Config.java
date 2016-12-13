package me.pingwei.rookielib.config;

/**
 * Created by xupingwei on 2016/4/14.
 */
public class Config {

    private static String mProjectName = "Rookie";

    //图片缓存文件夹
    private static String cacheImage = mProjectName + ".cache/";
    private static String cacheLog = mProjectName + "log/";

    /**
     * 返回项目名称
     *
     * @return
     */
    public static String getProjectName() {
        return mProjectName;
    }


    /**
     * 获取图片缓存路径
     *
     * @return
     */
    public static String getCacheImage() {
        return cacheImage;
    }

    public static void setProjectPath(String pathName) {
        mProjectName = pathName;
    }


}

package me.pingwei.rookielib.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2016/12/17.
 */

public class SSLContextUtil {

    /**
     * 拿到SSLContext
     * 包涵Https证书
     */
    @SuppressLint("TrulyRandom")
    public static SSLContext getSSLContext(Context context) {
        SSLContext sslContext = null;
        try {
            InputStream inputStream = context.getApplicationContext().getAssets().open("zhengshu.cer");
            CertificateFactory cerFactory = CertificateFactory.getInstance("X.509");
            Certificate cer = cerFactory.generateCertificate(inputStream);
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(null, null);
            keyStore.setCertificateEntry("trust", cer);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, null);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }


    /**
     * 获取默认的允许所有的
     * 没有证书直接允许Https请求
     */
    public static SSLContext getDefaultSLLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManagers}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    private static TrustManager trustManagers = new X509TrustManager() {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };
}

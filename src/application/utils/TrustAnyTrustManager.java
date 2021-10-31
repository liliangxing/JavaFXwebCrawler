package application.utils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @Author: Administrator
 * @Date: 2021/10/31 15:33
 * @Description:
 */
public class TrustAnyTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }
    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
    }

    public static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static void getHttps(HttpURLConnection conn,String userAgent){
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Referer", DownFile.referrer);
        conn.setConnectTimeout(10 * 1000);
        conn.setInstanceFollowRedirects(true);
        if (conn instanceof HttpsURLConnection) {
            try {
                SSLContext sc = SSLContext.getInstance("SSL");
                // -------------------------new OrderInfoController替换成你当前类的类名即可--------------
                sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new 					java.security.SecureRandom());
                ((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
                // -------------------------new OrderInfoController替换成你当前类的类名即可--------------
                ((HttpsURLConnection) conn).setHostnameVerifier(new TrustAnyTrustManager.TrustAnyHostnameVerifier());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

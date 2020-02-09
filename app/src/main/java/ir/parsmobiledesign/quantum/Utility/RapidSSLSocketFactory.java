package ir.parsmobiledesign.quantum.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.MySSLSocketFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class RapidSSLSocketFactory extends AsyncHttpClient {

    public RapidSSLSocketFactory() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException, CertificateException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
        this.setSSLSocketFactory(new MySSLSocketFactory(trustStore));

    }
    public static SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }
}

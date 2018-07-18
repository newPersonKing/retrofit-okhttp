package com.xsb.myupload.http;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class NovateHttpsFactroy {

    public static SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslContext=SSLContext.getInstance("SSL");
            sslContext.init(null,creatTrustManager(),new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static TrustManager[] creatTrustManager() {
        TrustManager[] trustManagers=new TrustManager[]{
             new X509TrustManager() {
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
             }
        };
        return trustManagers;
    }

    /**
     * X509TrustManager
     */
    public static X509TrustManager creatX509TrustManager() {

        return new X509TrustManager(){
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

    /*设置信任自签名证书*/
    public static SSLSocketFactory creatSSLSocketFactory(Context context, String name){
        if (context==null){
            throw new NullPointerException("context not null");
        }

        CertificateFactory certificateFactory;
        InputStream inputStream=null;
        Certificate certificate;

        try {
            inputStream=context.getAssets().open(name);
        }catch (IOException e) {
            e.printStackTrace();
        }

        try {
            certificateFactory=CertificateFactory.getInstance("X.509");
            certificate=certificateFactory.generateCertificate(inputStream);
            //Create a KeyStore containing our trusted CAs
            KeyStore keyStore=KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            keyStore.setCertificateEntry(name,certificate);
            //Create a TrustManager that trusts the CAs in our keyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            //Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get SkipHostnameVerifier
     *
     * @return
     */
    public static HostnameVerifier creatSkipHostnameVerifier() {
        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };
        return hostnameVerifier;
    }

}

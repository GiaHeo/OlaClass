package com.example.olaclass.network;

import android.content.Context;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import okhttp3.CertificatePinner;
import okhttp3.OkHttpClient;

public class PinnedOkHttpProvider {
    // Thay đổi domain và SHA256 theo chứng chỉ server của bạn
    private static final String HOSTNAME = "your.api.domain.com";
    private static final String SHA256 = "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    public static OkHttpClient getPinnedClient() {
        CertificatePinner certificatePinner = new CertificatePinner.Builder()
                .add(HOSTNAME, SHA256)
                .build();
        return new OkHttpClient.Builder()
                .certificatePinner(certificatePinner)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}

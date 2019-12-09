package com.chezi008.android.socket;

import android.annotation.SuppressLint;
import android.content.Context;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author ：chezi008 on 2018/7/18 15:48
 * @description ：普通socket，ip 端口连接
 * @email ：chezi008@163.com
 */
public class NormalSocket extends BaseTcpSocket {
    private static final String CLIENT_AGREEMENT = "TLS";
    public NormalSocket(String ip, int port) {
        super(ip, port);
    }

    public NormalSocket(String ip, int port, ISocketListener listener) {
        super(ip, port, listener);
    }

    @Override
    protected void initSocket(Context ctx) throws Exception {
        SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
        TrustManager manager = new X509TrustManager() {
            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {

            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sslContext.init(null, new TrustManager[]{manager}, null);

        mSocket = sslContext.getSocketFactory().createSocket(mHost, mPort);
        mSocket.setReceiveBufferSize(RECEIVE_BUFFER_SIEZE);
        mSocket.setSoTimeout(SOCKET_TIME_OUT);
        //在握手之前会话是无效的
        ((SSLSocket) mSocket).startHandshake();
    }
}

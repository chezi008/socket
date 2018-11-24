package com.chezi008.android.socket;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

/**
 * @author ：chezi008 on 2018/7/18 15:48
 * @description ：单向加密的tcp链接
 * 1、TCP应建立一个心跳机制，不然长时间没有数据的传输会被关闭。
 * @email ：chezi008@163.com
 */
public class SSLTcpSocket extends BaseTcpSocket {

    private static final String CLIENT_AGREEMENT = "TLS";
    private static final String CLIENT_TRUST_MANAGER = "X509";
    private static final String CLIENT_TRUST_KEYSTORE = "BKS";

    public SSLTcpSocket(@NonNull String ip, @NonNull int port) {
        super(ip, port);
    }

    public SSLTcpSocket(@NonNull String ip, @NonNull int port, @Nullable ISocketListener listener) {
        super(ip, port, listener);
    }

    public void setSSLConfi(@IntegerRes int keyPath, String keyPwd) {
        mKeyPath = keyPath;
        mKeyPwd = keyPwd;
    }

    private int mKeyPath;
    private String mKeyPwd;

    @Override
    protected void initSocket(Context ctx) throws Exception {
        SSLContext sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
        TrustManagerFactory trustManager = TrustManagerFactory.getInstance(CLIENT_TRUST_MANAGER);
        KeyStore tks = KeyStore.getInstance(CLIENT_TRUST_KEYSTORE);
        tks.load(ctx.getResources().openRawResource(mKeyPath), mKeyPwd.toCharArray());
        trustManager.init(tks);
        sslContext.init(null, trustManager.getTrustManagers(), null);

        mSocket = sslContext.getSocketFactory().createSocket(mHost, mPort);
        mSocket.setReceiveBufferSize(RECEIVE_BUFFER_SIEZE);
        mSocket.setSoTimeout(SOCKET_TIME_OUT);
        //在握手之前会话是无效的
        ((SSLSocket) mSocket).startHandshake();
    }
}

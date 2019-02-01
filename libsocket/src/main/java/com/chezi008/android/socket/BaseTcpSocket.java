package com.chezi008.android.socket;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ：chezi008 on 2018/8/20 17:51
 * @description ：
 * @email ：chezi008@163.com
 */
public class BaseTcpSocket implements ISocket {

    protected String TAG = getClass().getSimpleName();

    protected String mHost;
    protected int mPort;

    protected Socket mSocket;
    protected InputStream mInStream;
    protected OutputStream mOutStream;

    protected ISocketListener mSocketListener;

    /**
     * 包含 1、连接
     * 2、发送
     */
    private ExecutorService esSocket = Executors.newFixedThreadPool(3);
    private LinkedBlockingQueue<SocketData> queueBuffer = new LinkedBlockingQueue<>();
    private Future futSend;

    /**
     * @param ip
     * @param port
     */
    public BaseTcpSocket(@NonNull String ip, @NonNull int port) {
        this(ip, port, null);
    }

    public BaseTcpSocket(@NonNull String ip, @NonNull int port, @Nullable ISocketListener listener) {
        this.mSocketListener = listener;
        this.mHost = ip;
        this.mPort = port;
    }

    @Override
    public void connect(@Nullable final Context ctx) {
        esSocket.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    initSocket(ctx);
                    startReceive();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mSocketListener != null) {
                        mSocketListener.onFaild(e);
                    }
                }
            }
        });
    }

    protected final int PACKED_BUFFER_SIZE = 3000;
    protected final int SOCKET_TIME_OUT = 30000;
    protected final int RECEIVE_BUFFER_SIEZE = 2 * 1024 * 1024;

    private void startReceive() {
        try {
            //连接成功开始接收数据
            mOutStream = mSocket.getOutputStream();
            mInStream = mSocket.getInputStream();
            //连接成功
            if (mSocketListener != null ) {
                mSocketListener.onSuccess();
            }
            startSend();

            // 从Socket当中得到InputStream对象
            byte[] buffer = new byte[PACKED_BUFFER_SIZE];
            int packetSize;
            while ((packetSize = mInStream.read(buffer, 0, PACKED_BUFFER_SIZE)) != -1) {
                if (mSocketListener != null) {
                    mSocketListener.recv(buffer, 0, packetSize);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            close();
            if (mSocketListener != null) {
                mSocketListener.onFaild(e);
            }
        }
    }

    /**
     * 启动发送线程
     */
    private void startSend() {
        if (futSend == null || futSend.isDone()) {
            futSend = esSocket.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        SocketData data;
                        while ((data = queueBuffer.take()) != null) {
//                            Log.d(TAG, "run: -->发送数据 size:" + data.size);
//                            Log.d(TAG, "run: "+ Arrays.toString(data.data));
                            mOutStream.write(data.data, data.offset, data.size);
                        }
                    } catch (InterruptedException e) {

                    } catch (IOException e) {
                        e.printStackTrace();
                        if (mSocketListener != null) {
                            mSocketListener.onFaild(e);
                        }
                    }
                    queueBuffer.clear();
                    if (mSocketListener != null) {
                        mSocketListener.onClose();
                    }
                }
            });
        }


    }

    @Override
    public void send(byte[] data) {
        send(data, 0, data.length);
    }

    @Override
    public void send(byte[] data, int offset, int size) {
        try {
            queueBuffer.put(new SocketData(data, offset, size));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        //先把流给关闭
        try {
            if (mInStream!=null){
                mInStream.close();
            }
            if (mOutStream != null) {
                mOutStream.close();
            }
            if (mSocket != null) {
                mSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isClosed() {
        return mSocket != null ? mSocket.isClosed() : false;
    }

    @Override
    public void setSocketListener(ISocketListener listener) {
        this.mSocketListener  = listener;
    }

    /**
     * 初始化socket
     *
     * @param ctx
     * @return is init success
     * @throws Exception
     */
    protected void initSocket(@Nullable Context ctx)throws Exception{
        mSocket = new Socket(mHost, mPort);
        mSocket.setReceiveBufferSize(RECEIVE_BUFFER_SIEZE);
    }
}

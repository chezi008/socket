package com.chezi008.android.socket;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ：chezi008 on 18-9-28 下午2:52
 * @description ：
 * @email ：chezi008@qq.com
 */
public class BaseUdpSocket implements ISocket {
    private String TAG = getClass().getSimpleName();

    private DatagramSocket mDatagramSocket;
    private final int MAX_UDP_DATAGRAM_LEN = 1024 * 2;
    private final int UDP_TIME_OUT = 15000;

    protected ISocketListener mSocketListener;

    private byte[] sendBuffer = new byte[MAX_UDP_DATAGRAM_LEN];

    protected ExecutorService esClient = Executors.newFixedThreadPool(3);
    private Future ftRecv, ftSend;

    private LinkedBlockingQueue<SocketData> queueBuffer = new LinkedBlockingQueue<>();

    protected String mHost;
    protected int mPort;

    /**
     * @param ip
     * @param port
     */
    public BaseUdpSocket(@NonNull String ip, @NonNull int port) {
        this(ip, port, null);
    }

    public BaseUdpSocket(@NonNull String ip, @NonNull int port, @Nullable ISocketListener listener) {
        this.mSocketListener = listener;
        this.mHost = ip;
        this.mPort = port;
    }

    @Override
    public void connect(@NonNull Context ctx){
        if (ftRecv == null || ftRecv.isDone()) {
            ftRecv = esClient.submit(recvRunnable);
        }
    }

    @Override
    public void send(byte[] data) {
        send(data, 0, data.length);
    }

    @Override
    public void send(byte[] data, int offset, int size) {
//        Log.d(TAG, "send: -->" + size);
        try {
            queueBuffer.put(new SocketData(data, offset, size));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (mDatagramSocket != null) {
            mDatagramSocket.close();
        }
        queueBuffer.clear();
        ftSend.cancel(true);
        ftRecv.cancel(true);
    }

    @Override
    public boolean isClosed() {
        return ftRecv == null || ftRecv.isDone();
    }

    @Override
    public void setSocketListener(ISocketListener listener) {
        this.mSocketListener = listener;
    }


    private Runnable recvRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                mDatagramSocket = new DatagramSocket(mPort);
                byte[] buffer = new byte[MAX_UDP_DATAGRAM_LEN];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, MAX_UDP_DATAGRAM_LEN);
//                mDatagramSocket.setSoTimeout(UDP_TIME_OUT);

                //连接成功
                if (mSocketListener != null) {
                    mSocketListener.onSuccess();
                }
                startSendRunnable();
                //线程阻塞
                mDatagramSocket.receive(datagramPacket);
                int size = datagramPacket.getLength();
                Log.d(TAG, "receive Process: " + android.os.Process.myTid() + "  length:" + size);
                while (size > 0) {
                    if (mSocketListener != null) {
                        mSocketListener.revc(buffer, 0, size);
                    }
                    mDatagramSocket.receive(datagramPacket);
                    size = datagramPacket.getLength();
                }
            } catch (SocketException e) {
                if (mSocketListener != null) {
                    mSocketListener.onFaild(e);
                }
                e.printStackTrace();
            } catch (IOException e) {
                if (mSocketListener != null) {
                    mSocketListener.onFaild(e);
                }
                e.printStackTrace();
            }finally {
                close();
            }
            if (mSocketListener != null) {
                mSocketListener.onClose();
            }
            Log.d(TAG, this.toString() + "start: -->client接收线程结束");
        }
    };

    private void startSendRunnable() {
        if (ftSend == null || ftSend.isDone()) {
            String connectLog = String.format("连接的 ip:%s,prot:%d", mHost, mPort);
            Log.d(TAG, "start: " + connectLog);
            ftSend = esClient.submit(sendBufRunnable);
        }
    }

    private Runnable sendBufRunnable = new Runnable() {
        @Override
        public void run() {

            SocketData socketData;
            try {
                DatagramPacket datagramPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName(mHost), mPort);
                while ((socketData = queueBuffer.take()) != null) {
                    datagramPacket.setData(socketData.data, socketData.offset, socketData.size);
                    mDatagramSocket.send(datagramPacket);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

}

package com.chezi008.android.socket;

/**
 * @author ：chezi008 on 2018/7/18 15:54
 * @description ：
 * @email ：chezi008@163.com
 */
public interface ISocketListener {
    /**
     * 接收的数据
     * @param data
     * @param offset
     * @param size
     */
    void revc(byte[] data, int offset, int size);

    /**
     * 连接成功
     */
    void onSuccess();

    /**
     * 连接异常
     * @param t
     */
    void onFaild(Throwable t);

    /**
     * 连接失败
     */
    void onClose();
}

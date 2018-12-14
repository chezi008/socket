package com.chezi008.android.socket;

/**
 * @author ：chezi008 on 2018/7/24 16:40
 * @description ：
 * @email ：chezi008@163.com
 */
public class SocketData {
    public byte[] data;
    public int offset;
    public int size;

    public SocketData(byte[] data, int offset, int size) {
        this.data = data;
        this.offset = offset;
        this.size = size;
    }
}

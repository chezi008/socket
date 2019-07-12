# socket
libsocket库是基于socket封装的含TCP/UDP/SSLTCP通信功能的网络库，在库中定义了网络通信接口Isocket。

[![](https://jitpack.io/v/chezi008/socket.svg)](https://jitpack.io/#chezi008/socket)

#### 1、如何依赖
第一步：
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
第二步：
```
dependencies {
	        implementation 'com.github.chezi008:socket:1.x.x'
	}
```
#### 2、对外开放方法

```
public interface ISocket {
    /**
     * 连接网络
     * @param ctx ctx只有ssl连接的时候才需要，
     *            其他的情况可为null
     */
    void connect(Context ctx);
    void send(byte[] data);
    /**
     * 发送数据
     *
     * @param data   发送的数据
     * @param offset 偏移量
     * @param size   长度
     */
    void send(byte[] data, int offset, int size);
    /**
     * 结束连接
     */
    void close();
    /**
     * 是否是连接的
     */
    boolean isClosed();
    /**
     * 添加tcp状态的回调
     *
     * @param listener
     */
    void setSocketListener(ISocketListener listener);
}
```

#### 3、相关类说明

```
以下类均实现ISocket接口

BaseTcpSocket：封装的tcp类，使用tcp功能可以继承该类
BaseUdpSocket：封装udp功能，使用udp功能可以继承该类
SSLTcpSocket：基于tcp的SSL加密封装，需要使用SSL功能的TCP连接可以继承该类
```



#### 相关功能

- 添加BaseUdpSocket类，封装UDP基本通信功能，包含发送和接收，以及相关状态的回调。
- 添加BaseTcpSocket类，封装TCP基本通信功能，包含发送和接收，以及相关状态的回调。
- 添加SSLTcpScoket类，封装SSLScoket加密通信基本功能，包含发送和接收，以及相关状态的回调。

#### 更新
#### 1.0.5
- 将SocketData设为公开的

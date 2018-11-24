# socket
基本的网络通信库,封装UDP/TCP/HTTP通信等。

[![](https://jitpack.io/v/chezi008/socket.svg)](https://jitpack.io/#chezi008/socket)

## 如何依赖
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
## 相关功能
- 添加BaseUdpSocket类，封装UDP基本通信功能，包含发送和接收，以及相关状态的回调。
- 添加BaseTcpSocket类，封装TCP基本通信功能，包含发送和接收，以及相关状态的回调。
- 添加SSLTcpScoket类，封装SSLScoket加密通信基本功能，包含发送和接收，以及相关状态的回调。
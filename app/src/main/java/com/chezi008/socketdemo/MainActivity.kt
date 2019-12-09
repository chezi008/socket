package com.chezi008.socketdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.chezi008.android.socket.ISocketListener
import com.chezi008.android.socket.NormalSocket
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity : AppCompatActivity(), ISocketListener {
    override fun recv(data: ByteArray?, offset: Int, size: Int) {
        Log.d("MainActivity", "recv: ")
    }

    override fun onSuccess() {
        Log.d("MainActivity", "onSuccess: ")
    }

    override fun onFaild(t: Throwable?) {
        Log.d("MainActivity", "onFaild: ")
    }

    override fun onClose() {
        Log.d("MainActivity", "onClose: ")
    }

    companion object{
        const val IP = ""
        const val port = 6688
    }

    private var mTcpClient: NormalSocket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        conect.setOnClickListener {
            mTcpClient = NormalSocket(IP, port,this)
            mTcpClient?.connect(this@MainActivity)
        }
    }
}

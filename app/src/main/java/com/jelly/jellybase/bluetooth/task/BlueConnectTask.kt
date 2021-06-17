package com.jelly.jellybase.bluetooth.task

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.jelly.jellybase.bluetooth.util.BluetoothConnector
import com.jelly.jellybase.bluetooth.util.BluetoothConnector.NativeBluetoothSocket
import kotlinx.coroutines.*

class BlueConnectTask(private val mAddress: String) : CoroutineScope by MainScope() {
    fun executeOnExecutor(vararg params: BluetoothDevice) {
        launch {
            //蓝牙连接需要完整的权限,有些机型弹出提示对话框"***想进行通信",这就不行,日志会报错:
                //read failed, socket might closed or timeout, read ret: -1
            val socket=withContext(Dispatchers.Default) {
                    val connector = BluetoothConnector(
                        params[0], true,
                        BluetoothAdapter.getDefaultAdapter(), null
                    )
                    Log.d(TAG, "doInBackground")
                    var socket: BluetoothSocket? = null
                    //蓝牙连接需要完整的权限,有些机型弹出提示对话框"***想进行通信",这就不行,日志会报错:
                    //read failed, socket might closed or timeout, read ret: -1
                    try {
                        socket = (connector.connect() as NativeBluetoothSocket).underlyingSocket
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    socket
                }
            mListener!!.onBlueConnect(mAddress, socket)
        }
    }

    private var mListener: BlueConnectListener? = null
    fun setBlueConnectListener(listener: BlueConnectListener?) {
        mListener = listener
    }

    interface BlueConnectListener {
        fun onBlueConnect(address: String?, socket: BluetoothSocket?)
    }

    companion object {
        private const val TAG = "BlueConnectTask"
    }
}
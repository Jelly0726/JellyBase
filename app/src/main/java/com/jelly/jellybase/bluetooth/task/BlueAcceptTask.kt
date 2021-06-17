package com.jelly.jellybase.bluetooth.task

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.jelly.jellybase.bluetooth.util.BluetoothConnector
import kotlinx.coroutines.*
import java.lang.reflect.Method

class BlueAcceptTask(secure: Boolean) : CoroutineScope by MainScope() {
    private val mAdapter: BluetoothAdapter
    private var mmServerSocket: BluetoothServerSocket? = null
   fun executeOnExecutor(vararg params: Any){
       Log.d(TAG, "doInBackground")
       launch {
           val socket= withContext(Dispatchers.IO) {
               var socket: BluetoothSocket? = null
               while (true) {
                   try {
                       socket = mmServerSocket!!.accept()
                   } catch (e: Exception) {
                       e.printStackTrace()
                       try {
                           Thread.sleep(1000)
                       } catch (e1: InterruptedException) {
                           e1.printStackTrace()
                       }
                   }
                   if (socket != null) {
                       break
                   }
               }
               socket
           }
           mListener!!.onBlueAccept(socket)
       }
    }
    private var mListener: BlueAcceptListener? = null
    fun setBlueAcceptListener(listener: BlueAcceptListener?) {
        mListener = listener
    }

    interface BlueAcceptListener {
        fun onBlueAccept(socket: BluetoothSocket?)
    }

    companion object {
        private const val TAG = "BlueAcceptTask"
        private const val NAME_SECURE = "BluetoothChatSecure"
        private const val NAME_INSECURE = "BluetoothChatInsecure"
    }

    init {
        Log.d(TAG, "BlueAcceptTask")
        mAdapter = BluetoothAdapter.getDefaultAdapter()
        try {
            mmServerSocket = if (secure) {
                mAdapter.listenUsingRfcommWithServiceRecord(
                    NAME_SECURE, BluetoothConnector.uuid
                )
            } else {
                mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    NAME_INSECURE, BluetoothConnector.uuid
                )
            }
        } catch (e: Exception) {
            Log.d(TAG, "BlueAcceptTask First failed")
            e.printStackTrace()
            var listenMethod: Method? = null
            try {
                listenMethod = mAdapter.javaClass.getMethod(
                    "listenUsingRfcommOn", *arrayOf<Class<*>?>(Int::class.javaPrimitiveType)
                )
                mmServerSocket = listenMethod.invoke(
                    mAdapter, *arrayOf<Any>(29)
                ) as BluetoothServerSocket
            } catch (e1: Exception) {
                Log.d(TAG, "BlueAcceptTask Second failed")
                e1.printStackTrace()
            }
        }
    }
}
package com.jelly.jellybase.bluetooth;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.bluetooth.adapter.DeviceAdapter;
import com.jelly.jellybase.bluetooth.bean.BlueDevice;
import com.jelly.jellybase.bluetooth.task.BlueAcceptTask;
import com.jelly.jellybase.bluetooth.task.BlueConnectTask;
import com.jelly.jellybase.bluetooth.task.BlueReceiveTask;
import com.jelly.jellybase.bluetooth.util.BluetoothUtil;
import com.jelly.jellybase.bluetooth.widget.InputDialogFragment;
import com.jelly.jellybase.databinding.BluetoothActivityBinding;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
/**
 * 扫描传统蓝牙
 */
public class BluetoothActivity extends BaseActivity<BluetoothActivityBinding> implements
        OnItemClickListener, OnCheckedChangeListener,
        BlueConnectTask.BlueConnectListener, InputDialogFragment.InputCallbacks, BlueAcceptTask.BlueAcceptListener, View.OnClickListener {
    private static final String TAG = "BluetoothActivity";
    private BluetoothAdapter mBluetooth;
    private DeviceAdapter adapter;
    private ArrayList<BlueDevice> mDeviceList = new ArrayList<BlueDevice>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothPermissions();
        if (BluetoothUtil.getBlueToothStatus(this) == true) {
            getBinding().ckBluetooth.setChecked(true);
        }
        getBinding().ckBluetooth.setOnCheckedChangeListener(this);
        getBinding().leftBack.setOnClickListener(this);
        getBinding().tvDiscovery.setOnClickListener(this);
        mBluetooth = BluetoothAdapter.getDefaultAdapter();
        if (mBluetooth == null) {
            Toast.makeText(this, "本机未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            finish();
        }
        adapter=new DeviceAdapter(getApplicationContext(),mDeviceList);
        adapter.setOnDeviceClickListener(new DeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BlueDevice bleDevice,int position) {
                cancelDiscovery();
                BluetoothDevice device = mBluetooth.getRemoteDevice(bleDevice.getAddress());
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Method createBondMethod = null;
                    try {
                        createBondMethod = BluetoothDevice.class.getMethod("createBond");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "开始配对");
                    try {
                        Boolean result = (Boolean) createBondMethod.invoke(device);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                        bleDevice.getState() != DeviceAdapter.CONNECTED) {
                    getBinding().tvDiscovery.setText("开始连接");
                    BlueConnectTask connectTask = new BlueConnectTask(bleDevice.getAddress());
                    connectTask.setBlueConnectListener(BluetoothActivity.this);
                    connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device);
                }
            }

            @Override
            public void onDisConnect(final BlueDevice bleDevice,int position) {
                cancelDiscovery();
                getBinding().tvDiscovery.setText("连接已断开");
                mBlueSocket=null;
                if (bleDevice.getSocket() != null) {
                    try {
                        bleDevice.getSocket().close();
                        bleDevice.setSocket(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                bleDevice.setState(DeviceAdapter.BINDING);
                mDeviceList.set(position,bleDevice);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDetail(BlueDevice bleDevice,int position) {
                cancelDiscovery();
                BluetoothDevice device = mBluetooth.getRemoteDevice(bleDevice.getAddress());
                if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                        bleDevice.getState() == DeviceAdapter.CONNECTED) {
                    mBlueSocket=bleDevice.getSocket();
                    if (mBlueSocket!=null) {
                        getBinding().tvDiscovery.setText("正在发送消息");
                        InputDialogFragment dialog = InputDialogFragment.newInstance(
                                "", 0, "请输入要发送的消息");
                        String fragTag = getResources().getString(R.string.app_name);
                        dialog.show(getFragmentManager(), fragTag);
                    }
                }
            }
        });
        getBinding().lvBluetooth.setAdapter(adapter);
    }
    // 定义获取基于地理位置的动态权限
    private void bluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    /**
     * 重写onRequestPermissionsResult方法
     * 获取动态权限请求的结果,再开启蓝牙
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (BluetoothUtil.getBlueToothStatus(this) == true) {
                getBinding().ckBluetooth.setChecked(true);
            }
            getBinding().ckBluetooth.setOnCheckedChangeListener(this);
            mBluetooth = BluetoothAdapter.getDefaultAdapter();
            if (mBluetooth == null) {
                Toast.makeText(this, "本机未找到蓝牙功能", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "用户拒绝了权限", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.ck_bluetooth) {
            if (isChecked == true) {
                beginDiscovery();
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(intent, 1);
                // 下面这行代码为服务端需要，客户端不需要
                mHandler.postDelayed(mAccept, 1000);
            } else {
                cancelDiscovery();
                BluetoothUtil.setBlueToothStatus(this, false);
                mDeviceList.clear();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private Runnable mAccept = new Runnable() {
        @Override
        public void run() {
            if (mBluetooth.getState() == BluetoothAdapter.STATE_ON) {
                BlueAcceptTask acceptTask = new BlueAcceptTask(true);
                acceptTask.setBlueAcceptListener(BluetoothActivity.this);
                acceptTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.tv_discovery:
                beginDiscovery();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "允许本地蓝牙被附近的其它蓝牙设备发现", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "不允许蓝牙被附近的其它蓝牙设备发现", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Runnable mRefresh = new Runnable() {
        @Override
        public void run() {
            beginDiscovery();
            mHandler.postDelayed(this, 2000);
        }
    };

    private void beginDiscovery() {
        if (mBluetooth.isDiscovering() != true) {
            mDeviceList.clear();
            adapter.notifyDataSetChanged();
            getBinding().tvDiscovery.setText("正在搜索蓝牙设备");
            mBluetooth.startDiscovery();
        }
    }

    private void cancelDiscovery() {
        mHandler.removeCallbacks(mRefresh);
        getBinding().tvDiscovery.setText("取消搜索蓝牙设备");
        if (mBluetooth.isDiscovering() == true) {
            mBluetooth.cancelDiscovery();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHandler.postDelayed(mRefresh, 50);
        blueReceiver = new BluetoothReceiver();
        //需要过滤多个动作，则调用IntentFilter对象的addAction添加新动作
        IntentFilter foundFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        foundFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索完成
        foundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态改变
        foundFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式改变了
        foundFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生了变化
        registerReceiver(blueReceiver, foundFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cancelDiscovery();
        unregisterReceiver(blueReceiver);
    }

    private BluetoothReceiver blueReceiver;

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive action=" + action);
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (device.getType()!=BluetoothDevice.DEVICE_TYPE_CLASSIC){
                        return;
                    }
                }
                short rssi=intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);//获取额外rssi值
                int num=0;
                for (int i=0;i<mDeviceList.size();i++) {
                    if (mDeviceList.get(i).getAddress().equals(device.getAddress())) {
                        num++;
                    }
                }
                if (num==0){
                    BlueDevice item = new BlueDevice(device, device.getBondState() - 10,rssi);
                    mDeviceList.add(item);
                    adapter.notifyDataSetChanged();
                }
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                mHandler.removeCallbacks(mRefresh);
                getBinding().tvDiscovery.setText("蓝牙设备搜索完成");
            } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    getBinding().tvDiscovery.setText("正在配对" + device.getName());
                } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    getBinding().tvDiscovery.setText("完成配对" + device.getName());
                    mHandler.postDelayed(mRefresh, 50);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    getBinding().tvDiscovery.setText("取消配对" + device.getName());
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        cancelDiscovery();
        BlueDevice item = mDeviceList.get(position);
        BluetoothDevice device = mBluetooth.getRemoteDevice(item.getAddress());
        try {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                Log.d(TAG, "开始配对");
                Boolean result = (Boolean) createBondMethod.invoke(device);
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                    item.getState() != DeviceAdapter.CONNECTED) {
                getBinding().tvDiscovery.setText("开始连接");
                BlueConnectTask connectTask = new BlueConnectTask(item.getAddress());
                connectTask.setBlueConnectListener(this);
                connectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, device);
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED &&
                    item.getState() == DeviceAdapter.CONNECTED) {
                getBinding().tvDiscovery.setText("正在发送消息");
                InputDialogFragment dialog = InputDialogFragment.newInstance(
                        "", 0, "请输入要发送的消息");
                String fragTag = getResources().getString(R.string.app_name);
                dialog.show(getFragmentManager(), fragTag);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getBinding().tvDiscovery.setText("配对异常：" + e.getMessage());
        }
    }

    //向对方发送消息
    @Override
    public void onInput(String title, String message, int type) {
        Log.d(TAG, "onInput message=" + message);
        Log.d(TAG, "mBlueSocket is " + (mBlueSocket == null ? "null" : "not null"));
        if (mBlueSocket!=null) {
            BluetoothUtil.writeOutputStream(mBlueSocket, message);
        }
    }

    private BluetoothSocket mBlueSocket;

    //客户端主动连接
    @Override
    public void onBlueConnect(String address, BluetoothSocket socket) {
        if (socket==null){
            getBinding().tvDiscovery.setText("连接失败");
            return;
        }
        mBlueSocket = socket;
        getBinding().tvDiscovery.setText("连接成功");
        refreshAddress(address,socket);
    }
    //刷新已连接的状态
    private void refreshAddress(String address, BluetoothSocket socket) {
        for (int i = 0; i < mDeviceList.size(); i++) {
            BlueDevice item = mDeviceList.get(i);
            if (item.getAddress().equals(address) == true) {
                item.setState(DeviceAdapter.CONNECTED);
                item.setSocket(socket);
                mDeviceList.set(i, item);
            }else {
                if (item.getSocket()!=null){
                    try {
                        item.getSocket().close();
                        item.setSocket(null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                item.setState(item.getmDevice().getBondState() - 10);
                mDeviceList.set(i, item);
            }
        }
        adapter.notifyDataSetChanged();
    }


    //服务端侦听到连接
    @Override
    public void onBlueAccept(BluetoothSocket socket) {
        Log.d(TAG, "onBlueAccept socket is " + (socket == null ? "null" : "not null"));
        if (socket != null) {
            mBlueSocket = socket;
            BluetoothDevice device = mBlueSocket.getRemoteDevice();
            refreshAddress(device.getAddress(),socket);
            BlueReceiveTask receive = new BlueReceiveTask(socket, mHandler);
            receive.start();
        }
    }

    //收到对方发来的消息
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                byte[] readBuf = (byte[]) msg.obj;
                String readMessage = new String(readBuf, 0, msg.arg1);
                Log.d(TAG, "handleMessage readMessage=" + readMessage);
                AlertDialog.Builder builder = new AlertDialog.Builder(BluetoothActivity.this);
                builder.setTitle("我收到消息啦").setMessage(readMessage).setPositiveButton("确定", null);
                builder.create().show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.close();
    }
}

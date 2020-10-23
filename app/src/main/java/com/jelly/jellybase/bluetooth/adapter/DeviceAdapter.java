package com.jelly.jellybase.bluetooth.adapter;


import android.content.Context;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jelly.jellybase.R;
import com.jelly.jellybase.bluetooth.bean.BlueDevice;

import java.io.IOException;
import java.util.List;


public class DeviceAdapter extends BaseAdapter {

    private Context context;
    private List<BlueDevice> bleDeviceList;
    public static int CONNECTED = 3;//已连接
    public static int BINDING = 2;//已绑定
    private String[] mStateArray = {"绑定", "绑定中", "连接", "已连接"};
    public DeviceAdapter(Context context,List<BlueDevice> bleDeviceList) {
        this.context = context;
        this.bleDeviceList =bleDeviceList;
    }

    @Override
    public int getCount() {
        return bleDeviceList.size();
    }

    @Override
    public BlueDevice getItem(int position) {
        if (position > bleDeviceList.size())
            return null;
        return bleDeviceList.get(position);
    }
    public void close(){
        for (int i=0;i<bleDeviceList.size();i++){
            if (bleDeviceList.get(i).getSocket() != null) {
                try {
                    bleDeviceList.get(i).getSocket().close();
                    bleDeviceList.get(i).setSocket(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = View.inflate(context, R.layout.bluetooth_adapter_device, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.img_blue = (ImageView) convertView.findViewById(R.id.img_blue);
            holder.txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            holder.txt_mac = (TextView) convertView.findViewById(R.id.txt_mac);
            holder.txt_rssi = (TextView) convertView.findViewById(R.id.txt_rssi);
            holder.layout_idle = (LinearLayout) convertView.findViewById(R.id.layout_idle);
            holder.layout_connected = (LinearLayout) convertView.findViewById(R.id.layout_connected);
            holder.btn_disconnect = (Button) convertView.findViewById(R.id.btn_disconnect);
            holder.btn_connect = (Button) convertView.findViewById(R.id.btn_connect);
            holder.btn_detail = (Button) convertView.findViewById(R.id.btn_detail);
        }

        BlueDevice bleDevice = bleDeviceList.get(position);
        if (bleDevice != null) {
            boolean isConnected = bleDevice.getState() == CONNECTED;
            String name = bleDevice.getName();
            String mac = bleDevice.getAddress();
            holder.txt_name.setText(name);
            holder.txt_mac.setText(mac);
            holder.txt_rssi.setText(String.valueOf(bleDevice.getmRssi()));
            if (isConnected) {
                holder.img_blue.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext()
                        ,R.drawable.bluetooth_connected_24dp));
                holder.txt_name.setTextColor(0xFF3466FC);
                holder.txt_mac.setTextColor(0xFF3466FC);
                holder.layout_idle.setVisibility(View.GONE);
                holder.layout_connected.setVisibility(View.VISIBLE);
                //holder.btn_disconnect.setVisibility(View.GONE);
            } else {
                holder.img_blue.setImageDrawable(ContextCompat.getDrawable(context.getApplicationContext()
                        ,R.drawable.bluetooth_diconnect_24dp));
                holder.txt_name.setTextColor(0xFF000000);
                holder.txt_mac.setTextColor(0xFF000000);
                holder.layout_idle.setVisibility(View.VISIBLE);
                holder.layout_connected.setVisibility(View.GONE);
            }
            holder.btn_connect.setText(mStateArray[bleDevice.getState()]);
        }

        holder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    BlueDevice bleDevice=bleDeviceList.get(position);
                    mListener.onConnect(bleDevice,position);
                }
            }
        });

        holder.btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    BlueDevice bleDevice=bleDeviceList.get(position);
                    mListener.onDisConnect(bleDevice,position);
                }
            }
        });

        holder.btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    BlueDevice bleDevice=bleDeviceList.get(position);
                    mListener.onDetail(bleDevice,position);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        ImageView img_blue;
        TextView txt_name;
        TextView txt_mac;
        TextView txt_rssi;
        LinearLayout layout_idle;
        LinearLayout layout_connected;
        Button btn_disconnect;
        Button btn_connect;
        Button btn_detail;
    }

    public interface OnDeviceClickListener {
        void onConnect(BlueDevice bleDevice,int position);

        void onDisConnect(BlueDevice bleDevice,int position);

        void onDetail(BlueDevice bleDevice,int position);
    }

    private OnDeviceClickListener mListener;

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.mListener = listener;
    }

}

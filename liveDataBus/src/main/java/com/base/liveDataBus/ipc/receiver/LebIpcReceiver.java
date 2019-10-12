package com.base.liveDataBus.ipc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.base.liveDataBus.LiveDataBus;
import com.base.liveDataBus.ipc.IpcConst;
import com.base.liveDataBus.ipc.decode.DecodeException;
import com.base.liveDataBus.ipc.decode.IDecoder;
import com.base.liveDataBus.ipc.decode.ValueDecoder;
import com.base.liveDataBus.ipc.json.JsonConverter;


/**
 * Created by liaohailiang on 2019/3/26.
 */
public class LebIpcReceiver extends BroadcastReceiver {

    private IDecoder decoder;

    public LebIpcReceiver(JsonConverter jsonConverter) {
        this.decoder = new ValueDecoder(jsonConverter);
    }

    public void setJsonConverter(JsonConverter jsonConverter) {
        this.decoder = new ValueDecoder(jsonConverter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (IpcConst.ACTION.equals(intent.getAction())) {
            String key = intent.getStringExtra(IpcConst.KEY);
            try {
                Object value = decoder.decode(intent);
                if (key != null) {
                    LiveDataBus
                            .get(key)
                            .post(value);
                }
            } catch (DecodeException e) {
                e.printStackTrace();
            }
        }
    }
}
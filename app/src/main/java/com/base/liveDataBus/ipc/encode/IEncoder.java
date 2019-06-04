package com.base.liveDataBus.ipc.encode;

import android.content.Intent;

public interface IEncoder {

    void encode(Intent intent, Object value) throws EncodeException;
}
package com.base.liveDataBus.ipc.decode;

import android.content.Intent;

public interface IDecoder {

    Object decode(Intent intent) throws DecodeException;
}
package com.base.liveDataBus.core;


import android.content.Context;
import android.support.annotation.NonNull;

import com.base.liveDataBus.ipc.json.JsonConverter;
import com.base.liveDataBus.logger.Logger;


/**
 * Created by liaohailiang on 2019-08-28.
 */
public class Config {

    /**
     * lifecycleObserverAlwaysActive
     * set if then observer can always receive message
     * true: observer can always receive message
     * false: observer can only receive message when resumed
     *
     * @param active
     * @return
     */
    public Config lifecycleObserverAlwaysActive(boolean active) {
        LiveDataBusCore.get().setLifecycleObserverAlwaysActive(active);
        return this;
    }

    /**
     * @param clear
     * @return true: clear livedata when no observer observe it
     * false: not clear livedata unless app was killed
     */
    public Config autoClear(boolean clear) {
        LiveDataBusCore.get().setAutoClear(clear);
        return this;
    }

    /**
     * config broadcast
     * only if you called this method, you can use broadcastValue() to send broadcast message
     *
     * @param context
     * @return
     */
    public Config supportBroadcast(Context context) {
        LiveDataBusCore.get().registerReceiver(context);
        return this;
    }

    /**
     * setJsonConverter
     * default use gson as json converter
     *
     * @param jsonConverter
     * @return
     */
    public Config setJsonConverter(@NonNull JsonConverter jsonConverter) {
        LiveDataBusCore.get().setJsonConverter(jsonConverter);
        return this;
    }

    /**
     * setLogger, if not set, use DefaultLogger
     *
     * @param logger
     */
    public Config setLogger(@NonNull Logger logger) {
        LiveDataBusCore.get().setLogger(logger);
        return this;
    }
}
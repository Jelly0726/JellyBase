package com.base.liveDataBus;

import com.base.liveDataBus.core.Config;
import com.base.liveDataBus.core.LiveDataBusCore;
import com.base.liveDataBus.core.Observable;

/**
 *
 * 组件之间传递消息框架
 *

  在Application.onCreate方法中配置：

 LiveDataBus
          .config()
          ...

  supportBroadcast
  配置支持跨进程、跨APP通信，传入Context
  lifecycleObserverAlwaysActive
  配置LifecycleObserver（如Activity）接收消息的模式（默认值true）：
     true：整个生命周期（从onCreate到onDestroy）都可以实时收到消息
     false：激活状态（Started）可以实时收到消息，非激活状态（Stoped）无法实时收到消息，需等到Activity重新变成激活状态，方可收到消息
  autoClear
  配置在没有Observer关联的时候是否自动清除LiveEvent以释放内存（默认值false）
  setJsonConverter
  配置JsonConverter（默认使用gson）
  setLogger
  配置Logger（默认使用DefaultLogger）
 使用
 以生命周期感知模式订阅消息
 observe
 具有生命周期感知能力，LifecycleOwner销毁时自动取消订阅，不需要调用removeObserver

 LiveDataBus
 .get("key_name", String.class)
 .observe(this, new Observer<String>() {
@Override
public void onChanged(@Nullable String s) {
}
});
 以Forever模式订阅和取消订阅消息
 observeForever
 Forever模式订阅消息，需要调用removeObserver取消订阅

 LiveDataBus
 .get("key_name", String.class)
 .observeForever(observer);
 removeObserver
 取消订阅消息

 LiveDataBus
 .get("key_name", String.class)
 .removeObserver(observer);
 发送消息
 post
 发送一个消息，支持前台线程、后台线程发送

 LiveDataBus
 .get("key_name")
 .post(value);
 postDelay
 延迟发送一个消息，支持前台线程、后台线程发送

 LiveDataBus
 .get("key_name")
 .postDelay(value, 1000);
 跨进程、跨APP发送消息
 broadcast
 跨进程、跨APP发送消息，支持前台线程、后台线程发送。需要设置supportBroadcast

 LiveDataBus
 .get("key_name")
 .broadcast(value);
 以前台队列的形式发送跨进程消息

 LiveDataBus
 .get("key_name")
 .broadcast(value, true);
 Sticky模式
 支持在订阅消息的时候设置Sticky模式，这样订阅者可以接收到之前发送的消息。

 observeSticky
 以Sticky模式订阅消息，具有生命周期感知能力，LifecycleOwner销毁时自动取消订阅，不需要调用removeObserver

 LiveDataBus
 .get("sticky_key", String.class)
 .observeSticky(this, new Observer<String>() {
@Override
public void onChanged(@Nullable String s){
}
});
 observeStickyForever
 Forever模式订阅消息，需要调用removeObserver取消订阅，Sticky模式

 LiveDataBus
 .get("sticky_key", String.class)
 .observeStickyForever(observer);
  混淆
 -dontwarn com.base.liveDataBus.**
 -keep class com.base.liveDataBus.** { ; }
 -keep class android.arch.lifecycle.** { ; }
 -keep class android.arch.core.** { *; }

 for androidx:
 -dontwarn com.base.liveDataBus.**
 -keep class com.base.liveDataBus.** { ; }
 -keep class androidx.lifecycle.** { ; }
 -keep class androidx.arch.core.** { *; }
 */
public final class LiveDataBus {


    /**
     * get observable by key with type
     *
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Observable<T> get(String key, Class<T> type) {
        return LiveDataBusCore.get().with(key, type);
    }

    /**
     * get observable by key
     *
     * @param key
     * @return
     */
    public static Observable<Object> get(String key) {
        return get(key, Object.class);
    }

    /**
     * use the inner class Config to set params
     * first of all, call config to get the Config instance
     * then, call the method of Config to config LiveEventBus
     * call this method in Application.onCreate
     */
    public static Config config() {
        return LiveDataBusCore.get().config();
    }
}
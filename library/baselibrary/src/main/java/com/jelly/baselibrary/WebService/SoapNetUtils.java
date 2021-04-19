package com.jelly.baselibrary.WebService;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alibaba.fastjson.JSON;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * 处理Soap请求的工具类
 * Created by lipanquan on 2016/12/26.
 */
public class SoapNetUtils {

    /**
     * 请求处理成功的回调标示
     */
    private static final int MESSAGE_POST_SUCCESS = 0x1;

    /**
     * 请求处理失败的回调标示
     */
    private static final int MESSAGE_POST_FAIL = 0x2;

    /**
     * 命名空间
     */
    private static String namespace;

    /**
     * 服务器的地址
     */
    private static String url;

    private SoapNetUtils() {
    }

    private static SoapNetUtils instance = new SoapNetUtils();

    /**
     * 获取处理Soap请求的工具类
     *
     * @return 处理Soap请求的工具类
     */
    public static SoapNetUtils getInstance() {
        return instance;
    }

    /**
     * 初始化Soap协议请求工具类对象
     *
     * @param namespace 命名空间
     * @param url       服务器的地址
     */
    public void initSoap(String namespace, String url) {
        SoapNetUtils.namespace = namespace;
        SoapNetUtils.url = url;
        if (SoapNetUtils.namespace == null || SoapNetUtils.namespace.equals("") ||
                SoapNetUtils.url == null || SoapNetUtils.url.equals("")) {
            throw new RuntimeException("namespace不能为空，并且url不能为空。初始化Soap协议请求工具类对象失败");
        }
    }

    /**
     * 执行请求
     *
     * @param t          指定返回结果的class对象文件
     * @param urlType    请求的标示类型
     * @param parameters 参数列表
     * @param callback   回调函数
     * @param <T>        指定返回结果的class对象文件
     */
    public <T> void doSoapRequest(final Class<T> t, final String urlType, final Map<String, Object> parameters, final SoapCallback callback) {
        if (SoapNetUtils.namespace == null || SoapNetUtils.namespace.equals("") ||
                SoapNetUtils.url == null || SoapNetUtils.url.equals("")) {
            throw new RuntimeException("initSoap(String namespace, String url)方法进行初始化Soap协议请求工具类之后再使用...");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    T result = doSoap(t, urlType, parameters, callback);
                    //向主线程发送消息成功，getTelAddress函数执行完毕
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_POST_SUCCESS;
                    msg.obj = new MyExecuteTaskResult<T>(callback, result);
                    getHandler().sendMessage(msg);
                } catch (Exception e) {
                    //向主线程发送消息成功，getTelAddress函数执行完毕
                    Message msg = Message.obtain();
                    msg.what = MESSAGE_POST_FAIL;
                    msg.obj = new MyExecuteTaskResult<T>(callback, null);
                    getHandler().sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 正真执行网络请求
     *
     * @param t          指定返回结果的class对象文件
     * @param urlType    请求的标示类型
     * @param parameters 参数列表
     * @param callback   回调函数
     * @param <T>        指定返回结果的class对象文件
     */
    private <T> T doSoap(Class<T> t, String urlType, Map<String, Object> parameters, SoapCallback callback) throws Exception {
        Object instance = t.newInstance();
        SoapObject soapObject = new
                SoapObject(namespace, urlType);//"UserLogin"创建SOAP对象
        //设置属性，这些属性值通过SOAP协议传送给服务器
        Iterator<Map.Entry<String, Object>> it = parameters.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (entry.getValue() instanceof ArrayList) {
                SoapObject parameterValue = new SoapObject();
                for (String str : (ArrayList<String>)entry.getValue()) {
                    parameterValue.addProperty("long", Long.valueOf(str));
                }
                soapObject.addProperty(entry.getKey(), parameterValue);
            } else
                soapObject.addProperty(entry.getKey(), entry.getValue());
        }
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.bodyOut = soapObject;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(soapObject);
        HttpTransportSE httpTransportSE = new HttpTransportSE(url);
        try {
            //调用服务
            httpTransportSE.call(namespace + urlType, envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (envelope.bodyIn instanceof SoapObject) {
            SoapObject response = (SoapObject) envelope.bodyIn;
            Field[] fields = instance.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                Class<?> type = fields[i].getType();
                Object fieldValue;
                try {
                    fieldValue = response.getProperty(fields[i].getName());
                } catch (Exception e) {
                    continue;
                }
                Object o;
                if (fieldValue instanceof SoapPrimitive) {
                    o = JSON.parseObject(fieldValue.toString(), type);
                    fields[i].set(instance, o);
                } else if (fieldValue instanceof SoapObject) {
                    if (type == ArrayList.class) {
                        SoapObject object = (SoapObject) fieldValue;
                        o = new ArrayList();
                        Method m = o.getClass().getMethod("add", Object.class);
                        for (int j = 0; j < object.getPropertyCount(); j++) {
                            SoapPrimitive primitive = (SoapPrimitive) object.getProperty(j);
                            m.invoke(o, primitive.getValue());
                        }
                        fields[i].set(instance, o);
                    }
                    continue;
                } else {
                    continue;
                }
                fields[i].set(instance, o);
            }
        }
        return (T) instance;
    }
    private static InternalHandler sHandler;
    private static Handler getHandler() {
        synchronized (SoapCallback.class) {
            if (sHandler == null) {
                sHandler = new InternalHandler();
            }
            return sHandler;
        }
    }
    private static class MyExecuteTaskResult<Result> {
        @SuppressWarnings("rawtypes")
        final SoapCallback mTask;
        final Result mData;
        public MyExecuteTaskResult(SoapCallback task,
                                   Result data) {
            mTask = task;
            mData = data;
        }
    }
    private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            MyExecuteTaskResult<?> result = (MyExecuteTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_SUCCESS:
                    result.mTask.onResponseResult(result.mData);
                    break;
                case MESSAGE_POST_FAIL:
                    result.mTask.onFailResult();
                    break;
            }
            result.mTask.onFinally();
        }
    }
    public static final void main(String[] arg){
//        SoapNetUtils.getInstance().doSoapRequest(User.class, URL_TYPE_LOGIN, parameters, new SoapCallback<User>() {
//            @Override
//            public void onResponseResult(User user) {
//                if (user.isLoginSuccessed()) {
//                    user.setUserName(et_login_username.getText().toString());
//                    // 保存用户名和密码
//                    putString(SPKey.KEY_USERNAME, et_login_username.getText().toString().trim());
//                    if (isAutoLogin)
//                        putString(SPKey.KEY_PASSWORD, et_login_password.getText().toString().trim());
//                    getMyApplication().setCurrentUser(user);
//                    showToastShort(R.string.toast_login_success);
//                    startEnterActivity(SelectDeviceListActivity.class, true);
//                } else {
//                    showToastShort(R.string.toast_login_fail);
//                }
//            }
//
//            @Override
//            public void onFailResult() {
//                showToastShort(R.string.toast_login_fail);
//            }
//
//            @Override
//            public void onFinally() {
//                btn_login.setEnabled(true);
//                dismissMDialog();
//            }
//        });
    }
}
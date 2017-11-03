package com.base.okGo.utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.base.okGo.SafeHostnameVerifier;
import com.base.okGo.SafeTrustManager;
import com.jelly.jellybase.BuildConfig;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;


/**
 * Created by Administrator on 2017/6/14.
 */

public class OkGoUtils {
    //初始化OKGo
    public static void initOkGo(Application application) {
        //---------这里给出的是示例代码,告诉你可以这么传,实际使用的时候,根据需要传,不需要就不传-------------//
        HttpHeaders headers = new HttpHeaders();
//        headers.put("commonHeaderKey1", "commonHeaderValue1");    //header不支持中文，不允许有特殊字符
//        headers.put("commonHeaderKey2", "commonHeaderValue2");
        HttpParams params = new HttpParams();
//        params.put("commonParamsKey1", "commonParamsValue1");     //param支持中文,直接传,不要自己编码
//        params.put("commonParamsKey2", "这里支持中文参数");
        //----------------------------------------------------------------------------------------//

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        if(BuildConfig.LOG_DEBUG){
            //Debug，打印日志
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);        //log打印级别，决定了log显示的详细程度
        }else {
            //不打印日志
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.NONE);        //log打印级别，决定了log显示的详细程度
        }
        loggingInterceptor.setColorLevel(Level.INFO);                               //log颜色级别，决定了log在控制台显示的颜色
        builder.addInterceptor(loggingInterceptor);
        //builder.addInterceptor(new ChuckInterceptor(this));                       //第三方的开源库，使用通知显示当前请求的log

        //超时时间设置，默认60秒
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //全局的读取超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //全局的写入超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //全局的连接超时时间

        //自动管理cookie（或者叫session的保持），以下几种任选其一就行
        //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(application)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
        //builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保持cookie，app退出后，cookie消失

        //https相关设置，以下几种方案根据需要自己设置
        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        //方法二：自定义信任规则，校验服务端证书
        HttpsUtils.SSLParams sslParams2 = HttpsUtils.getSslSocketFactory(new SafeTrustManager());
        //方法三：使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("srca.cer"));
        //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
        //HttpsUtils.SSLParams sslParams4 = HttpsUtils.getSslSocketFactory(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"));
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
        builder.hostnameVerifier(new SafeHostnameVerifier());

        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(application)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //设置OkHttpClient
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
                .addCommonHeaders(headers)                      //全局公共头
                .addCommonParams(params);                       //全局公共参数
    }

    //获取签名字符串
    public static String getSign(Map<String, String> map, long timestamp, String uuid, String SignToken){
        map= sortMapByKey(map);
        StringBuffer stringBuffer=new StringBuffer("");
        stringBuffer
                .append("timestamp")
                .append("=")
                .append(timestamp+"")
                .append("&")
                .append("nonce")
                .append("=")
                .append(uuid)
                .append("&")
                .append("token")
                .append("=")
                .append(SignToken)
                .append("&");
        Iterator iterator=map.entrySet().iterator();
        while(iterator.hasNext()){
            LinkedHashMap.Entry entent= (LinkedHashMap.Entry) iterator.next();
            String key= (String) entent.getKey();
            String value= (String) entent.getValue();
            if(!TextUtils.isEmpty(value)) {
                stringBuffer
                        .append(key)
                        .append("=")
                        .append(value);
                if (iterator.hasNext()) {
                    stringBuffer.append("&");
                }
            }
        }
        if(stringBuffer.substring(stringBuffer.length()-1).equals("&")){
            stringBuffer.deleteCharAt(stringBuffer.length()-1);
        }
        // Log.i("msg","签名前="+stringBuffer.toString());
        Log.i("msg","签名前="+stringBuffer.toString());
        return Md5(stringBuffer.toString()).toUpperCase();
        //return MD5(stringBuffer.toString()).toUpperCase();
    }
    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<String, String>(
                new MapKeyComparator());

        sortMap.putAll(map);

        return sortMap;
    }
    //比较器类

    private static class MapKeyComparator implements Comparator<String> {
        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }
    /**
     * MD5加密算法
     * @param plainText
     * @return
     */
    public static String Md5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0) i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            return buf.toString();//32位的加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

    }
    /**
     * 异步获取外网时间戳
     * @param  getIPAsyncTaskBack 异步获取时间戳回调接口 在UI线程执行
     * @return
     */
    private static GetTimeStampAsyncTask getTimeStampAsyncTask;
    public static void getNetWorkTime(GetTimeStampAsyncTaskBack getTimeStampAsyncTaskBack){
        if(getTimeStampAsyncTask!=null){
            getTimeStampAsyncTask.cancel(true);
        }
        getTimeStampAsyncTask=null;
        getTimeStampAsyncTask=new GetTimeStampAsyncTask(getTimeStampAsyncTaskBack);
        getTimeStampAsyncTask.execute();
    }

    /**
     * 构造函数AsyncTask<Params, Progress, Result>参数说明:
     Params   启动任务执行的输入参数
     Progress 后台任务执行的进度
     Result   后台计算结果的类型
     */
    private static class GetTimeStampAsyncTask extends AsyncTask<String,Integer,String> {
        private GetTimeStampAsyncTaskBack getTimeStampAsyncTaskBack;
        public GetTimeStampAsyncTask(GetTimeStampAsyncTaskBack getTimeStampAsyncTaskBack){
            this.getTimeStampAsyncTaskBack=getTimeStampAsyncTaskBack;
        }
        final ExecutorService exec= Executors.newFixedThreadPool(3);
        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected String doInBackground(final String... params) {
            String timestamp="0";
            try {
                Callable<String> call=new Callable<String>(){
                    public String call() throws Exception {
                        URL url = null;//取得资源对象
                        long timestamp=0;
                        try {
                            url = new URL("http://www.baidu.com");
                            URLConnection uc = url.openConnection();//生成连接对象
                            uc.connect(); //发出连接
                            timestamp = uc.getDate(); //取得网站日期时间
                        } catch (Exception e) {
                            timestamp=0;
                            Log.e("提示", "网络连接异常，无法获取时间戳！e="+e.toString());
                        }
                        return timestamp+"";
                    }
                };
                // 开始任务执行服务
                Future<String> task=exec.submit(call);
                timestamp=task.get();
                // 停止任务执行服务
                exec.shutdown();
                return timestamp;
            }catch (Exception e){
                timestamp="0";
                Log.e("提示", "网络连接异常，无法获取时间戳！e="+e.toString());
            }
            return timestamp;
        }
        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行 在主线程中执行的操作
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(getTimeStampAsyncTaskBack!=null){
                getTimeStampAsyncTaskBack.GetTimeStamp(Long.parseLong(s));
            }
        }
        /**
         * 方法用于异步任务被取消时,在主线程中执行相关的操作
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        /**
         * 方法用于异步任务被取消时,在主线程中执行相关的操作
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    /**
     * 异步获取时间戳回调接口
     */
    public interface GetTimeStampAsyncTaskBack{
        public void GetTimeStamp(long timestamp);
    }

    /**
     * 异步获取外网ip
     * @param  type I ip格式 Z 中文格式 H 组合格式
     * @param  getIPAsyncTaskBack 异步获取IP回调接口 在UI线程执行
     * @return
     */
    private static GetIPAsyncTask getIPAsyncTask;
    public static void getNetIp(String type, GetIPAsyncTaskBack getIPAsyncTaskBack){
        if(getIPAsyncTask!=null){
            getIPAsyncTask.cancel(true);
        }
        getIPAsyncTask=null;
        getIPAsyncTask=new GetIPAsyncTask(getIPAsyncTaskBack);
        getIPAsyncTask.execute(type);
    }

    /**
     * 构造函数AsyncTask<Params, Progress, Result>参数说明:
     Params   启动任务执行的输入参数
     Progress 后台任务执行的进度
     Result   后台计算结果的类型
     */
    private static class GetIPAsyncTask extends AsyncTask<String,Integer,String> {
        private GetIPAsyncTaskBack getIPAsyncTaskBack;
        public GetIPAsyncTask(GetIPAsyncTaskBack getIPAsyncTaskBack){
            this.getIPAsyncTaskBack=getIPAsyncTaskBack;
        }
        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected String doInBackground(final String... params) {
            String IP = "127.0.0.1";
            try {
                final ExecutorService exec= Executors.newFixedThreadPool(3);
                Callable<String> call=new Callable<String>(){
                    public String call() throws Exception {
                        String IP = "127.0.0.1";
                        String address = "http://ip.chinaz.com/getip.aspx";
                        URL url = new URL(address);
                        HttpURLConnection connection = (HttpURLConnection) url
                                .openConnection();
                        connection.setUseCaches(false);
                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            String tmpString = "";
                            InputStream in = new BufferedInputStream(connection.getInputStream());
                            Scanner scanner = new Scanner(in).useDelimiter("\\A");
                            tmpString= scanner.hasNext() ? scanner.next() : "";
                            if (!tmpString.isEmpty()){
                                JSONObject jsonObject = new JSONObject(tmpString.toString());
                                if(params[0].equals("I")){
                                    IP = jsonObject.getString("ip");
                                }
                                if(params[0].equals("Z")){
                                    IP =  jsonObject.getString("address");
                                }
                                if(params[0].equals("H")) {
                                    IP = jsonObject.getString("ip") + "(" + jsonObject.getString("address")
                                            +  ")";
                                }
                                //Log.e("提示", "您的IP地址是：" + IP+" params="+params[0]);
                            }else {
                                IP = "127.0.0.1";
                                Log.e("提示", "网络连接异常，无法获取IP地址！");
                            }
                        } else {
                            IP = "127.0.0.1";
                            Log.e("提示", "网络连接异常，无法获取IP地址！");
                        }
                        return IP;
                    }
                };
                // 开始任务执行服务
                Future<String> task=exec.submit(call);
                IP=task.get();
                // 停止任务执行服务
                exec.shutdown();
                return IP;
            }catch (Exception e){
                IP = "127.0.0.1";
                Log.e("提示", "网络连接异常，无法获取IP地址！e="+e.toString());
            }
            return IP;
        }
        /**
         * 运行在UI线程中，在调用doInBackground()之前执行
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行 在主线程中执行的操作
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(getIPAsyncTaskBack!=null){
                getIPAsyncTaskBack.GetIP(s);
            }
        }
        /**
         * 方法用于异步任务被取消时,在主线程中执行相关的操作
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        /**
         * 方法用于异步任务被取消时,在主线程中执行相关的操作
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

    /**
     * 异步获取Ip回调接口
     */
    public interface GetIPAsyncTaskBack{
        public void GetIP(String IP);
    }
}

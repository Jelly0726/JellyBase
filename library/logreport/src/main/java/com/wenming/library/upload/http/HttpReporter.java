/*
 * @(#)CrashHttpReport.java		       Project: CrashHandler
 * Date: 2014-7-1
 *
 * Copyright (c) 2014 CFuture09, Institute of Software,
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wenming.library.upload.http;

import android.content.Context;
import android.util.Log;

import com.wenming.library.upload.BaseUpload;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;

/**
 * HTTP的post请求方式发送。
 */
public class HttpReporter extends BaseUpload {
    //    HttpClient httpclient = new DefaultHttpClient();
    private String url;
    private Map<String, String> otherParams;
    private String titleParam;
    private String bodyParam;
    private String fileParam;
    private String to;
    private String toParam;
    private HttpReportCallback callback;

    private static final int TIME_OUT = 8 * 1000;                          //超时时间
    private static final String CHARSET = "utf-8";                         //编码格式
    private static final String PREFIX = "--";                            //前缀
    private static final String BOUNDARY = UUID.randomUUID().toString();  //边界标识 随机生成
    private static final String CONTENT_TYPE = "multipart/form-data";     //内容类型
    private static final String LINE_END = "\r\n";

    public HttpReporter(Context context) {
        super(context);
    }

    @Override
    protected void sendReport(String title, String body, File file, OnUploadFinishedListener onUploadFinishedListener) {
//        SimpleMultipartEntity entity = new SimpleMultipartEntity();
//        entity.addPart(titleParam, title);
//        entity.addPart(bodyParam, body);
//        entity.addPart(toParam, to);
//        if (otherParams != null) {
//            for (Map.Entry<String, String> param : otherParams.entrySet()) {
//                entity.addPart(param.getKey(), param.getValue());
//            }
//        }
//        entity.addPart(fileParam, file, true);
        HttpURLConnection conn = null;
        try {
            URL urls = new URL(url);
            conn = (HttpURLConnection) urls.openConnection();
            conn.setRequestMethod("POST");
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);//Post 请求不能使用缓存
            //设置请求头参数
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE+";boundary=" + BOUNDARY);
            /**
             * 请求体
             */
            //上传参数
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            //getStrParams()为一个
            dos.writeBytes( getStrParams(otherParams).toString() );
            dos.flush();

            //文件上传
            StringBuilder fileSb = new StringBuilder();
            fileSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    /**
                     * 这里重点注意： name里面的值为服务端需要的key 只有这个key 才可以得到对应的文件
                     * filename是文件的名字，包含后缀名的 比如:abc.png
                     */
                    .append("Content-Disposition: form-data; name=\"file\"; filename=\""
                            + fileParam + "\"" + LINE_END)
                    .append("Content-Type: image/jpg" + LINE_END) //此处的ContentType不同于 请求头 中Content-Type
                    .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                    .append(LINE_END);// 参数头设置完以后需要两个换行，然后才是参数内容
            dos.writeBytes(fileSb.toString());
            dos.flush();
            InputStream is = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1){
                dos.write(buffer,0,len);
            }
            is.close();
            dos.writeBytes(LINE_END);

            //请求结束标志
            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + LINE_END);
            dos.flush();
            dos.close();
            Log.e("SSSS","postResponseCode() = "+conn.getResponseCode() );
            //读取服务器返回信息
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line = null;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                Log.e("SSSS", "run: " + response);
            }
//            HttpPost req = new HttpPost(url);
//            req.setEntity(entity);
//            HttpResponse resp = httpclient.execute(req);
//            int statusCode = resp.getStatusLine().getStatusCode();
//            String responseString = EntityUtils.toString(resp.getEntity());
            onUploadFinishedListener.onSuceess();
        } catch (Exception e) {
            onUploadFinishedListener.onError("Http send fail!!!!!!");
            e.printStackTrace();
        }finally {
            if (conn!=null){
                conn.disconnect();
            }
        }
    }
    /**
     * 对post参数进行编码处理
     * */
    private static StringBuilder getStrParams(Map<String,String> strParams){
        StringBuilder strSb = new StringBuilder();
        for (Map.Entry<String, String> entry : strParams.entrySet() ){
            strSb.append(PREFIX)
                    .append(BOUNDARY)
                    .append(LINE_END)
                    .append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_END)
                    .append("Content-Type: text/plain; charset=" + CHARSET + LINE_END)
                    .append("Content-Transfer-Encoding: 8bit" + LINE_END)
                    .append(LINE_END)// 参数头设置完以后需要两个换行，然后才是参数内容
                    .append(entry.getValue())
                    .append(LINE_END);
        }
        return strSb;
    }
    private boolean deleteLog(File file) {
        Log.d("HttpReporter", "delete: " + file.getName());
        return file.delete();
    }

    public String getUrl() {
        return url;
    }

    /**
     * @param url 发送请求的地址。
     */
    public HttpReporter setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getTitleParam() {
        return titleParam;
    }

    /**
     * @param titleParam 标题的参数名
     */
    public HttpReporter setTitleParam(String titleParam) {
        this.titleParam = titleParam;
        return this;
    }

    public String getBodyParam() {
        return bodyParam;
    }

    /**
     * @param bodyParam 内容的参数名
     */
    public HttpReporter setBodyParam(String bodyParam) {
        this.bodyParam = bodyParam;
        return this;
    }

    public String getFileParam() {
        return fileParam;
    }

    /**
     * @param fileParam 文件的参数名
     */
    public HttpReporter setFileParam(String fileParam) {
        this.fileParam = fileParam;
        return this;
    }

    public Map<String, String> getOtherParams() {
        return otherParams;
    }

    /**
     * 。
     *
     * @param otherParams 其他自定义的参数对（可不设置）
     */
    public void setOtherParams(Map<String, String> otherParams) {
        this.otherParams = otherParams;
    }

    public String getTo() {
        return to;
    }

    /**
     * @param to 收件人
     */
    public HttpReporter setTo(String to) {
        this.to = to;
        return this;
    }

    public HttpReportCallback getCallback() {
        return callback;
    }

    /**
     * @param callback 设置发送请求之后的回调接口。
     */
    public HttpReporter setCallback(HttpReportCallback callback) {
        this.callback = callback;
        return this;
    }

    public String getToParam() {
        return toParam;
    }

    /**
     * @param toParam 收件人参数名。
     */
    public HttpReporter setToParam(String toParam) {
        this.toParam = toParam;
        return this;
    }


    public interface HttpReportCallback {
        /**
         * 判断是否发送成功。它在发送日志的方法中被调用，如果成功，则日志文件会被删除。
         *
         * @param status  状态码
         * @param content 返回的内容。
         * @return 如果成功，则日志文件会被删除,返回true
         */
        public boolean isSuccess(int status, String content);
    }
}

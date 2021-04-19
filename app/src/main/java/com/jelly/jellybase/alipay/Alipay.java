package com.jelly.jellybase.alipay;

/**
 * Created by Administrator on 2016/6/6.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.jelly.baselibrary.alipay.PayResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 支付宝支付
 * @author Administrator
 *
 */
public class Alipay{
    private Alipay(){};
    private static Alipay alipay=null;
    private static ProgressDialog progressDialog=null;
    public static Alipay getInstance(){
        if(alipay==null){
            alipay=new Alipay();
        }
        return alipay;
    }
    private Activity activity=null;//activity  对象
    private AliPayResultListener aliListener;
    private static String res="";
    private static OrderInfo order;
    /**
     * 确认支付宝充值
     * @param activitys  上下文Activity
     * @param zyListener  设置回调
     */
    public void alipay(Activity activitys, OrderMsgUtil orderMsgUtil
            , AliPayResultListener zyListener
    ){
        activity=activitys;
        this.aliListener=zyListener;
        progressDialog=new ProgressDialog(activitys);
        progressDialog.setTitle("进度提示-商户提示框");
        progressDialog.setMessage("正在支付.....");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        if(orderMsgUtil.getType()==0) {//消费
            PostExpense get = PostExpense.getInstance(activity);
            get.setCallBackListener(backListener);
            get.postRecharge(orderMsgUtil.getPlaycode(),
                    orderMsgUtil.getExOrderNo(),
                    orderMsgUtil.getMhtOrderDetail(), orderMsgUtil.getPrice(),
                    orderMsgUtil.getAgentSign(), zyListener);
        }else{//充值
            PostRecharge get = PostRecharge.getInstance(activity);
            get.setCallBackListener(backListener);
            get.postRecharge(orderMsgUtil.getPlaycode(),
                    orderMsgUtil.getMhtOrderDetail(), orderMsgUtil.getPrice(),
                    orderMsgUtil.getAgentSign(), zyListener);
        }
    }
    /**
     * call alipay sdk pay. 调用SDK支付
     */
    private void pay() {
        // 订单
        final String orderInfo = order.getIntoSign();
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(activity);
                // 调用支付接口，获取支付结果
                //String result = alipay.pay(orderInfo,true);
                Map<String, String> result= alipay.payV2(orderInfo, true);
                PayResult payResult = new PayResult(result);
                //返回此次支付结果及加签，建议对支付签名信息拿签约时的公钥做验签
                String resultInfo = payResult.getResult();
                String resultStatus = payResult.getResultStatus();
                // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                if (TextUtils.equals(resultStatus, "9000")) {
                    if(aliListener!=null){
                        aliListener.onSucceed("支付成功!");
                    }
                } else {
                    // 判断resultStatus 为非“9000”则代表可能支付失败
                    // “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                    if (TextUtils.equals(resultStatus, "8000")) {

                    } else {
                        String ss="未知!";
                        // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                        if (TextUtils.equals(resultStatus, "6001")) {
                            ss="用户中途取消！";

                        }
                        if(TextUtils.equals(resultStatus, "6002")){
                            ss="网络连接出错！";
                        }
                        if(aliListener!=null){
                            aliListener.onFailure(01,"交易状态:支付失败!"
                                    +"\n"+"状态码:"+resultStatus+"原因:"+ss);
                        }
                    }
                }
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    private BackListener backListener=new BackListener(){
        @Override
        public Object parseNetworkResponse(Response response, int id,String s) {
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            try{
                JSONObject jsonObject =new JSONObject(s);
                String status = jsonObject.getString("status");
                String message = jsonObject.getString("msg");
                if (status.equals("1")) {
                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject json=jsonArray.getJSONObject(i);
                        order=new OrderInfo();
                        String ZhengyouOrderNo=json.getString("code");
                        String intoSign=json.getString("payInfo");
                        order.setIntoSign(intoSign);
                        order.setZhengyouOrderNo(ZhengyouOrderNo);
                        //Log.i("WF","intoSign="+intoSign);
                    }
                    pay();
                }else{
                    res=message;
                    if(aliListener!=null){
                        aliListener.onFailure(01,"alipay:"+ res);
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
                if(response.isSuccessful()){
                    res=e.toString();
                }else{
                    res="返回码："+response.code()+"  message："+response.message();
                }
                if(aliListener!=null){
                    aliListener.onFailure(01,"alipay:"+res);
                }
            }
            return null;
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            //alipaylisten.alipayResult(FAILURE,"PostExpense:t:"+request.url());
            if(progressDialog!=null){
                progressDialog.dismiss();
            }
            if(call!=null){
                res="alipay:"+call.request().url();
            }else{
                if(e!=null){
                    res="alipay:"+e.toString();
                }else{
                    res="alipay:未知异常！";
                }
            }

            if(aliListener!=null){
                aliListener.onFailure(01, res);
            }
        }

        @Override
        public void onResponse(Object response, int id) {

        }
    };

}


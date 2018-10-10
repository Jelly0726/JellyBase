package com.jelly.jellybase.alipay;

import android.content.Context;

import com.base.encrypt.SafetyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 充值
 * 封装 网络请求使用okhttp
 * Created by Administrator on 2015/12/28.
 */
public class PostRecharge {
	private static BackListener callBackListener;///回调监听
	private AliPayResultListener aliListener;
	private static PostRecharge postExpense =null;
	private static Context context;
	private PostRecharge() {
	}
	public static PostRecharge getInstance(Context context){
		PostRecharge.context=context;
		if(postExpense ==null){
			postExpense =new PostRecharge();
		}
		return postExpense;
	}

	/**
	 * 设置回调监听
	 * @param callBackListeners
	 */
	public void setCallBackListener(BackListener callBackListeners){
		callBackListener=callBackListeners;
	}
	/**
	 * 
	 * @param playcode  用户账号
	 * @param subject     交易的商品名称
	 * @param price       交易的金额
	 * @param agentSign  交易签名(由游戏商服务器生成的唯一签名，用于服务器异步通知时的验签)
	 */
	public void postRecharge(final String playcode, String subject
			, String price, String agentSign, AliPayResultListener aliListener){
		this.aliListener=aliListener;
		try{
			Map<String, String> map=new HashMap<String, String>();
			map.put("memberid",playcode);
			map.put("subject",subject);
			map.put("amount", price);
			String sign= SafetyUtil.getInstance().getSign(map,1);
			map.put("sign", sign);
		}catch(Exception e){
			if(aliListener!=null){
				aliListener.onFailure(04, "e="+e.toString());
			}
			if(callBackListener!=null) {
				callBackListener.onError(null,e,04);
			}
		}
		
	}
}
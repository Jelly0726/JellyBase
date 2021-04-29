package com.jelly.jellybase.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.config.BaseConfig;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.WeixinpayActivityBinding;
import com.jelly.jellybase.weixinpay.PayUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**微信支付回调
 * Created by BYPC006 on 2016/8/24.
 */
public class WXPayEntryActivity extends BaseActivity<WeixinpayActivityBinding> implements IWXAPIEventHandler {
	private IWXAPI mApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		initView();
		mApi = WXAPIFactory.createWXAPI(this, BaseConfig.WechatPay_APP_ID);
		mApi.handleIntent(getIntent(), this);
	}
	private void initView(){
		getViewBinding().leftBack.setOnClickListener(mListener);

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mApi.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq baseReq) {
	}

	@Override
	public void onResp(final BaseResp baseResp) {
		int result = 0;
		Log.i("WXEntryActivity", "baseResp.getType()="+baseResp.getType());
		if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){

			switch (baseResp.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					if (baseResp.errCode == BaseResp.ErrCode.ERR_OK){
						// 支付成功,发送广播给下单页面,通知支付成功
						Intent intent=new Intent();
						intent.setAction(PayUtil.WX_RECHARGE_SUCCESS);
						sendBroadcast(intent);
					}
					finish();
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					result = R.string.errcode_cancel;
					ToastUtils.showToast(this,result);
					finish();
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					result = R.string.errcode_deny;
					ToastUtils.showToast(this,result);
					finish();
					break;
				default:
					result = R.string.errcode_unknown;
					ToastUtils.showToast(this,result);
					finish();
					break;
			}

		}
	}
	private View.OnClickListener mListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()){
				case R.id.left_back:
					finish();
					break;
				default:
					break;
			}
		}
	};
}

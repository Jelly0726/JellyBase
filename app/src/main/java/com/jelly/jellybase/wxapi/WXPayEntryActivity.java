package com.jelly.jellybase.wxapi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.base.config.BaseConfig;
import com.base.toast.ToastUtils;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;
import com.jelly.jellybase.weixinpay.PayUtil;
import com.mylhyl.circledialog.CircleDialog;
import com.mylhyl.circledialog.callback.ConfigDialog;
import com.mylhyl.circledialog.callback.ConfigText;
import com.mylhyl.circledialog.params.DialogParams;
import com.mylhyl.circledialog.params.TextParams;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


/**微信支付回调
 * Created by BYPC006 on 2016/8/24.
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
	private IWXAPI mApi;
	private LinearLayout left_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		initView();
		mApi = WXAPIFactory.createWXAPI(this, BaseConfig.WechatPay_APP_ID);
		mApi.handleIntent(getIntent(), this);
	}
	@Override
	public int getLayoutId(){
		return R.layout.weixinpay_activity;
	}
	private void initView(){
		left_back = (LinearLayout) findViewById(R.id.left_back);
		left_back.setOnClickListener(mListener);

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
					result = R.string.errcode_success;
					new CircleDialog.Builder()
							.configDialog(new ConfigDialog() {
								@Override
								public void onConfig(DialogParams params) {
									params.width = 0.6f;
								}
							})
							.setCanceledOnTouchOutside(false)
							.setCancelable(false)
							.setTitle(getString(R.string.wx_pay_notify))
							.configText(new ConfigText() {
								@Override
								public void onConfig(TextParams params) {
									params.gravity = Gravity.CENTER;
									params.textColor = Color.parseColor("#FF1F50F1");
									params.padding = new int[]{20, 0, 20, 0};
								}
							})
							.setText(getString(result))
							.setPositive(getString(R.string.wx_pay_ok), new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if (baseResp.errCode == BaseResp.ErrCode.ERR_OK){
										// 支付成功,发送广播给下单页面,通知支付成功
										Intent intent=new Intent();
										intent.setAction(PayUtil.WX_RECHARGE_SUCCESS);
										sendBroadcast(intent);
									}
									finish();
								}
							}).show(getSupportFragmentManager());
					break;
				case BaseResp.ErrCode.ERR_USER_CANCEL:
					result = R.string.errcode_cancel;
					ToastUtils.showToast(this,result);
					finish(2000);
					break;
				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					result = R.string.errcode_deny;
					ToastUtils.showToast(this,result);
					finish(2000);
					break;
				default:
					result = R.string.errcode_unknown;
					ToastUtils.showToast(this,result);
					finish(2000);
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

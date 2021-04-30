package com.jelly.baselibrary.redpacket;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.R;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.databinding.RedPacketStartActivityBinding;
import com.jelly.baselibrary.redpacket.service.NotificationService;
import com.jelly.baselibrary.redpacket.service.RedPacketService;
import com.jelly.baselibrary.redpacket.util.EventBusMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 自动抢红包首页
 */
public class StartActivity extends BaseActivity<RedPacketStartActivityBinding>
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    boolean changeByUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        getBinding().switchService.setOnCheckedChangeListener(this);
        getBinding().switchNotification.setOnCheckedChangeListener(this);
        getBinding().switchWechat.setOnCheckedChangeListener(this);
        getBinding().switchQq.setOnCheckedChangeListener(this);
        getBinding().leftBack.setOnClickListener(this);
        getBinding().tvGuide.setOnClickListener(this);
    }
    public void onClick(View view){
        if (view.getId() == R.id.left_back) {
            finish();
        }else if (view.getId()==R.id.tv_guide){
            Intent itGuide = new Intent(getApplicationContext(), GuideActivity.class);
            startActivity(itGuide);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (RedPacketService.isRunning()) {
            getBinding().switchService.setChecked(true);
        }else {
            getBinding().switchService.setChecked(false);
        }
        boolean enable = Config.getConfig(this).isEnableNotificationService();
        boolean running = RedPacketService.isNotificationServiceRunning();
        if (enable && running && !getBinding().switchNotification.isChecked()) {
            changeByUser = false;
            getBinding().switchNotification.setChecked(true);
        } else if((!enable || !running) && getBinding().switchNotification.isChecked()) {
            changeByUser = false;
            getBinding().switchNotification.setChecked(false);
        }
        updateCount();
        updateEnableStatus();
    }

    private void updateCount() {
        int wechatSum = AppPrefs.getInt(this, Config.KEY_WECHAT_SUM);
        int qqSum = AppPrefs.getInt(this, Config.KEY_QQ_SUM);
        getBinding().tvWechatCout.setText("微信红包："+wechatSum+"个");
        getBinding().tvQqCout.setText("QQ红包："+qqSum+"个");
        getBinding().tvTotalCount.setText(wechatSum+qqSum+"个红包");
    }

    private void updateEnableStatus() {
        getBinding().switchWechat.setChecked(AppPrefs.getBoolean(this, Config.KEY_WECHAT_ENABLE));
        getBinding().switchQq.setChecked(AppPrefs.getBoolean(this, Config.KEY_QQ_ENABLE));
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
        int id = compoundButton.getId();
        if (id == R.id.switch_service) {
            if (!changeByUser) {
                changeByUser = true;
                return;
            }
            if (enable && !RedPacketService.isRunning()) {
                openAccessibilityServiceSettings();
            }
        } else if (id == R.id.switch_notification) {
            if (!changeByUser) {
                changeByUser = true;
                return;
            }
            if (enable && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Toast.makeText(StartActivity.this, "该功能只支持安卓4.3以上的系统", Toast.LENGTH_SHORT).show();
                return;
            }
            Config.getConfig(this).setNotificationServiceEnable(enable);
            if (enable && !NotificationService.isRunning())
                openNotificationServiceSettings();
        } else if (id == R.id.switch_wechat) {
            AppPrefs.putBoolean(this, Config.KEY_WECHAT_ENABLE, enable);
        } else if (id == R.id.switch_qq) {
            if (!changeByUser) {
                changeByUser = true;
                return;
            }
            if (enable) {
                changeByUser = false;
                getBinding().switchQq.setChecked(false);
                Toast.makeText(this, "暂未开通，敬请期待", Toast.LENGTH_SHORT).show();
            }
//                SharedPreferencesUtil.saveBoolean(this, Config.KEY_QQ_ENABLE, enable);
        }
    }


    /** 打开辅助服务的设置*/
    public void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, R.string.notification_setting_tips, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 打开通知栏设置*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public void openNotificationServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, R.string.notification_setting_tips, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onEventMainThread(EventBusMsg msg) {
        switch (msg.getType()) {
            case EventBusMsg.ACCESSIBILITY_CONNECTED:
                Toast.makeText(getApplicationContext(), "已成功连接服务", Toast.LENGTH_SHORT).show();
                changeByUser = false;
                getBinding().switchService.setChecked(true);
                break;
            case EventBusMsg.ACCESSIBILITY_DISCONNECTED:
                Toast.makeText(getApplicationContext(), "已断开连接", Toast.LENGTH_SHORT).show();
                changeByUser = false;
                getBinding().switchService.setChecked(false);
                break;
            case EventBusMsg.NOTIFICATION_CONNECTED:
                Toast.makeText(getApplicationContext(), "正在监听通知栏", Toast.LENGTH_SHORT).show();
                changeByUser = false;
                getBinding().switchNotification.setChecked(true);
                break;
            case EventBusMsg.NOTIFICATION_DISCONNECTED:
                Toast.makeText(getApplicationContext(), "已停止监听通知栏", Toast.LENGTH_SHORT).show();
                changeByUser = false;
                getBinding().switchNotification.setChecked(false);
                break;
            default:
                break;
        }
    }
}

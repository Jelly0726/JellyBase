package com.jelly.baselibrary.redpacket;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.jelly.baselibrary.R;
import com.jelly.baselibrary.R2;
import com.jelly.baselibrary.applicationUtil.AppPrefs;
import com.jelly.baselibrary.redpacket.service.NotificationService;
import com.jelly.baselibrary.redpacket.service.RedPacketService;
import com.jelly.baselibrary.redpacket.util.EventBusMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自动抢红包首页
 */
public class StartActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    @BindView(R2.id.left_back)
    LinearLayout left_back;
    @BindView(R2.id.tv_total_count)
    TextView tvTotalCount;
    @BindView(R2.id.tv_wechat_cout)
    TextView tvWechatCout;
    @BindView(R2.id.tv_qq_cout)
    TextView tvQqCout;
    @BindView(R2.id.switch_service)
    SwitchCompat switchService;
    @BindView(R2.id.switch_notification)
    SwitchCompat switchNotification;
    @BindView(R2.id.tv_guide)
    TextView tvGuide;
    @BindView(R2.id.switch_wechat)
    SwitchCompat switchWechat;
    @BindView(R2.id.switch_qq)
    SwitchCompat switchQq;

    boolean changeByUser = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.red_packet_start_activity);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        switchService.setOnCheckedChangeListener(this);
        switchNotification.setOnCheckedChangeListener(this);
        switchWechat.setOnCheckedChangeListener(this);
        switchQq.setOnCheckedChangeListener(this);
    }
    @OnClick({R2.id.left_back})
    public void onClick(View view){
        switch (view.getId()){
            case R2.id.left_back:
                finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (RedPacketService.isRunning()) {
            switchService.setChecked(true);
        }else {
            switchService.setChecked(false);
        }
        boolean enable = Config.getConfig(this).isEnableNotificationService();
        boolean running = RedPacketService.isNotificationServiceRunning();
        if (enable && running && !switchNotification.isChecked()) {
            changeByUser = false;
            switchNotification.setChecked(true);
        } else if((!enable || !running) && switchNotification.isChecked()) {
            changeByUser = false;
            switchNotification.setChecked(false);
        }
        updateCount();
        updateEnableStatus();
    }

    private void updateCount() {
        int wechatSum = AppPrefs.getInt(this, Config.KEY_WECHAT_SUM);
        int qqSum = AppPrefs.getInt(this, Config.KEY_QQ_SUM);
        tvWechatCout.setText("微信红包："+wechatSum+"个");
        tvQqCout.setText("QQ红包："+qqSum+"个");
        tvTotalCount.setText(wechatSum+qqSum+"个红包");
    }

    private void updateEnableStatus() {
        switchWechat.setChecked(AppPrefs.getBoolean(this, Config.KEY_WECHAT_ENABLE));
        switchQq.setChecked(AppPrefs.getBoolean(this, Config.KEY_QQ_ENABLE));
    }

    @OnClick(R2.id.tv_guide)
    public void onClick() {
        Intent itGuide = new Intent(getApplicationContext(), GuideActivity.class);
        startActivity(itGuide);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean enable) {
        switch (compoundButton.getId()) {
            case R2.id.switch_service:
                if (!changeByUser) {
                    changeByUser = true;
                    return;
                }
                if (enable && !RedPacketService.isRunning()) {
                    openAccessibilityServiceSettings();
                }
                break;
            case R2.id.switch_notification:
                if (!changeByUser) {
                    changeByUser = true;
                    return;
                }
                if(enable && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Toast.makeText(StartActivity.this, "该功能只支持安卓4.3以上的系统", Toast.LENGTH_SHORT).show();
                    return;
                }
                Config.getConfig(this).setNotificationServiceEnable(enable);
                if (enable && !NotificationService.isRunning())
                    openNotificationServiceSettings();
                break;
            case R2.id.switch_wechat:
                AppPrefs.putBoolean(this, Config.KEY_WECHAT_ENABLE, enable);
                break;
            case R2.id.switch_qq:
                if (!changeByUser) {
                    changeByUser = true;
                    return;
                }
                if (enable) {
                    changeByUser = false;
                    switchQq.setChecked(false);
                    Toast.makeText(this, "暂未开通，敬请期待", Toast.LENGTH_SHORT).show();
                }
//                SharedPreferencesUtil.saveBoolean(this, Config.KEY_QQ_ENABLE, enable);
                break;
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
                switchService.setChecked(true);
                break;
            case EventBusMsg.ACCESSIBILITY_DISCONNECTED:
                Toast.makeText(getApplicationContext(), "已断开连接", Toast.LENGTH_SHORT).show();
                changeByUser = false;
                switchService.setChecked(false);
                break;
            case EventBusMsg.NOTIFICATION_CONNECTED:
                Toast.makeText(getApplicationContext(), "正在监听通知栏", Toast.LENGTH_SHORT).show();
                changeByUser = false;
                switchNotification.setChecked(true);
                break;
            case EventBusMsg.NOTIFICATION_DISCONNECTED:
                Toast.makeText(getApplicationContext(), "已停止监听通知栏", Toast.LENGTH_SHORT).show();
                changeByUser = false;
                switchNotification.setChecked(false);
                break;
            default:
                break;
        }
    }
}

package com.jelly.jellybase.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.Utils.ToastUtils;
import com.base.multiClick.AntiShake;
import com.base.view.BaseActivity;
import com.jelly.jellybase.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CopyActivity extends BaseActivity{
    @BindView(R.id.left_back)
    LinearLayout left_back;

    @BindView(R.id.pintText_tv)
    TextView pintText_tv;
    @BindView(R.id.pintCopy_btn)
    Button pintCopy_btn;
    @BindView(R.id.pintText_ed)
    EditText pintText_ed;
    @BindView(R.id.pintPaste_btn)
    Button pintPaste_btn;

    @BindView(R.id.netText_tv)
    TextView netText_tv;
    @BindView(R.id.netCopy_btn)
    Button netCopy_btn;
    @BindView(R.id.netText_ed)
    EditText netText_ed;
    @BindView(R.id.netPaste_btn)
    Button netPaste_btn;

    @BindView(R.id.intentText_btn)
    Button intentText_btn;
    @BindView(R.id.intentPaste_btn)
    Button intentPaste_btn;

    private ClipboardManager mClipboardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.copy_activity);
        ButterKnife.bind(this);
        iniView();
    }
    private void iniView(){
        mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
    }
    @OnClick({R.id.left_back,R.id.pintCopy_btn,R.id.pintPaste_btn,R.id.netCopy_btn,R.id.netPaste_btn,
    R.id.intentText_btn,R.id.intentPaste_btn})
    public void onClick(View v) {
        if (AntiShake.check(v.getId()))return;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.pintCopy_btn:
                String ssss=pintText_tv.getText().toString();
                ClipData mClipData =ClipData.newPlainText("Label", ssss);         //‘Label’这是任意文字标签
                mClipboardManager.setPrimaryClip(mClipData);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.pintPaste_btn:
                // 粘贴板有数据，并且是文本
                if (mClipboardManager.hasPrimaryClip()
                        && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                    CharSequence text = item.getText();
                    if (text == null) {
                        return;
                    }
                    pintText_ed.setText(text);
                    pintText_ed.setSelection(text.length());
                }
                break;
            case R.id.netCopy_btn:
                /**
                 *
                 Uri uri = Uri.parse(uriStr);
                 ClipData clipData = ClipData.newUri(getContentResolver(), "copy from demo", uri);
                 mClipboardManager.setPrimaryClip(clipData);
                 */
                String netCopy=netText_tv.getText().toString();
                ClipData clipData= ClipData.newRawUri("Label", Uri.parse(netCopy));
                mClipboardManager.setPrimaryClip(clipData);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.netPaste_btn:
                if (mClipboardManager.hasPrimaryClip()
                        && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_URILIST)) {
                    ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                    Uri uri = item.getUri();
                    if (uri == null) {
                        return;
                    }
                    netText_ed.setText(uri.toString());
                    netText_ed.setSelection(uri.toString().length());
                }
                break;
            case R.id.intentText_btn:
                Intent intent = new Intent(this, CopyActivity.class);
                ClipData clipDatas = ClipData.newIntent("Label", intent);
                mClipboardManager.setPrimaryClip(clipDatas);
                ToastUtils.showShort(this, "复制成功，可以发给朋友们了。");
                break;
            case R.id.intentPaste_btn:
                if (mClipboardManager.hasPrimaryClip()
                        && mClipboardManager.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT)) {
                    ClipData.Item item = mClipboardManager.getPrimaryClip().getItemAt(0);
                    Intent intent1 = item.getIntent();
                    if (intent1 == null) {
                        return;
                    }
                    startActivity(intent1);
                }
                break;
        }
    }
}

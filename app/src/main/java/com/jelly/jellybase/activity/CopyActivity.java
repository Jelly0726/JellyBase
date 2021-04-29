package com.jelly.jellybase.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.jelly.baselibrary.BaseActivity;
import com.jelly.baselibrary.multiClick.AntiShake;
import com.jelly.baselibrary.toast.ToastUtils;
import com.jelly.jellybase.R;
import com.jelly.jellybase.databinding.CopyActivityBinding;

public class CopyActivity extends BaseActivity<CopyActivityBinding> implements View.OnClickListener {
    private ClipboardManager mClipboardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniView();
    }
    private void iniView(){
        mClipboardManager =(ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        getViewBinding().leftBack.setOnClickListener(this);
        getViewBinding().pintCopyBtn.setOnClickListener(this);
        getViewBinding().pintPasteBtn.setOnClickListener(this);
        getViewBinding().netCopyBtn.setOnClickListener(this);
        getViewBinding().netPasteBtn.setOnClickListener(this);
        getViewBinding().intentTextBtn.setOnClickListener(this);
        getViewBinding().intentPasteBtn.setOnClickListener(this);
    }
    public void onClick(View v) {
        if (AntiShake.check(v.getId()))return;
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
            case R.id.pintCopy_btn:
                String ssss=getViewBinding().pintTextTv.getText().toString();
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
                    getViewBinding().pintTextEd.setText(text);
                    getViewBinding().pintTextEd.setSelection(text.length());
                }
                break;
            case R.id.netCopy_btn:
                /**
                 *
                 Uri uri = Uri.parse(uriStr);
                 ClipData clipData = ClipData.newUri(getContentResolver(), "copy from demo", uri);
                 mClipboardManager.setPrimaryClip(clipData);
                 */
                String netCopy=getViewBinding().netTextTv.getText().toString();
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
                    getViewBinding().netTextEd.setText(uri.toString());
                    getViewBinding().netTextEd.setSelection(uri.toString().length());
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

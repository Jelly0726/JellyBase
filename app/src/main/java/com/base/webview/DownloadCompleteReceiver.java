package com.base.webview;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.base.log.DebugLog;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * 文件下载成功广播接受者
 * 那怎么知道文件下载成功呢？系统在下载完成后会发送一条广播，里面有任务 ID，告诉调用者任务完成，
 * 通过 DownloadManager 获取到文件信息就可以进一步处理。
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DebugLog.i("onReceive. intent:{}", intent != null ? intent.toUri(0) : null);
        if (intent != null) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                DebugLog.i("downloadId:{}", downloadId+"");
                DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
                DebugLog.i("getMimeTypeForDownloadedFile:{}", type);
                if (TextUtils.isEmpty(type)) {
                    type = "*/*";
                }
                Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                DebugLog.i("UriForDownloadedFile:{}", uri+"");
                if (uri != null) {
                    Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                    handlerIntent.setDataAndType(uri, type);
                    context.startActivity(handlerIntent);
                }
            }
        }
    }
}

package com.rex.richeditor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rex.editor.common.DownloadTask;
import com.rex.editor.view.RichEditor;
import com.rex.editor.view.RichEditorNew;

import static com.rex.richeditor.MainActivity.verifyStoragePermissions;

/**
 * @author Rex
 * 展示部分
 */
public class ShowHtmlActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_html);

        Toast.makeText(this, "以上为模拟在线图片效果", Toast.LENGTH_SHORT).show();
        //下载权限需要
        verifyStoragePermissions(this);
        String html = getIntent().getStringExtra("html");

        Log.i("rex", "html：" + html);
        boolean isPublish = getIntent().getBooleanExtra("isPublish", true);
        RichEditorNew richEditor = findViewById(R.id.richEditor);
        TextView tvHtmlCode = findViewById(R.id.tvHtmlCode);
        if (isPublish) {

            tvHtmlCode.setVisibility(View.GONE);
            richEditor.setVisibility(View.VISIBLE);
            //为图片加上点击事件 Js
            //不可编辑 预览模式
            richEditor.loadRichEditorCode(html);
            richEditor.setOnClickImageTagListener(new RichEditor.OnClickImageTagListener() {
                @Override
                public void onClick(String url) {
                    Toast.makeText(ShowHtmlActivity.this, "url:" + url, Toast.LENGTH_LONG).show();
                }
            });

            richEditor.setDownloadListener(DownloadTask.getDefaultDownloadListener(this));
        } else {
            tvHtmlCode.setText(html);
            tvHtmlCode.setVisibility(View.VISIBLE);
            richEditor.setVisibility(View.GONE);
        }


    }
}

package com.jelly.jellybase.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.applicationUtil.MyApplication;
import com.jelly.jellybase.R;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.ig.DefaultImageDownloader;

/**
 * Created by Administrator on 2017/12/18.
 */

public class ResolveHtmlActivity extends AppCompatActivity implements View.OnClickListener{
    private LinearLayout left_back;
    private TextView productDetail_tv;

    @Override
    public void onDestroy() {
        // activity onDestory时
        RichText.clear(this);
        //在应用退出时调用
        RichText.recycle();
        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resolve_html_activity);

        left_back= (LinearLayout) findViewById(R.id.left_back);
        left_back.setOnClickListener(this);

        productDetail_tv=findViewById(R.id.productDetail_tv);
        String html="<h1>百度一下,你就知道官</h1>"
                + "全球最大的中文搜索引擎、致力于让网民更便捷地获取信息，找到所求。百度超过千亿的中文网页数据库，" +
                "可以瞬间找到相关的搜索结果。"
                + "<img src='https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000" +
                "&sec=1513612039447&di=2b72984467a03a7cfb0bb40ffe008388&imgtype=0" +
                "&src=http%3A%2F%2Foss.laohucaijing.com%2FUserFiles%2FImage%2F201603%2F20160330111155221.png' />";
        // 设置为Html
        RichText.initCacheDir(MyApplication.getMyApp());
        RichText.fromHtml(html)
                .bind(this)
                .autoFix(true) // 是否自动修复，默认true
                .resetSize(true) // 默认false，是否忽略img标签中的宽高尺寸（只在img标签中存在宽高时才有效）
                // ，true：忽略标签中的尺寸并触发SIZE_READY回调，false：使用img标签中的宽高尺寸，不触发SIZE_READY回调
                .imageDownloader(new DefaultImageDownloader()) // 设置DefaultImageGetter的图片下载器
                .cache(CacheType.all)//默认为CacheType.ALL
                .into(productDetail_tv);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }

}

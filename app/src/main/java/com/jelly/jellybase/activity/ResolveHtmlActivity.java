package com.jelly.jellybase.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.base.BaseApplication;
import com.jelly.baselibrary.richtext.OkHttpImageDownloader;
import com.jelly.baselibrary.BaseActivity;
import com.jelly.jellybase.R;
import com.zzhoujay.richtext.CacheType;
import com.zzhoujay.richtext.ImageHolder;
import com.zzhoujay.richtext.RichText;
import com.zzhoujay.richtext.RichTextConfig;
import com.zzhoujay.richtext.RichType;
import com.zzhoujay.richtext.callback.Callback;
import com.zzhoujay.richtext.callback.DrawableGetter;
import com.zzhoujay.richtext.ig.DefaultImageGetter;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/12/18.
 */

public class ResolveHtmlActivity extends BaseActivity {
    @BindView(R.id.productDetail_tv)
    TextView productDetail_tv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iniVew();
    }
    @Override
    public int getLayoutId(){
        return R.layout.resolve_html_activity;
    }
    private void iniVew(){
        String html="<h1>百度一下,你就知道官</h1>"
                + "全球最大的中文搜索引擎、致力于让网民更便捷地获取信息，找到所求。百度超过千亿的中文网页数据库，" +
                "可以瞬间找到相关的搜索结果。"
                + "<img src='https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000" +
                "&sec=1513612039447&di=2b72984467a03a7cfb0bb40ffe008388&imgtype=0" +
                "&src=http%3A%2F%2Foss.laohucaijing.com%2FUserFiles%2FImage%2F201603%2F20160330111155221.png' />";
        // 设置为Html
        RichText.initCacheDir(BaseApplication.getInstance());
        if (html.toLowerCase().contains(".gif")) {
            //gif图片关闭硬件加速
            productDetail_tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        RichText.from(html) // 数据源
                .type(RichType.html) // 数据格式,不设置默认是Html,使用fromMarkdown的默认是Markdown格式
                .autoFix(true) // 是否自动修复，默认true
                .autoPlay(true) // gif图片是否自动播放
//                .showBorder(true) // 是否显示图片边框
//                .borderColor(Color.RED) // 图片边框颜色
//                .borderSize(10) // 边框尺寸
//                .borderRadius(50) // 图片边框圆角弧度
                .scaleType(ImageHolder.ScaleType.fit_center) // 图片缩放方式
                .size(ImageHolder.MATCH_PARENT, ImageHolder.WRAP_CONTENT) // 图片占位区域的宽高
//                .fix(imageFixCallback) // 设置自定义修复图片宽高
//                .fixLink(linkFixCallback) // 设置链接自定义回调
//                .noImage(true) // 不显示并且不加载图片
                .resetSize(false) // 默认false，是否忽略img标签中的宽高尺寸（只在img标签中存在宽高时才有效），true：忽略标签中的尺寸并触发SIZE_READY回调，false：使用img标签中的宽高尺寸，不触发SIZE_READY回调
//                .clickable(true) // 是否可点击，默认只有设置了点击监听才可点击
//                .imageClick(onImageClickListener) // 设置图片点击回调
//                .imageLongClick(onImageLongClickListener) // 设置图片长按回调
//                .urlClick(onURLClickListener) // 设置链接点击回调
//                .urlLongClick(onUrlLongClickListener) // 设置链接长按回调
                .imageGetter(new DefaultImageGetter())//.imageGetter(yourImageGetter) // 设置图片加载器，默认为DefaultImageGetter，
                .imageDownloader(new OkHttpImageDownloader()) // 设置DefaultImageGetter的图片下载器
                .cache(CacheType.all)//默认为CacheType.ALL
                .errorImage(new DrawableGetter() {
                    @Override
                    public Drawable getDrawable(ImageHolder holder, RichTextConfig config, TextView textView) {
                        return ContextCompat.getDrawable(ResolveHtmlActivity.this,R.mipmap.nopic);
                    }
                })// 设置加载失败的错误图
                .placeHolder(new DrawableGetter() {
                    @Override
                    public Drawable getDrawable(ImageHolder holder, RichTextConfig config, TextView textView) {
                        return ContextCompat.getDrawable(ResolveHtmlActivity.this,R.mipmap.nopic);
                    }
                })//设置加载中显示的占位图
                .done(new Callback() {
                    @Override
                    public void done(boolean imageLoadDone) {
                        Log.i("SSSS", "imageLoadDone="+imageLoadDone);
                    }
                })
                .bind(this)
                .into(productDetail_tv);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // activity onDestory时
        RichText.clear(this);
        //在应用退出时调用
        RichText.recycle();
    }
    @OnClick({R.id.left_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_back:
                finish();
                break;
        }
    }
}

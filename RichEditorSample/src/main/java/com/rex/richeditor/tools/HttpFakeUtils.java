package com.rex.richeditor.tools;

import com.rex.editor.common.EssFile;

/**
 * @author Rex on 2019/9/3.
 * 模拟假的 上传本地文件到服务器生成在线url，这部分 还是得替换成你自己服务器的
 */
public class HttpFakeUtils {

    public final static String TEST_VIDEO_URL = "https://www.w3school.com.cn/example/html5/mov_bbb.mp4";
    public final static String TEST_IMAGE_URL = "http://1812.img.pp.sohu.com.cn/images/blog/2009/11/18/18/8/125b6560a6ag214.jpg";
    public final static String TEST_WEB_URL = "https://github.com/RexSuper/RichHtmlEditorForAndroid";
    public final static String TEST_VIDEOTHUMBNAIL_URL = "http://s13.sinaimg.cn/orignal/55bc63e40759b4d0c165c";

    public interface HttpResult {
        void onSuccess(String url);

        void onError(String error);
    }

    public static void postFile(EssFile essFile, HttpResult httpResult) {
        if (httpResult != null) {
            if (essFile.isGif() || essFile.isImage()) {
                if (essFile.getAbsolutePath().contains("poster")) {
                    httpResult.onSuccess(TEST_VIDEOTHUMBNAIL_URL);
                } else {
                    httpResult.onSuccess(TEST_IMAGE_URL);
                }

            } else if (essFile.isVideo()) {
                httpResult.onSuccess(TEST_VIDEO_URL);
            } else {
                httpResult.onSuccess(TEST_VIDEO_URL);
            }
        }
    }

    public static void publishArticle(String html, HttpResult httpResult) {
        if (httpResult != null) {
            httpResult.onSuccess("发布成功");
        }

    }


}

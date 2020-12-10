package fm.jiecao.jcvideoplayer_lib;

/**
 * 视频播放监听
 */
public interface JCVedioPlayerListener {
    /**
     * 开始播放
     */
    public void onStart();

    /**
     * 播放完成
     */
    public void onCompletion();

    /**
     * 播放出错
     */
    public void onError();
}


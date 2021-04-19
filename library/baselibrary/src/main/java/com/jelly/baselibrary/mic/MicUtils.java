package com.jelly.baselibrary.mic;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.jelly.baselibrary.log.LogUtils;

import java.io.ObjectStreamException;

public class MicUtils {
    /**
     * 但是jdk 1.5 以后java 编译器允许乱序执行 。所以执行顺序可能是1-3-2 或者 1-2-3.如果是前者先执行3 的话
     * 切换到其他线程，instance 此时 已经是非空了，此线程就会直接取走instance ，直接使用，这样就回出错。DCL 失效。
     * 解决方法 SUN 官方已经给我们了。将instance 定义成 private volatile static Singleton instance =null: 即可
     */
    private MicUtils() {}
    /**
     * 内部类，在装载该内部类时才会去创建单利对象
     */
    private static class SingletonHolder{
        private static final MicUtils instance = new MicUtils();
    }
    /**
     * 单一实例
     */
    public static MicUtils getMicUtils() {
        return SingletonHolder.instance;
    }
    /**
     * 要杜绝单例对象在反序列化时重新生成对象，那么必须加入如下方法：
     * @return
     * @throws ObjectStreamException
     */
    private Object readResolve() throws ObjectStreamException {
        return SingletonHolder.instance;
    }
    public void getMic(Context context){
        AudioManager   mAm = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        LogUtils.i("isMusicActive="+mAm.isMusicActive());
        //if (mAm.isMusicActive()){
        AudioRecord mic;
        //根据自己设置的音频格式配置相应的数组大小，用于存储数据，同时可以提高效率，节约空间
        /*
        * ASAMPLERATE：音频采样率，有44100、22050、11025、4000、8000 等，代表了采样的品质高低，采样率越高品质越高。
          ACHANNEL：声道设置：android支持双声道立体声和单声道。MONO单声道，STEREO立体声
          AudioFormat.ENCODING_PCM_16BIT：采样大小为16bit 还可以设置成8bit
        *
        * */
        int bufferSize = 2 * AudioRecord.getMinBufferSize(11025,AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        LogUtils.i("bufferSize="+bufferSize);
        mic = new AudioRecord(MediaRecorder.AudioSource.MIC, 11025, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        mic.startRecording();
        LogUtils.i("getAudioSource="+mic.getAudioSource());
        byte pcmBuffer[] = new byte[2048];
       // while (aloop && !Thread.interrupted()) {
            int size = mic.read(pcmBuffer, 0, pcmBuffer.length);
            LogUtils.i("size="+size);
            if (size <= 0) {
               // break;
            }
            //将获取的数据发送出去
            // mEncoder.onGetPcmFrame(pcmBuffer, size);
        //}
        //  }

    }
}

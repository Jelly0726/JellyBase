package com.jelly.baselibrary.mic;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;

import com.jelly.baselibrary.log.LogUtils;

public class MicService extends Service {
    private static final String TAG = "MainService";
    private AudioManager mAm;
    private MyOnAudioFocusChangeListener mListener;
    @Override
    public void onCreate()
    {
        LogUtils.i(TAG, "micsevice onCreate");
        mAm = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mListener = new MyOnAudioFocusChangeListener();
    }


    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startid)
    {
        // Request audio focus for playback
        int result = mAm.requestAudioFocus(mListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
        {

        }
        else
        {
        }
    }


    @Override
    public void onDestroy()
    {
        LogUtils.i(TAG, "micsevice onDestroy");
        mAm.abandonAudioFocus(mListener);
    }


    private class MyOnAudioFocusChangeListener implements
            AudioManager.OnAudioFocusChangeListener
    {
        @Override
        public void onAudioFocusChange(int focusChange)
        {
            LogUtils.i(TAG, "focusChange=" + focusChange);
        }
    }
}

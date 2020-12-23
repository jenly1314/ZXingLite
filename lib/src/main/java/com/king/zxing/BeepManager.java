package com.king.zxing;
/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;

import com.king.zxing.util.LogUtils;

import java.io.Closeable;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class BeepManager implements MediaPlayer.OnErrorListener, Closeable {

    private static final long VIBRATE_DURATION = 200L;

    private final Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean playBeep;
    private boolean vibrate;

    BeepManager(Context context) {
        this.context = context;
        this.mediaPlayer = null;
        updatePrefs();
    }

    public void setVibrate(boolean vibrate){
        this.vibrate = vibrate;
    }

    public void setPlayBeep(boolean playBeep){
        this.playBeep = playBeep;
    }

    private synchronized void updatePrefs() {
        if (mediaPlayer == null) {
            mediaPlayer = buildMediaPlayer(context);
        }
        if(vibrator == null){
            vibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    public synchronized void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private MediaPlayer buildMediaPlayer(Context context) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.zxl_beep);
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            return mediaPlayer;
        } catch (Exception e) {
            LogUtils.w(e);
            mediaPlayer.release();
            return null;
        }
    }

    @Override
    public synchronized boolean onError(MediaPlayer mp, int what, int extra) {
        close();
        updatePrefs();
        return true;
    }

    @Override
    public synchronized void close() {
        try{
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }catch (Exception e){
            LogUtils.e(e);
        }
    }

}
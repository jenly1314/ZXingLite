package com.king.zxing.manager;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.king.zxing.R;
import com.king.zxing.util.LogUtils;

import java.io.Closeable;

/**
 * 蜂鸣音效管理器：主要用于播放蜂鸣提示音和振动效果
 *
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class BeepManager implements MediaPlayer.OnErrorListener, Closeable {

    private static final long VIBRATE_DURATION = 200L;

    private final Context context;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private boolean playBeep;
    private boolean vibrate;

    public BeepManager(Context context) {
        this.context = context;
        this.mediaPlayer = null;
        updatePrefs();
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public void setPlayBeep(boolean playBeep) {
        this.playBeep = playBeep;
    }

    private synchronized void updatePrefs() {
        if (mediaPlayer == null) {
            mediaPlayer = buildMediaPlayer(context);
        }
        if (vibrator == null) {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }

    public synchronized void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(VIBRATE_DURATION);
            }
        }
    }

    private MediaPlayer buildMediaPlayer(Context context) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor file = context.getResources().openRawResourceFd(R.raw.zxl_beep);
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
            mediaPlayer.setOnErrorListener(this);
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
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

}
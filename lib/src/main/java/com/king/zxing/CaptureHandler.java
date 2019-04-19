package com.king.zxing;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Browser;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.king.zxing.camera.CameraManager;

import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public class CaptureHandler extends Handler {

    private static final String TAG = CaptureHandler.class.getSimpleName();

    private final OnCaptureListener onCaptureListener;
    private final DecodeThread decodeThread;
    private State state;
    private final CameraManager cameraManager;
    private final Activity activity;
    private final ViewfinderView viewfinderView;


    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    CaptureHandler(Activity activity,ViewfinderView viewfinderView,OnCaptureListener onCaptureListener,
                   Collection<BarcodeFormat> decodeFormats,
                   Map<DecodeHintType,?> baseHints,
                   String characterSet,
                   CameraManager cameraManager) {
        this.activity = activity;
        this.viewfinderView = viewfinderView;
        this.onCaptureListener = onCaptureListener;
        decodeThread = new DecodeThread(activity,cameraManager,this, decodeFormats, baseHints, characterSet,
                new ViewfinderResultPointCallback(viewfinderView));
        decodeThread.start();
        state = State.SUCCESS;

        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.restart_preview) {
            restartPreviewAndDecode();

        } else if (message.what == R.id.decode_succeeded) {
            state = State.SUCCESS;
            Bundle bundle = message.getData();
            Bitmap barcode = null;
            float scaleFactor = 1.0f;
            if (bundle != null) {
                byte[] compressedBitmap = bundle.getByteArray(DecodeThread.BARCODE_BITMAP);
                if (compressedBitmap != null) {
                    barcode = BitmapFactory.decodeByteArray(compressedBitmap, 0, compressedBitmap.length, null);
                    // Mutable copy:
                    barcode = barcode.copy(Bitmap.Config.ARGB_8888, true);
                }
                scaleFactor = bundle.getFloat(DecodeThread.BARCODE_SCALED_FACTOR);
            }
            onCaptureListener.onHandleDecode((Result) message.obj, barcode, scaleFactor);

        } else if (message.what == R.id.decode_failed) {// We're decoding as fast as possible, so when one decode fails, start another.
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);

        } else if (message.what == R.id.return_scan_result) {
            activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
            activity.finish();

        } else if (message.what == R.id.launch_product_query) {
            String url = (String) message.obj;

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intents.FLAG_NEW_DOC);
            intent.setData(Uri.parse(url));

            ResolveInfo resolveInfo =
                    activity.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
            String browserPackageName = null;
            if (resolveInfo != null && resolveInfo.activityInfo != null) {
                browserPackageName = resolveInfo.activityInfo.packageName;
                Log.d(TAG, "Using browser in package " + browserPackageName);
            }

            // Needed for default Android browser / Chrome only apparently
            if (browserPackageName != null) {
                switch (browserPackageName) {
                    case "com.android.browser":
                    case "com.android.chrome":
                        intent.setPackage(browserPackageName);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, browserPackageName);
                        break;
                }
            }

            try {
                activity.startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
                Log.w(TAG, "Can't find anything to handle VIEW of URI " + url);
            }

        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause() will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            viewfinderView.drawViewfinder();
        }
    }
}

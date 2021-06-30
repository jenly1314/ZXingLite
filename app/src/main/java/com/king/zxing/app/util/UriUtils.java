package com.king.zxing.app.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.king.zxing.util.LogUtils;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class UriUtils {

    private UriUtils(){
        throw new AssertionError();
    }

    /**
     * 获取图片
     */
    public static String getImagePath(Context context,Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        //获取系統版本
        int currentapiVersion = Build.VERSION.SDK_INT;
        if(currentapiVersion> Build.VERSION_CODES.KITKAT){
            LogUtils.d("uri=intent.getData :" + uri);
            if (DocumentsContract.isDocumentUri(context, uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                Log.d("getDocumentId(uri) :", "" + docId);
                Log.d("uri.getAuthority() :", "" + uri.getAuthority());
                if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
                } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(context,contentUri, null);
                }

            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagePath = getImagePath(context,uri, null);
            }
        }else{
            imagePath = getImagePath(context,uri, null);
        }

        return imagePath;

    }

    /**
     * 通过uri和selection来获取真实的图片路径,从相册获取图片时要用
     */
    private static String getImagePath(Context context,Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}

/*
 * Copyright (C) 2018 Jenly Yu
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
package com.king.zxing.app.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.king.zxing.app.R;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 */
public final class StatusBarUtils {

    private StatusBarUtils(){
        throw new AssertionError();
    }

    public static void immersiveStatusBar(Activity activity, Toolbar toolbar) {
        immersiveStatusBar(activity,toolbar,0.0f);
    }

    public static void immersiveStatusBar(Activity activity,Toolbar toolbar,@FloatRange(from = 0.0, to = 1.0) float alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewGroup decorView = (ViewGroup) window.getDecorView();
        ViewGroup contentView = window.getDecorView().findViewById(Window.ID_ANDROID_CONTENT);
        View rootView = contentView.getChildAt(0);
        if (rootView != null) {
            rootView.setFitsSystemWindows(false);
        }
        if(toolbar!=null){
            toolbar.setPadding(0,getStatusBarHeight(activity),0,0);
        }

        decorView.addView(createStatusBarView(activity,alpha));
    }

    private static View createStatusBarView(Activity activity,@FloatRange(from = 0.0, to = 1.0) float alpha) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.argb((int) (alpha * 255), 0, 0, 0));
        statusBarView.setId(R.id.translucent_view);
        return statusBarView;
    }

    /** 获取状态栏高度 */
    public static int getStatusBarHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.status_bar_height);
    }
}

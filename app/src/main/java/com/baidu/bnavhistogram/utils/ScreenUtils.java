/*
 * Copyright (C) 2018 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.bnavhistogram.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public class ScreenUtils {
    private static Display defaultDisplay;

    private static float mDensity = 0;
    private static float mScaledDensity = 0;
    private static int mScreenHeight = 0;
    private static int StatusBarHeightCacheVal = 0;
    private static int StatusBarHeightFullScreenCacheVal = 0;

    public static float getDensity(Context context) {
        if (mDensity == 0) {
            mDensity = context.getResources().getDisplayMetrics().density;
        }
        return mDensity;
    }

    public static float getScaledDensity(Context context) {
        if (mScaledDensity == 0) {
            mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        }
        return mScaledDensity;
    }

    public static int dip2px(Context context, int dip) {
        return (int) (0.5F + getDensity(context) * dip);
    }

    public static int px2dip(Context context, int px) {
        return (int) (0.5F + px / getDensity(context));
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param （DisplayMetrics类中属性scaledDensity）
     *
     * @return
     */
    public static int px2sp(float pxValue, Context context) {
        return (int) (pxValue / getScaledDensity(context) + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param （DisplayMetrics类中属性scaledDensity）
     *
     * @return
     */
    public static int sp2px(float spValue, Context context) {
        return (int) (spValue * getScaledDensity(context) + 0.5f);
    }

    public static int getScreenWidth(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().widthPixels;
        }
        return 0;
    }

    // get screen height value int pixels
    public static int getScreenHeight(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
        return 0;
    }

    public static void setViewScreenHeight(int height) {
        if (mScreenHeight >= 0 && (mScreenHeight - height) < mScreenHeight / 4) {
            mScreenHeight = height;
        }
    }

    public static int getViewScreenHeight(Context context) {
        if (context == null) {
            return 0;
        }
        if (mScreenHeight > 0) {
            return mScreenHeight;
        }
        return getScreenHeight(context) - getStatusBarHeight(context);
    }

    public static Display getDefaultDisplay(Context context) {
        if (defaultDisplay == null) {
            defaultDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        }
        return defaultDisplay;
    }

    public static int getHeight(Context context) {
        return getDefaultDisplay(context).getHeight();
    }

    public static int getWidth(Context context) {
        return getDefaultDisplay(context).getWidth();
    }

    public static int percentHeight(float percent, Context context) {
        return (int) (percent * getHeight(context));
    }

    public static int percentWidth(float percent, Context context) {
        return (int) (percent * getWidth(context));
    }

    public static int getStatusBarHeight(Context context) {
        if (StatusBarHeightCacheVal != 0) {
            return StatusBarHeightCacheVal;
        }
        if (context instanceof Activity) {
            Rect rectangle = new Rect();
            Window window = ((Activity) context).getWindow();
            if (window != null && window.getDecorView() != null) {
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
            }
            StatusBarHeightCacheVal = rectangle.top;
        } else {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return (int) Math.ceil(25 * metrics.density);
        }

        return StatusBarHeightCacheVal;
    }

    public static int getStatusBarHeightFullScreen(Context context) {
        if (StatusBarHeightFullScreenCacheVal != 0) {
            return StatusBarHeightFullScreenCacheVal;
        }
        if (Build.VERSION.SDK_INT < 21 || context == null) {
            return 0;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            StatusBarHeightFullScreenCacheVal = context.getResources().getDimensionPixelSize(resourceId);
        }
        return StatusBarHeightFullScreenCacheVal > 0 ? StatusBarHeightFullScreenCacheVal : dip2px(context, 25);
    }

}

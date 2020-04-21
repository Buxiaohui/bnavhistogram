package com.baidu.histogram.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * 震动管理类
 */
public class VibrateHelper {

    public static final int DEFAULT_VIBRATE_DURATION_MS = 500;
    private Vibrator vibrator;
    private Context mCtx;

    public VibrateHelper(@NonNull Context ctx) {
        if (ctx != null) {
            this.mCtx = ctx;
            vibrator = (Vibrator) ctx.getSystemService(Service.VIBRATOR_SERVICE);
        } else {
            throw new IllegalArgumentException("context must not be null");
        }
    }

    public static boolean isVibratePermissionEnabled(Context ctx) {
        boolean ret =
                ContextCompat.checkSelfPermission(ctx, Manifest.permission.VIBRATE)
                        == PackageManager.PERMISSION_GRANTED;
        return ret;
    }

    public static boolean hasVibratePermission(@NonNull Context ctx) {
        if (ctx == null) {
            return false;
        }
        return ContextCompat.checkSelfPermission(ctx,
                Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED;
    }

    public void mobileVibration() {
        mobileVibration(DEFAULT_VIBRATE_DURATION_MS);
    }

    public void mobileVibration(long milliseconds) {
        if (vibrator == null) {
            return;
        }
        if (!checkVibrate()) {
            return;
        }
        if (mCtx == null || !hasVibratePermission(mCtx)) {
            return;
        }
        try {
            vibrator.vibrate(milliseconds);
        } catch (Exception e) {
        }

    }

    @SuppressLint("NewApi")
    private boolean checkVibrate() {
        if (vibrator == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (!vibrator.hasVibrator()) {
                return false;
            }
        }
        return true;
    }

    public void onDestroy() {
        if (vibrator == null) {
            return;
        }
        if (!checkVibrate()) {
            return;
        }
        if (mCtx == null || !hasVibratePermission(mCtx)) {
            return;
        }
        try {
            vibrator.cancel();
        } catch (Exception e) {
        }
    }
}

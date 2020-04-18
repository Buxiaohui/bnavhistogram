package com.baidu.bnavhistogram;

import com.baidu.bnavhistogram.utils.ScreenUtils;
import com.baidu.histogram.view.BaseSizeDefiner;

import android.content.Context;
import androidx.annotation.NonNull;

public class HistogramSizeDefiner extends BaseSizeDefiner {
    public HistogramSizeDefiner(@NonNull Context ctx) {
        super(ctx);
    }

    @Override
    public int getDimensionPixelOffset(int dimenId) {
        return mCtx.getResources().getDimensionPixelOffset(dimenId);
    }

    @Override
    public int dip2px(int dp) {
        return ScreenUtils.dip2px(mCtx, dp);
    }

    @Override
    public int getScreenWidthPx() {
        return ScreenUtils.getScreenWidth(mCtx);
    }

    @Override
    public int getHistogramLeftPaddingPx() {
        return dip2px(0);
    }

    @Override
    public int getMaxItemCount() {
        return 7;
    }

    @Override
    public double getCenterItemScaling() {
        return 1.5d;
    }

    @Override
    public int getSelectTimeZoneHeight() {
        return dip2px(29);
    }

    @Override
    public int getSelectTimeZoneWidth() {
        return dip2px(75);
    }

    @Override
    public int getPanelBg9ImgPaddingPx() {
        return 0;
    }

    @Override
    public int getMinHeightPx() {
        return dip2px(21);
    }

    @Override
    public int getMaxHeightPx() {
        return dip2px(72);
    }

    @Override
    public int getItemPillarWidthPx() {
        return dip2px(18);
    }

    @Override
    public int getHistogramHeightPx() {
        return dip2px(141);
    }

    @Override
    public int getItemTimeHeightPx() {
        return dip2px(33);
    }

    @Override
    public int getItemTimeMarginPx() {
        return dip2px(3);
    }
}

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
    public int dip2px(int dp) {
        return ScreenUtils.dip2px(mCtx, dp);
    }

    @Override
    public int getScreenWidthPx() {
        return ScreenUtils.getScreenWidth(mCtx);
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
    public int getLeftMarginPx() {
        return 0;
    }

    @Override
    public int getLeftPaddingPx() {
        return 0;
    }
}

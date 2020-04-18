package com.baidu.histogram.view;

import android.content.Context;
import androidx.annotation.NonNull;

public abstract class BaseSizeDefiner {
    // 横屏展示7个item
    public static final int MAX_ITEM_COUNT = 7;
    // 居中的item宽度是两侧item的1.5倍
    public static final double SELECT_ITEM_SCALING = 1.5D;
    // 展示几个item
    protected int mMaxItemCount;
    // 居中的item宽度是普通item的几倍
    protected double mSelectItemScaling;
    // 单个item宽度
    protected int mItemWidthPx;
    // 被选中的item宽度
    protected int mItemSelectWidthPx;
    // 内部柱子宽度
    protected int mItemPillarWidthPx;
    // 柱状图区宽度
    protected int mHistogramWithPx;
    // 屏幕宽度
    protected int mScreenWithPx;
    // 柱状图区左右间距,left=right
    protected int mHistogramLeftPaddingPx;
    // 单个item中柱子的左右边界
    protected int mPillarLeft;
    protected int mPillarRight;
    // 柱子的最小高度
    protected int mMinHeightPx;
    // 柱子的最大高度
    protected int mMaxHeightPx;
    // 柱状图高度
    protected int mHistogramHeightPx;
    // 当item的中线达到边界内时，进行加粗等变化
    protected int mPilllarAnimLeft;
    protected int mPilllarAnimRight;
    // 底部时间的高度
    protected int mItemTimeHeightPx;
    // 底部时间的margin
    protected int mItemTimeMarginPx;

    // 面板.9图阴影宽度,left=right
    protected int mPanelBg9ImgLeftPaddingPx;

    protected double mScaleOfItemWidth;
    protected int mSelectTimeZoneWidth;
    protected int mSelectTimeZoneHeight;

    protected int mHistogramScrollOffset;

    protected Context mCtx;

    public BaseSizeDefiner(@NonNull Context ctx) {
        this.mCtx = ctx;
        init();
    }

    public void init() {
        mMaxItemCount = getMaxItemCount();
        mSelectItemScaling = getCenterItemScaling();
        mScreenWithPx = getScreenWidthPx();
        // 正常宽度的柱子数量
        int normalWidthItemCount = mMaxItemCount / 2 * 2;
        mSelectTimeZoneWidth = getSelectTimeZoneWidth();
        mSelectTimeZoneHeight = getSelectTimeZoneHeight();
        mHistogramLeftPaddingPx = getHistogramLeftPaddingPx();
        mPanelBg9ImgLeftPaddingPx = getPanelBg9ImgPaddingPx();
        mHistogramWithPx = mScreenWithPx - (mHistogramLeftPaddingPx << 1) - (mPanelBg9ImgLeftPaddingPx << 1);

        double itemWidth = mHistogramWithPx / (normalWidthItemCount + mSelectItemScaling);
        mItemWidthPx = (int) Math.ceil(itemWidth);

        mItemSelectWidthPx = (int) Math.ceil(itemWidth * mSelectItemScaling);

        mScaleOfItemWidth = (1.0d * mItemSelectWidthPx) / (1.0d * mItemWidthPx);

        mItemPillarWidthPx = getItemPillarWidthPx();

        mPillarLeft = (mHistogramWithPx >> 1) - (mItemPillarWidthPx >> 1);
        mPillarRight = (mHistogramWithPx >> 1) + (mItemPillarWidthPx >> 1);

        // 当普通柱子的中心x坐标处于[mPilllarAnimLeft ,mPilllarAnimRight]区间内就会开始动画(逐渐变宽度)
        mPilllarAnimLeft = (mHistogramWithPx >> 1) - ((mItemWidthPx + mScreenWithPx) >> 1);
        mPilllarAnimRight = (mHistogramWithPx >> 1) + ((mItemWidthPx + mScreenWithPx) >> 1);

        mMinHeightPx = getMinHeightPx();

        mMaxHeightPx = getMaxHeightPx();

        mHistogramHeightPx = getHistogramHeightPx();

        mItemTimeHeightPx = getItemTimeHeightPx();

        mItemTimeMarginPx = getItemTimeMarginPx();

        int midX = mItemWidthPx >> 1;
        int halfScreen = mHistogramWithPx >> 1;
        float delta = 1.0f * (mItemSelectWidthPx >> 1) + 1.0f * (mItemWidthPx >> 1);
        if (halfScreen - midX >= delta) {
            mHistogramScrollOffset =
                    (int) (midX - halfScreen + 1.0f * (mItemSelectWidthPx >> 1) - 1.0f * (mItemWidthPx >> 1));
        } else {
            mHistogramScrollOffset =
                    (int) (1.0f * (mItemWidthPx >> 1) / (mItemSelectWidthPx + mItemWidthPx) * (midX - halfScreen));
        }
    }

    public abstract int getDimensionPixelOffset(int dimenId);

    public abstract int dip2px(int dp);

    public abstract int getScreenWidthPx();

    public abstract int getHistogramLeftPaddingPx();

    public abstract int getMaxItemCount();

    public abstract double getCenterItemScaling();

    public abstract int getSelectTimeZoneHeight();

    public abstract int getSelectTimeZoneWidth();

    public abstract int getPanelBg9ImgPaddingPx();

    public abstract int getMinHeightPx();

    public abstract int getMaxHeightPx();

    public abstract int getItemPillarWidthPx();

    public abstract int getHistogramHeightPx();

    public abstract int getItemTimeHeightPx();

    public abstract int getItemTimeMarginPx();

    public int getHistogramWithPx() {
        return mHistogramWithPx;
    }

    public int getLeftEmptyItemCount() {
        return getMaxItemCount() >> 1;
    }

    public int getHistogramScrollOffset() {
        return mHistogramScrollOffset;
    }

    public double getScaleOfItemWidth() {
        return mScaleOfItemWidth;
    }

    public int getItemSelectWidthPx() {
        return mItemSelectWidthPx;
    }

    public int getItemWidthPx() {
        return mItemWidthPx;
    }

    public int getPillarLeft() {
        return mPillarLeft;
    }

    public int getPillarRight() {
        return mPillarRight;
    }

    public int getPilllarAnimLeft() {
        return mPilllarAnimLeft;
    }

    public int getPilllarAnimRight() {
        return mPilllarAnimRight;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SizeHolder{");
        sb.append("mItemWidthPx=").append(mItemWidthPx);
        sb.append(", mItemSelectWidthPx=").append(mItemSelectWidthPx);
        sb.append(", mItemPillarWidthPx=").append(mItemPillarWidthPx);
        sb.append(", mHistogramWithPx=").append(mHistogramWithPx);
        sb.append(", mScreenWithPx=").append(mScreenWithPx);
        sb.append(", mHistogramLeftPaddingPx=").append(mHistogramLeftPaddingPx);
        sb.append(", mPillarLeft=").append(mPillarLeft);
        sb.append(", mPillarRight=").append(mPillarRight);
        sb.append(", mMinHeightPx=").append(mMinHeightPx);
        sb.append(", mMaxHeightPx=").append(mMaxHeightPx);
        sb.append(", mHistogramHeightPx=").append(mHistogramHeightPx);
        sb.append(", mPilllarAnimLeft=").append(mPilllarAnimLeft);
        sb.append(", mPilllarAnimRight=").append(mPilllarAnimRight);
        sb.append(", mItemTimeHeightPx=").append(mItemTimeHeightPx);
        sb.append(", mItemTimeMarginPx=").append(mItemTimeMarginPx);
        sb.append(", mPanelBg9ImgLeftPaddingPx=").append(mPanelBg9ImgLeftPaddingPx);
        sb.append(", mScaleOfItemWidth=").append(mScaleOfItemWidth);
        sb.append(", mSelectTimeZoneWidth=").append(mSelectTimeZoneWidth);
        sb.append(", mSelectTimeZoneHeight=").append(mSelectTimeZoneHeight);
        sb.append(", mHistogramScrollOffset=").append(mHistogramScrollOffset);
        sb.append('}');
        return sb.toString();
    }

    public void release() {

    }
}

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
    // 柱状图区左右两侧padding,left=right
    protected int mLeftPaddingPx;
    // 柱状图区左右两侧margin,left=right
    protected int mLeftMarginPx;
    // 柱子的最小高度
    protected int mMinHeightPx;
    // 柱子的最大高度
    protected int mMaxHeightPx;
    // 柱状图高度
    protected int mHistogramHeightPx;
    // 当item的中线达到边界内时，进行加粗等变化
    protected int mPilllarAnimLeft;
    protected int mPilllarAnimRight;

    protected double mScaleOfItemWidth;

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
        mLeftPaddingPx = getLeftPaddingPx();
        mLeftMarginPx = getLeftMarginPx();
        mHistogramWithPx = mScreenWithPx - (mLeftPaddingPx << 1) - (mLeftMarginPx << 1);

        double itemWidth = mHistogramWithPx / (normalWidthItemCount + mSelectItemScaling);
        mItemWidthPx = (int) Math.ceil(itemWidth);

        mItemSelectWidthPx = (int) Math.ceil(itemWidth * mSelectItemScaling);

        mScaleOfItemWidth = (1.0d * mItemSelectWidthPx) / (1.0d * mItemWidthPx);

        // 当普通柱子的中心x坐标处于[mPilllarAnimLeft ,mPilllarAnimRight]区间内就会开始动画(逐渐变宽度)
        mPilllarAnimLeft = (mHistogramWithPx >> 1) - ((mItemWidthPx ) >> 2);
        mPilllarAnimRight = (mHistogramWithPx >> 1) + ((mItemWidthPx ) >> 2);

        mMinHeightPx = getMinHeightPx();

        mMaxHeightPx = getMaxHeightPx();

        mHistogramHeightPx = getHistogramHeightPx();

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

    /**
     * @param dp 转 pix
     * @return
     */
    public abstract int dip2px(int dp);

    /**
     * @return 屏幕宽度，单位：像素
     */
    public abstract int getScreenWidthPx();

    /**
     * @return 柱状图区域左侧padding ，单位：像素
     */
    public abstract int getLeftMarginPx();

    /**
     * @return 柱状图区域左侧margin ，单位：像素
     */
    public abstract int getLeftPaddingPx();

    /**
     * @return 最多展示柱子数量
     */
    public abstract int getMaxItemCount();

    /**
     * @return 居中的柱子宽度与两侧柱子的宽度比例
     */
    public abstract double getCenterItemScaling();

    /**
     * @return 柱子最小高度，单位：像素
     */
    public abstract int getMinHeightPx();

    /**
     * @return 柱子最大高度，单位：像素
     */
    public abstract int getMaxHeightPx();

    public abstract int getItemPillarWidthPx();

    public abstract int getHistogramHeightPx();

    /**
     * @return 柱状图的整个view的宽度
     */
    public int getHistogramWithPx() {
        return mHistogramWithPx;
    }

    /**
     * @return 左侧占位柱子数量
     */
    public int getLeftEmptyItemCount() {
        return getMaxItemCount() >> 1;
    }

    public int getHistogramScrollOffset() {
        return mHistogramScrollOffset;
    }

    public int getItemSelectWidthPx() {
        return mItemSelectWidthPx;
    }

    public int getItemWidthPx() {
        return mItemWidthPx;
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
        sb.append(", mHistogramLeftPaddingPx=").append(mLeftPaddingPx);
        sb.append(", mMinHeightPx=").append(mMinHeightPx);
        sb.append(", mMaxHeightPx=").append(mMaxHeightPx);
        sb.append(", mHistogramHeightPx=").append(mHistogramHeightPx);
        sb.append(", mPilllarAnimLeft=").append(mPilllarAnimLeft);
        sb.append(", mPilllarAnimRight=").append(mPilllarAnimRight);
        sb.append(", mScaleOfItemWidth=").append(mScaleOfItemWidth);
        sb.append(", mHistogramScrollOffset=").append(mHistogramScrollOffset);
        sb.append('}');
        return sb.toString();
    }

    public void release() {

    }
}

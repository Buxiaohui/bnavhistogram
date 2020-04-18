package com.baidu.histogram.view;

import java.util.List;

import com.baidu.histogram.callback.OnItemClickListener;
import com.baidu.histogram.callback.OnRecyclerItemClickListener;
import com.baidu.histogram.utils.LogUtil;
import com.baidu.histogram.utils.VibrateHelper;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistogramViewHelper<T> {
    private static final String TAG = "FutureTripMainPanelHistogramView";
    private static final boolean PRINT_HIGH_FREQUENCY_LOG = true;
    protected Context mCtx;

    protected ViewCallback mViewCallback;
    protected DataCallback mDataCallback;

    protected BaseSizeDefiner mSizeDefiner;

    protected VibrateHelper mVibrateHelper;

    protected RecyclerView mHistogramView;
    protected HistogramBaseAdapter mHistogramAdapter;
    protected HistogramLinearLayoutManager mLinearLayoutManager;
    protected RecyclerView.OnScrollListener mOnScrollListener;
    protected OnRecyclerItemClickListener mOnRecyclerItemClickListener;

    protected View mHistogramMidLine; // 中间线
    protected View mHistogramTopLine; // 最大值线

    // 底部高亮区域
    protected HistogramBottomHighLightView mHistogramBottomHighLightView;
    protected HistogramBottomViewBaseAdapter mHistogramHighLightViewAdapter;

    protected int mTimeViewInitOffset;

    protected boolean mIsUserClickCauseScroll;
    protected int mCurCenterItemIndex;

    public HistogramViewHelper(@NonNull ViewCallback viewCallback,
                               @NonNull DataCallback<T> dataCallback) {
        if (viewCallback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        if (dataCallback == null) {
            throw new IllegalArgumentException("dataCallback must not be null");
        }
        this.mViewCallback = viewCallback;
        this.mCtx = viewCallback.getContext();
        this.mHistogramView = viewCallback.getHistogramView();
        this.mHistogramAdapter = viewCallback.getHistogramAdapter();
        this.mHistogramBottomHighLightView = viewCallback.getHistogramBottomHighLightView();
        this.mHistogramHighLightViewAdapter = viewCallback.getHistogramBottomHighLightViewAdapter();
        mSizeDefiner = viewCallback.getSizeDefiner();

        this.mDataCallback = dataCallback;

        init();
    }

    private void init() {
        mVibrateHelper = new VibrateHelper(mCtx);
        mHistogramAdapter.setViewCallback(new HistogramBaseAdapter.ViewCallback() {
            @Override
            public int getCenterItemIndex() {
                return mCurCenterItemIndex;
            }
        });
        final OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int index, Object data) {
                onClickHistogramItem(index);
            }
        };
        mHistogramAdapter.setOnItemClickListener(onItemClickListener);
        mHistogramBottomHighLightView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClickListener.onItemClick(view, position, mDataCallback.getDataList().get(position));
            }
        });
        int initTimeViewOffsetIndex = mSizeDefiner.getLeftEmptyItemCount();
        calTimeViewOffsetOnInit(initTimeViewOffsetIndex);
        updateClipRect();
        // 裁剪bottom_middle露出区域
        initOnRecyclerItemClickListener();
        initScrollListener();
    }

    private void initOnRecyclerItemClickListener() {
        if (mOnRecyclerItemClickListener != null) {
            mHistogramView.removeOnItemTouchListener(mOnRecyclerItemClickListener);
        } else {
            mOnRecyclerItemClickListener = new OnRecyclerItemClickListener(mHistogramView) {
                @Override
                public void onItemClick(final RecyclerView.ViewHolder vh) {
                    mHistogramView.post(new Runnable() {
                        @Override
                        public void run() {
                            int index = (int) vh.itemView.getTag();
                            if (mDataCallback.isEmptyItem(index)) {
                                setCurSelectItemCenterHorizontalImmediately();
                            } else {
                                onClickHistogramItem(index);
                            }
                        }
                    });

                }

                @Override
                public void onItemLongClick(RecyclerView.ViewHolder vh) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            };
        }
        mHistogramView.addOnItemTouchListener(mOnRecyclerItemClickListener);
    }

    private void setCurSelectItemCenterHorizontalImmediately() {
        int curSelectIndex = mCurCenterItemIndex;
        setTargetItemCenterHorizontalImmediately(curSelectIndex);
    }

    public void ensureSelectIndexCenterHorizontal() {
        int curSelectIndex = mCurCenterItemIndex;
        if (isCenterHorizontal(curSelectIndex)) {
            moveTimeViewOnHistogramScroll();
            return;
        }
        if (LogUtil.LOGGABLE) {
            if (mHistogramView != null) {
                LogUtil.e(TAG, "ensureSelectIndexCenterHorizontal,scrollState:"
                        + mHistogramView.getScrollState());
            }
        }
        if (LogUtil.LOGGABLE) {
            if (mHistogramView != null) {
                LogUtil.e(TAG, "ensureSelectIndexCenterHorizontal,scrollState:"
                        + mHistogramView.getScrollState());
            }
        }
        if (mHistogramView != null && mHistogramView.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
            mHistogramView.stopScroll();
        }
        setCurSelectItemCenterHorizontalImmediately();
    }

    private void moveTimeViewOnHistogramScroll() {
        if (mHistogramBottomHighLightView == null) {
            return;
        }
        int histogramOff = calRecyclerViewOffset(getLayoutManager(), mHistogramView);
        double timeDx = Math.round(histogramOff * mSizeDefiner.getScaleOfItemWidth());
        double flagTimeDx = timeDx;

        int timeListViewCount = mHistogramHighLightViewAdapter.getCount();
        int targetOffset = (mTimeViewInitOffset + (int) flagTimeDx);
        int maxIndex = timeListViewCount - (BaseSizeDefiner.MAX_ITEM_COUNT >> 1) - 1;
        int itemWidth = mSizeDefiner.getItemSelectWidthPx();
        int maxScrollDistance = itemWidth * maxIndex + (itemWidth >> 1) - getMidXOfParent();
        if (PRINT_HIGH_FREQUENCY_LOG) {
            LogUtil.e(TAG, "moveTimeViewOnHistogramScroll,targetOffset:" + targetOffset
                    + ",timeListViewCount:" + timeListViewCount
                    + ",itemWidth:" + itemWidth
                    + ",maxIndex:" + maxIndex);
        }
        if (targetOffset > maxScrollDistance) { // 右边界兜底策略
            targetOffset = maxScrollDistance;
        }
        if (targetOffset < mTimeViewInitOffset) { // 左边界兜底策略
            targetOffset = mTimeViewInitOffset;
        }
        mHistogramBottomHighLightView.scrollToImmediately(targetOffset);
    }

    public void updateClipRect() {
        if (mSizeDefiner == null || mHistogramBottomHighLightView == null) {
            return;
        }
        int screen = mSizeDefiner.getHistogramWithPx();
        int zoneWidth = mSizeDefiner.getSelectTimeZoneWidth() - mSizeDefiner.dip2px(10); // 时间轴的正中间椭圆形区域的宽度
        int height = mSizeDefiner.getSelectTimeZoneHeight(); // 时间轴的正中间椭圆形区域的高度
        int left = (screen - zoneWidth) >> 1;
        int top = 0;
        int right = (screen + zoneWidth) >> 1;
        int bottom = height;
        Rect rect = new Rect(left, top, right, bottom);
        mHistogramBottomHighLightView.updateRect(rect);
        mHistogramBottomHighLightView.invalidate();
    }

    private LinearLayoutManager getLayoutManager() {
        return mViewCallback.getLayoutManager();
    }

    /**
     * @param index
     * @param midX
     *         index对应的柱子的中心x坐标
     */
    public void updateItemAppearanceOnScroll(int index, int midX) {

    }

    private void moveHightLightViewOnHistogramScroll() {
        if (mHistogramBottomHighLightView == null) {
            return;
        }
        int histogramOff = calRecyclerViewOffset(getLayoutManager(), mHistogramView);

        double timeDx = Math.round(histogramOff * mSizeDefiner.getScaleOfItemWidth());
        double flagTimeDx = timeDx;

        int timeListViewCount = mHistogramHighLightViewAdapter.getCount();
        int targetOffset = (mTimeViewInitOffset + (int) flagTimeDx);
        int maxIndex = timeListViewCount - (mSizeDefiner.MAX_ITEM_COUNT >> 1) - 1;
        int itemWidth = mSizeDefiner.getItemSelectWidthPx();
        int maxScrollDistance = itemWidth * maxIndex + (itemWidth >> 1) - getMidXOfParent();
        if (PRINT_HIGH_FREQUENCY_LOG) {
            LogUtil.e(TAG, "moveTimeViewOnHistogramScroll,targetOffset:" + targetOffset
                    + ",timeListViewCount:" + timeListViewCount
                    + ",itemWidth:" + itemWidth
                    + ",maxIndex:" + maxIndex);
        }
        if (targetOffset > maxScrollDistance) { // 右边界兜底策略
            targetOffset = maxScrollDistance;
        }
        if (targetOffset < mTimeViewInitOffset) { // 左边界兜底策略
            targetOffset = mTimeViewInitOffset;
        }
        mHistogramBottomHighLightView.scrollToImmediately(targetOffset);
    }

    private int calRecyclerViewOffset(LinearLayoutManager layoutManager,
                                      RecyclerView recyclerView) {
        recyclerView.requestLayout();
        // 找到即将移出屏幕Item的position,position是移出屏幕item的数量
        int position = layoutManager.findFirstVisibleItemPosition();
        // 根据position找到这个Item
        View firstVisibleChildView = layoutManager.findViewByPosition(position);

        if (firstVisibleChildView == null) {
            if (LogUtil.LOGGABLE) {
                LogUtil.e(TAG, "calRecyclerViewOffset,view is null,pos:" + position);
            }
            return 0;
        }

        // 获取Item的高
        int itemWidth = firstVisibleChildView.getWidth();
        // 算出该Item还未移出屏幕的高度
        int itemLeft = firstVisibleChildView.getLeft();
        // position移出屏幕的数量*高度得出移动的距离
        int iposition = position * itemWidth;
        // 减去该Item还未移出屏幕的部分可得出滑动的距离
        int result = iposition - itemLeft;
        if (LogUtil.LOGGABLE && PRINT_HIGH_FREQUENCY_LOG) {
            LogUtil.e(TAG, "calRecyclerViewOffset,result:" + result + ",itemLeft:" + itemLeft);
        }
        return result;
    }

    private int getMidXOfParent() {
        return mSizeDefiner.getHistogramWithPx() >> 1;
    }

    private int calculateTimeViewOffset(HistogramBottomHighLightView listView) {
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        View c = listView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int bottom = c.getBottom();
        int left = c.getLeft();
        int top = c.getTop();
        int right = c.getRight();
        int result = -left + (firstVisiblePosition) * c.getWidth();
        if (LogUtil.LOGGABLE) {
            LogUtil.e(TAG,
                    "calCulateTimeOffset,result:" + result + ",top:" + top + ",right:" + right
                            + ",left:" + left + ",bottom:" + bottom + "，firstVisiblePosition："
                            + firstVisiblePosition);
        }
        return result;
    }

    private void calTimeViewOffsetOnInit(int index) {
        int mid = mSizeDefiner.getItemSelectWidthPx() * index + (mSizeDefiner.getItemSelectWidthPx()
                                                                         >> 1);
        int halfScreen = getMidXOfParent();
        if (mid == halfScreen) {
            mTimeViewInitOffset = 0;
        } else if (mid < halfScreen) {
            mTimeViewInitOffset = mid - halfScreen;
        } else {
            mTimeViewInitOffset = mid - halfScreen;
        }
    }

    /**
     * 滚动时-3的原因，请查看
     *
     * @see android.support.v7.widget.LinearSmoothScroller#calculateDxToMakeVisible(View, int)
     */
    public void onScrollStateChangedCommon(RecyclerView recyclerView, int newState) {
        if (LogUtil.LOGGABLE) {
            LogUtil.e(TAG,
                    "onScrollStateChangedCommon,newState:" + newState
                            + ",mIsUserClickCauseScroll:" + mIsUserClickCauseScroll);
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) { // 滑动停止
            if (mDataCallback == null || !mDataCallback.isDataReady()) {
                return;
            }
            if (mIsUserClickCauseScroll) {
                mIsUserClickCauseScroll = false;
                return;
            }
            int target = getTargetCenterItemIndex();
            // 滑动停止时index为target的item居中显示
            onScrollEnd(target);
        }
    }

    /**
     * 计算出滑动停止时应该居中的item的index
     *
     * @return
     */
    private int getTargetCenterItemIndex() {
        // 检测是否居中
        int firstPosition = getLayoutManager().findFirstVisibleItemPosition();
        int lastPosition = getLayoutManager().findLastVisibleItemPosition();
        int target = mSizeDefiner.getLeftEmptyItemCount();
        View itemView;
        int itemViewCenterXOffset = Integer.MAX_VALUE;
        if (firstPosition < 0 || lastPosition < 0) {
            return target;
        }
        int halfScreenX = getMidXOfParent();
        for (int i = firstPosition; i <= lastPosition; i++) {
            itemView = getLayoutManager().findViewByPosition(i);
            int midX = getMidX(itemView.getRight(), itemView.getLeft());
            int offset = midX - halfScreenX;
            if (Math.abs(offset) < itemViewCenterXOffset) {
                itemViewCenterXOffset = Math.abs(offset);
                target = i;
            }

        }
        int LeftEmptyItemCount = mSizeDefiner.getLeftEmptyItemCount();
        if (target < mSizeDefiner.getLeftEmptyItemCount()) {
            target = mSizeDefiner.getLeftEmptyItemCount();
        }
        if (target >= (mDataCallback.getTotalItemCount() - LeftEmptyItemCount)) {
            target = (mDataCallback.getTotalItemCount() - LeftEmptyItemCount) - 1;
        }
        return target;
    }

    private void initScrollListener() {
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                onScrollStateChangedCommon(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                onScrolledCommon(recyclerView, dx, dy);
            }
        };
        mHistogramView.clearOnScrollListeners();
        mHistogramView.addOnScrollListener(mOnScrollListener);
    }

    private void onScrolledCommon(RecyclerView recyclerView, int dx, int dy) {
        if (mDataCallback == null || !mDataCallback.isDataReady()) {
            return;
        }
        for (int i = 0; i < mDataCallback.getTotalItemCount(); i++) {
            View view1 = getLayoutManager().findViewByPosition(i);
            if (view1 != null) {
                int left = view1.getLeft();
                int right = view1.getRight();
                int mid = getMidX(left, right);
                updateItemAppearanceOnScroll(i, mid);
                changeWidthOnScroll(i, view1);
            }
        }
        moveHightLightViewOnHistogramScroll();
    }

    private void changeWidthOnScroll(int index, View view) {
        if (!mViewCallback.enableSelectItemMoreWidth()) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int leftMidBorder = (mSizeDefiner.getHistogramWithPx() >> 1) - (
                (mSizeDefiner.getItemWidthPx() + mSizeDefiner.getItemSelectWidthPx()) >> 1);
        int rightMidBorder = (mSizeDefiner.getHistogramWithPx() >> 1) + (
                (mSizeDefiner.getItemWidthPx() + mSizeDefiner.getItemSelectWidthPx()) >> 1);
        int midX = (view.getLeft() + view.getRight()) >> 1;
        if (midX >= leftMidBorder && midX <= rightMidBorder) {
            int delta = Math.abs(midX - (mSizeDefiner.getHistogramWithPx() >> 1));
            float a = 2f * (mSizeDefiner.getItemWidthPx() - mSizeDefiner.getItemSelectWidthPx()) / (
                    mSizeDefiner.getItemWidthPx() + mSizeDefiner.getItemSelectWidthPx());
            int x = new Float(a * delta).intValue();
            int w = x + mSizeDefiner.getItemSelectWidthPx();
            layoutParams.width = w;
        } else {
            int w = mSizeDefiner.getItemWidthPx();
            layoutParams.width = w;
        }
        if (LogUtil.LOGGABLE && PRINT_HIGH_FREQUENCY_LOG) {
            LogUtil.e(TAG,
                    "calculate_w,index:" + index + ",layoutParams.width:" + layoutParams.width);
        }
        view.setLayoutParams(layoutParams);
    }

    public void onClickHistogramItem(int index) {
        if (mCurCenterItemIndex == index) {
            mViewCallback.onCenterItemClick(index);
            return;
        }
        if (mDataCallback.isEmptyItem(index)) {
            return;
        }
        setSelectedItemCenterHorizontal(index);
        // 外部实现 ，数据请求等动作
        mDataCallback.onClickHistogramItem(mCurCenterItemIndex, index);
        mCurCenterItemIndex = index;
    }

    public void onScrollEnd(int index) {
        moveHightLightViewOnHistogramScroll(); // 停止滑动时底部高亮view兜底
        setSelectedItemCenterHorizontal(index); // 将对应柱子滑到中间
        // 外部实现 ，数据请求等动作
        mDataCallback.onClickHistogramItem(mCurCenterItemIndex, index);
        mCurCenterItemIndex = index;
    }

    /**
     * 主动设置居中
     *
     * @param index
     */
    public void setTargetItemCenterHorizontalImmediately(int index) {
        int offsetVal = mSizeDefiner.getHistogramScrollOffset();
        mViewCallback.onItemScrollToCenter(index);
        mCurCenterItemIndex = index;
        getLayoutManager().scrollToPositionWithOffset(index, -offsetVal);
        mHistogramAdapter.notifyDataSetChanged();
        moveHightLightViewOnHistogramScroll();
    }

    /**
     * 主动设置居中
     *
     * @param index
     */
    public void setSelectedItemCenterHorizontal(int index) {
        if (PRINT_HIGH_FREQUENCY_LOG) {
            if (isCenterHorizontal(index)) {
                LogUtil.e(TAG, "setSelectedItemCenterHorizontal,已经居中");
            } else {
                LogUtil.e(TAG, "setSelectedItemCenterHorizontal,当前未居中");
            }
        }
        int firstPosition = getLayoutManager().findFirstVisibleItemPosition();
        int lastPosition = getLayoutManager().findLastVisibleItemPosition();
        if (firstPosition < 0 || lastPosition < 0) {
            return;
        }
        if (PRINT_HIGH_FREQUENCY_LOG) {
            LogUtil.e(TAG,
                    "offsetttt_00000[firstPosition ,lastPosition]:" + "[" + firstPosition + ","
                            + lastPosition + "]" + ",index:" + index);
        }
        int moveOffset = 0;
        int halfScreen = getMidXOfParent();
        int midX = 0;
        if (index < firstPosition) {
            int left1 = mHistogramView.getChildAt(0).getLeft();
            int right1 = mHistogramView.getChildAt(0).getRight();
            midX = getMidX(left1, right1);
            if (mViewCallback.enableSelectItemMoreWidth()) {
                float offset = midX - halfScreen + 1.0f * mSizeDefiner.getItemSelectWidthPx() / 2
                        - 1.0f * mSizeDefiner.getItemWidthPx() / 2;
                offset = offset + (mSizeDefiner.getItemWidthPx() * (index - firstPosition));
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG, "offsetttt_66666:" + ((int) offset));
                }
                moveOffset = (int) offset;
            } else {
                int offset = halfScreen - midX + (mSizeDefiner.getItemWidthPx() * (firstPosition
                                                                                           - index));
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG, "offsetttt_55555:" + (-offset));
                }
                moveOffset = -offset;
            }
        } else if (index <= lastPosition) {
            int left1 = mHistogramView.getChildAt(index - firstPosition).getLeft();
            int right1 = mHistogramView.getChildAt(index - firstPosition).getRight();
            midX = getMidX(left1, right1);
            if (mViewCallback.enableSelectItemMoreWidth()) {
                float delta = 1.0f * mSizeDefiner.getItemSelectWidthPx() / 2
                        + 1.0f * mSizeDefiner.getItemWidthPx() / 2;
                float offset;
                if (midX == halfScreen) {
                    if (PRINT_HIGH_FREQUENCY_LOG) {
                        LogUtil.e(TAG, "offsetttt_正好居中了");
                    }
                    offset = 0f;
                } else if (midX > halfScreen) {
                    if (midX - halfScreen >= delta) {
                        if (PRINT_HIGH_FREQUENCY_LOG) {
                            LogUtil.e(TAG, "offsetttt_在中线右侧delta外");
                        }
                        offset = midX - halfScreen - 1.0f * mSizeDefiner.getItemSelectWidthPx() / 2
                                + 1.0f * mSizeDefiner.getItemWidthPx() / 2;
                    } else {
                        if (PRINT_HIGH_FREQUENCY_LOG) {
                            LogUtil.e(TAG, "offsetttt_在中线右侧delta内");
                        }
                        offset = (1.0f * mSizeDefiner.getItemWidthPx() * 2) / (
                                mSizeDefiner.getItemSelectWidthPx()
                                        + mSizeDefiner.getItemWidthPx()) * (midX - halfScreen);
                    }
                } else {
                    if (halfScreen - midX >= delta) {
                        if (PRINT_HIGH_FREQUENCY_LOG) {
                            LogUtil.e(TAG, "offsetttt_在中线左侧delta外");
                        }
                        offset = midX - halfScreen + 1.0f * mSizeDefiner.getItemSelectWidthPx() / 2
                                - 1.0f * mSizeDefiner.getItemWidthPx() / 2;
                    } else {
                        if (PRINT_HIGH_FREQUENCY_LOG) {
                            LogUtil.e(TAG, "offsetttt_在中线左侧delta内");
                        }
                        offset = (1.0f * mSizeDefiner.getItemWidthPx() * 2) / (
                                mSizeDefiner.getItemSelectWidthPx()
                                        + mSizeDefiner.getItemWidthPx()) * (midX - halfScreen);
                    }
                }
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG,
                            "offsetttt_11111:" + (int) offset
                                    + ",offset:" + offset
                                    + ",Math.round(offset):" + Math.round(offset)
                                    + ",midX:" + midX
                                    + ",halfScreen:" + halfScreen);
                }
                moveOffset = Math.round(offset);
            } else {
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG, "offsetttt_22222:" + (midX - halfScreen));
                }
                moveOffset = midX - halfScreen;
            }
        } else {
            int left1 = mHistogramView.getChildAt(lastPosition - firstPosition).getLeft();
            int right1 = mHistogramView.getChildAt(lastPosition - firstPosition).getRight();
            midX = getMidX(left1, right1);
            if (mViewCallback.enableSelectItemMoreWidth()) {
                float offset = midX - halfScreen - 1.0f * mSizeDefiner.getItemSelectWidthPx() / 2
                        + 1.0f * mSizeDefiner.getItemWidthPx() / 2;
                offset = offset + (mSizeDefiner.getItemWidthPx() * (index - lastPosition));
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG, "offsetttt_33333:" + (int) offset);
                }
                moveOffset = (int) offset;
            } else {
                int offset =
                        midX - halfScreen + (mSizeDefiner.getItemWidthPx() * (index - lastPosition));
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG, "offsetttt_44444:" + (offset));
                }
                moveOffset = offset;
            }
        }
        if (LogUtil.LOGGABLE) {
            LogUtil.e(TAG,
                    "setSelectedItemCenterHorizontal,index:" + index + ",moveOffset:" + moveOffset
                            + ",midX:" + midX);
        }
        if (moveOffset != 0) {
            if (Math.abs(moveOffset) < 10) { // 移动距离小于10px直接移动
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG, "offsetttt_直接滑动:" + Math.abs(moveOffset));
                }
                setTargetItemCenterHorizontalImmediately(index);
            } else {
                if (PRINT_HIGH_FREQUENCY_LOG) {
                    LogUtil.e(TAG, "offsetttt_线性滑动:" + Math.abs(moveOffset));
                }
                mHistogramView.smoothScrollBy(moveOffset, 0);
            }
        }
    }

    private boolean isCenterHorizontal(int index) {
        return isCenterHorizontal(index, 3);
    }

    /**
     * @param index
     *         被选中的item index
     *
     * @return 被选中的item是否已经居中
     */
    private boolean isCenterHorizontal(int index, int level) {
        int offset = getTargetItemOffset2CenterHorizontal(index);
        if (LogUtil.LOGGABLE) {
            LogUtil.e(TAG,
                    "isCenterHorizontal,index:" + index + ",offset:" + offset + ",level:" + level);
        }
        if (offset < 0) {
            return false;
        }
        if (offset <= level) {
            return true;
        }
        return false;
    }

    private int getTargetItemOffset2CenterHorizontal(int index) {
        if (mHistogramView == null || getLayoutManager().getChildCount() <= 0) {
            if (LogUtil.LOGGABLE) {
                LogUtil.e(TAG, "getTargetItemOffset2CenterHorizontal,Oops!!! what's your problem?");
            }
            return -1;
        }
        int firstPosition = getLayoutManager().findFirstVisibleItemPosition();
        View view = mHistogramView.getChildAt(index - firstPosition);
        if (view == null) {
            if (LogUtil.LOGGABLE) {
                LogUtil.e(TAG, "getTargetItemOffset2CenterHorizontal,view is null,index:" + index);
            }
            return -1;
        }
        int left1 = view.getLeft();
        int right1 = view.getRight();
        int midX = getMidX(left1, right1);
        int halfScreen = getMidXOfParent();
        if (LogUtil.LOGGABLE) {
            LogUtil.e(TAG,
                    "getTargetItemOffset2CenterHorizontal,midX:" + midX
                            + ",halfScreen:" + halfScreen
                            + ",offset:" + Math.abs(midX - halfScreen));
        }
        return Math.abs(midX - halfScreen);
    }

    private int getMidX(int leftX, int rightX) {
        return ((rightX + leftX) >> 1);
    }

    public interface ViewCallback {
        LinearLayoutManager getLayoutManager();

        Context getContext();

        /**
         * 点击了居中的柱子
         *
         * @param index
         */
        void onCenterItemClick(int index);

        /**
         * 使对应柱子居中
         *
         * @param index
         */
        void onItemScrollToCenter(int index);

        RecyclerView getHistogramView();

        HistogramBaseAdapter getHistogramAdapter();

        HistogramBottomHighLightView getHistogramBottomHighLightView();

        HistogramBottomViewBaseAdapter getHistogramBottomHighLightViewAdapter();

        BaseSizeDefiner getSizeDefiner();

        boolean enableSelectItemMoreWidth();
    }

    public interface DataCallback<T> {
        /**
         * 是否为两侧占位的柱子
         *
         * @param index
         *
         * @return
         */
        boolean isEmptyItem(int index);

        int getTotalItemCount();

        /**
         * 数据集
         *
         * @return
         */
        List<T> getDataList();

        boolean isDataReady();

        void onClickHistogramItem(int lastCenterIndex, int index);
    }
}

package com.baidu.histogram.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public abstract class HistogramLinearLayoutManager extends LinearLayoutManager {

    public HistogramLinearLayoutManager(Context context) {
        super(context);
    }

    public HistogramLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public HistogramLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        MyLinearSmoothScroller linearSmoothScroller =
                new MyLinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    abstract float getUserDefineTimeRadio();

    public class MyLinearSmoothScroller extends LinearSmoothScroller {

        public MyLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            if (getUserDefineTimeRadio() > 0f) {
                return super.calculateSpeedPerPixel(displayMetrics) * getUserDefineTimeRadio();
            }
            return super.calculateSpeedPerPixel(displayMetrics);
        }
    }
}

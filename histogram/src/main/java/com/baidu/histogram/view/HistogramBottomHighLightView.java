package com.baidu.histogram.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HistogramBottomHighLightView extends HorizontalListView {
    private boolean isRect = false;
    private Rect mClipRect = new Rect();

    public HistogramBottomHighLightView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isRect() {
        return isRect;
    }

    public void setRect(boolean rect) {
        isRect = rect;
    }

    public void updateRect(Rect rect) {
        mClipRect = rect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isRect) {
            canvas.clipRect(mClipRect);
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // return super.dispatchTouchEvent(ev);
        return false;
    }
}

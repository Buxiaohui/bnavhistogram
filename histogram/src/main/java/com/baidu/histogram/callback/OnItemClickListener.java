package com.baidu.histogram.callback;

import android.view.View;

public interface OnItemClickListener<T> {
    void onItemClick(View view, int index, T data);
}

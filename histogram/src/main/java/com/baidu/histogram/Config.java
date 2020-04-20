package com.baidu.histogram;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

public class Config {
    public static boolean DEBUG_UI = BuildConfig.DEBUG;
    @IntDef({ItemState.EMPTY, ItemState.SELECT, ItemState.UN_SELECT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemState {
        int EMPTY = 0;
        int SELECT = 1;
        int UN_SELECT = 2;
    }

    @IntDef({RequestState.INVALID, RequestState.LOADING, RequestState.FAIL, RequestState.SUCCESS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RequestState {
        int INVALID = -1;
        int LOADING = 0;
        int FAIL = 1;
        int SUCCESS = 2;
    }

}

package com.baidu.bnavhistogram.data;

import java.util.Random;

import com.baidu.histogram.data.BaseItemData;

public class HistogramItem extends BaseItemData {
    private String str;

    public String getStr() {
        return "11:30";
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public int getState() {
        return 0;
    }

    @Override
    public double getHeight() {
        return 30d + new Random().nextInt(100);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HistogramItem{");
        sb.append("str='").append(str).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

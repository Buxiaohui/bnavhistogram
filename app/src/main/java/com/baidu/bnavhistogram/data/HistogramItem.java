package com.baidu.bnavhistogram.data;

import java.util.Random;

import com.baidu.histogram.data.BaseItemData;

public class HistogramItem extends BaseItemData {
    private String str;
    private boolean isSelect;
    private boolean isHighLight;

    public boolean isHighLight() {
        return isHighLight;
    }

    public void setHighLight(boolean highLight) {
        isHighLight = highLight;
    }

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

    private float height;

    @Override
    public double getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HistogramItem{");
        sb.append("str='").append(str).append('\'');
        sb.append(", isSelect=").append(isSelect);
        sb.append(", isHighLight=").append(isHighLight);
        sb.append('}');
        return sb.toString();
    }
}

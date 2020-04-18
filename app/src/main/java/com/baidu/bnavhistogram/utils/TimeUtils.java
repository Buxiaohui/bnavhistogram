package com.baidu.bnavhistogram.utils;

public class TimeUtils {
    public static String getTimeStamp(int index) {
        int min_15_sec = 60 * 15;
        int reminder = index % 48;
        int secTotal = reminder * min_15_sec;
        int hour = secTotal / (60 * 60);
        int min = secTotal % (60 * 60) / 60;
        return getFormatStr(hour) + ":" + getFormatStr(min);
    }

    public  static String getFormatStr(int num) {
        if (num <= 9) {
            return "0" + num;
        }
        return String.valueOf(num);
    }
}

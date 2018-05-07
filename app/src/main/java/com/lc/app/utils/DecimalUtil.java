package com.lc.app.utils;

import java.text.NumberFormat;

/**
 * Created by Orange on 18-5-7.
 * Email:addskya@163.com
 */

public class DecimalUtil {

    public static String valueOf(float number) {
        return getNumber(number);
    }

    public static String getNumber(float number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        return format.format(number);
    }
}

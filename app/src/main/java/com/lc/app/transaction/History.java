package com.lc.app.transaction;

import android.databinding.BaseObservable;

/**
 * Created by Orange on 18-3-29.
 * Email:addskya@163.com
 */

public class History extends BaseObservable {

    private String from;
    private String to;

    private float value;
    private long date;


    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public float getValue() {
        return value;
    }

    public long getDate() {
        return date;
    }
}

package com.lc.app.code;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class RatioFrameLayout extends ViewGroup {

    private final static int RATIO_W = 9;
    private final static int RATIO_H = 16;

    private int mAdjustH = 0;
    private int mAdjustW = 0;

    public RatioFrameLayout(Context context) {
        super(context);
    }

    public RatioFrameLayout(Context context,
                            AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioFrameLayout(Context context,
                            AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatioFrameLayout(Context context,
                            AttributeSet attrs,
                            int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int ratioW = width;
        int ratioH = height;
        if (width * RATIO_H > height * RATIO_W) {
            ratioH = width * RATIO_H / RATIO_W;
            mAdjustW = 0;
            mAdjustH = (ratioH - height) / 2;
        } else {
            ratioW = height * RATIO_W / RATIO_H;
            mAdjustW = (ratioW - width) / 2;
            mAdjustH = 0;
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        measureChildren(MeasureSpec.makeMeasureSpec(ratioW, widthMode),
                MeasureSpec.makeMeasureSpec(ratioH, heightMode));
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.layout(l - mAdjustW, t - mAdjustH, r + mAdjustW, b + mAdjustH);
        }
    }
}

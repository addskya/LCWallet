package com.lc.app.ui;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.lc.app.R;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class RecyclerView extends android.support.v7.widget.RecyclerView {

    public RecyclerView(@NonNull Context context) {
        super(context);
    }

    public RecyclerView(@NonNull Context context,
                        @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerView);
        initItemDecoration(a);
        a.recycle();
    }


    private void initItemDecoration(TypedArray array) {
        Drawable divider = array.getDrawable(R.styleable.RecyclerView_listDivider);
        if (divider == null) {
            return;
        }

        int paddingStart = array.getDimensionPixelOffset(
                R.styleable.RecyclerView_dividerPaddingStart,0);
        int paddingEnd = array.getDimensionPixelOffset(
                R.styleable.RecyclerView_dividerPaddingEnd, 0);
        int orientation = array.getInt(
                R.styleable.RecyclerView_orientation, LinearLayoutManager.VERTICAL);

        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),orientation);
        itemDecoration.setDrawable(divider);
        itemDecoration.setOrientation(orientation);
        addItemDecoration(itemDecoration);
    }


}

package com.lc.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lc.app.R;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */

public class SimpleImageView extends SimpleDraweeView {

    private static final String TAG = "SimpleImageView";

    public SimpleImageView(Context context) {
        super(context);
    }

    public SimpleImageView(Context context,
                           AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageView(Context context,
                           AttributeSet attrs,
                           int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(
                    attrs,
                    R.styleable.SimpleImageView);
            try {
                if (ta.hasValue(R.styleable.SimpleImageView_src)) {
                    setSrc(ta.getResourceId(R.styleable.SimpleImageView_src, 0));
                }
            } finally {
                ta.recycle();
            }
        }

    }

    public void setSrc(@DrawableRes int srcResId) {
        if (srcResId == 0) {
            return;
        }
        setController(getControllerImpl(srcResId));
    }

    private DraweeController getControllerImpl(@DrawableRes int srcResId) {
        Uri uri = Uri.parse("res:///" + srcResId);
        return Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
    }
}

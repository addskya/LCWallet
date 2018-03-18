package com.lc.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.lc.app.R;

/**
 * Created by Orange on 18-3-18.
 * Email:addskya@163.com
 */
public class PasswordView extends FrameLayout {
    private static final String TAG = "PasswordView";

    private EditText mEditText;

    public PasswordView(@NonNull Context context,
                        @Nullable AttributeSet attrs) {
        super(context, attrs);
        loadViews();
        initAttrs(context, attrs);
    }

    public PasswordView(@NonNull Context context,
                        @Nullable AttributeSet attrs,
                        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadViews();
        initAttrs(context, attrs);
    }

    private void loadViews() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.password_view, this, true);
        mEditText = findViewById(R.id.password);

        CheckBox visible = findViewById(R.id.password_visible);
        if (visible != null) {
            visible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    toggle(isChecked);
                }
            });
            // 与Check状态同步
            toggle(visible.isChecked());
        }
    }

    private void initAttrs(@NonNull Context context,
                           @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PasswordView);
        if (array.hasValue(R.styleable.PasswordView_hint)) {
            mEditText.setHint(array.getString(R.styleable.PasswordView_hint));
        }

        array.recycle();
    }

    /**
     * 获取输入的密码文本
     *
     * @return 输入的密码文本
     */
    public CharSequence getText() {
        return mEditText == null ? "" : mEditText.getText();
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if (mEditText != null) {
            mEditText.addTextChangedListener(textWatcher);
        }
    }

    private void toggle(boolean checked) {
        if (mEditText == null) {
            return;
        }
        mEditText.setInputType(
                InputType.TYPE_CLASS_TEXT | (checked ?
                        InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                        InputType.TYPE_TEXT_VARIATION_PASSWORD));

        // 将光标移到文本最后
        int length = mEditText.getText().length();
        mEditText.setSelection(length);
    }
}

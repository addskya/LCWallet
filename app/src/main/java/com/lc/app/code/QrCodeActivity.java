package com.lc.app.code;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.lc.app.BaseActivity;


/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 * 扫描二维码界面
 */
public class QrCodeActivity extends BaseActivity {


    /**
     * launch the QrCode Activity Ui
     *
     * @param activity    the activity context
     * @param requestCode the requestCode
     */
    public static void intentTo(@NonNull Activity activity, int requestCode) {
        //Intent intent = new Intent(activity, QrCodeActivity.class);
        //activity.startActivityForResult(intent, requestCode);


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}

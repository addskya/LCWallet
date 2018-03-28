package com.lc.app.code;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.lc.app.BaseDialog;
import com.lc.app.R;
import com.lc.app.model.Account;
import com.lc.app.ui.SimpleImageView;

import java.io.File;

/**
 * Created by Orange on 18-3-17.
 * Email:addskya@163.com
 * 显示二维码对话框
 */
public class QrCodeDialog extends BaseDialog {

    private static final String TAG = "QrCodeDialog";
    private SimpleImageView mQrCodeImageView;
    private TextView mAccountName;

    private QrCodeDialog(@NonNull Context context) {
        super(context);
    }

    /**
     * Display the Account QrCode
     *
     * @param context the Activity context
     * @param account the account
     */
    public static void intentTo(@NonNull Context context,
                                @NonNull final Account account) {
        final QrCodeDialog dialog = new QrCodeDialog(context);
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.setAccount(account);
            }
        });
        dialog.show();
    }

    @Override
    protected View onCreateView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.dialog_qr_code,
                null, false);
        mQrCodeImageView = view.findViewById(R.id.qr_img);
        mAccountName = view.findViewById(R.id.account_name);
        return view;
    }

    private void setAccount(@NonNull Account account) {
        CharSequence content = account.getRealAddress();
        Log.i(TAG, "content:" + content);

        File outputFile = new File(getBaseContext().getCacheDir(), "account_qr.png");
        boolean success = QrCodeUtil.createQRImage(content, 500, 500, outputFile);
        if (success) {
            final Uri uri = Uri.fromFile(outputFile);
            mQrCodeImageView.setImageURI(uri);
            mAccountName.setText(account.getWalletName());
        }
    }
}
